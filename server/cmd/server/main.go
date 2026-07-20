// Command server runs the Fingers on Buzzers game server.
package main

import (
	"context"
	"errors"
	"io"
	"log/slog"
	"net"
	"net/http"
	"os"
	"os/signal"
	"strings"
	"syscall"
	"time"

	"github.com/James-Garden/fingers-on-buzzers/server/internal/ws"
)

const (
	defaultAddr    = ":8080"
	shutdownGrace  = 10 * time.Second
	headerTimeout  = 10 * time.Second
	defaultOrigins = "localhost:5173,127.0.0.1:5173"
)

func main() {
	log := newLogger()
	if err := run(log); err != nil {
		log.Error("server failed", "err", err)
		os.Exit(1)
	}
	log.Info("server stopped")
}

// run holds the body so main's only job is the exit code: os.Exit skips defers.
func run(log *slog.Logger) error {
	// Request contexts derive from BaseContext, so cancelling this cancels every
	// in-flight websocket Read and Write.
	rootCtx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()

	hub := ws.NewHub(log)
	addr := envOr("ADDR", defaultAddr)

	srv := &http.Server{
		Addr:    addr,
		Handler: newMux(hub, log, allowedOrigins()),
		// No ReadTimeout or WriteTimeout: their deadlines survive the websocket
		// hijack and would kill live connections mid-session.
		ReadHeaderTimeout: headerTimeout,
		BaseContext:       func(net.Listener) context.Context { return rootCtx },
	}

	serveErr := make(chan error, 1)
	go func() {
		log.Info("server listening", "addr", addr)
		if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
			serveErr <- err
			return
		}
		serveErr <- nil
	}()

	select {
	case err := <-serveErr:
		if err != nil {
			return err
		}
	case <-rootCtx.Done():
		log.Info("shutdown signal received")
	}

	shutCtx, cancel := context.WithTimeout(context.Background(), shutdownGrace)
	defer cancel()

	// Hub first: http.Server.Shutdown does not wait for hijacked connections.
	if err := hub.Shutdown(shutCtx); err != nil {
		log.Warn("hub shutdown incomplete", "err", err)
	}
	if err := srv.Shutdown(shutCtx); err != nil {
		log.Warn("http shutdown incomplete", "err", err)
	}
	return nil
}

func newMux(hub *ws.Hub, log *slog.Logger, origins []string) *http.ServeMux {
	mux := http.NewServeMux()
	mux.HandleFunc("GET /healthz", func(w http.ResponseWriter, _ *http.Request) {
		w.Header().Set("Content-Type", "text/plain; charset=utf-8")
		w.WriteHeader(http.StatusOK)
		_, _ = io.WriteString(w, "ok")
	})
	mux.Handle("/ws", ws.NewHandler(hub, log, origins))
	return mux
}

func newLogger() *slog.Logger {
	var level slog.Level
	if err := level.UnmarshalText([]byte(envOr("LOG_LEVEL", "info"))); err != nil {
		level = slog.LevelInfo
	}
	log := slog.New(slog.NewTextHandler(os.Stdout, &slog.HandlerOptions{Level: level}))
	slog.SetDefault(log)
	return log
}

// allowedOrigins must carry Vite's port, since patterns match host plus port.
// In production the client is served by this binary, so it is same-origin.
func allowedOrigins() []string {
	raw := envOr("WS_ALLOWED_ORIGINS", defaultOrigins)
	var out []string
	for _, p := range strings.Split(raw, ",") {
		if p = strings.TrimSpace(p); p != "" {
			out = append(out, p)
		}
	}
	return out
}

func envOr(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}
