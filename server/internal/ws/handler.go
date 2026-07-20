package ws

import (
	"context"
	"errors"
	"io"
	"log/slog"
	"net/http"
	"sync/atomic"
	"time"

	"github.com/coder/websocket"
)

// Handler upgrades HTTP requests and registers them with the hub.
type Handler struct {
	hub  *Hub
	log  *slog.Logger
	opts *websocket.AcceptOptions

	nextID atomic.Uint64
}

// originPatterns are matched against the Origin host including port; empty
// means same-origin only.
func NewHandler(hub *Hub, log *slog.Logger, originPatterns []string) *Handler {
	return &Handler{
		hub: hub,
		log: log,
		opts: &websocket.AcceptOptions{
			OriginPatterns: originPatterns,
		},
	}
}

func (h *Handler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	conn, err := websocket.Accept(w, r, h.opts)
	if err != nil {
		// Accept has already written the response; w must not be touched.
		h.log.Warn("websocket upgrade failed", "err", err, "remote_addr", r.RemoteAddr)
		return
	}
	conn.SetReadLimit(readLimit)

	id := h.nextID.Add(1)
	log := h.log.With("conn_id", id)
	c := newClient(id, conn, h.hub, log)

	if !h.hub.add(c) {
		_ = conn.Close(websocket.StatusGoingAway, "server shutting down")
		return
	}
	defer func() {
		// Deregister first, so the notice only reaches live clients.
		h.hub.remove(c)
		h.hub.announceDeparture(id)
	}()
	defer c.kill()

	// Cancelling on return kills the write pump with the handler.
	ctx, cancel := context.WithCancel(r.Context())
	defer cancel()

	started := time.Now()
	log.Info("connection accepted",
		"remote_addr", r.RemoteAddr,
		"origin", r.Header.Get("Origin"),
		"clients", h.hub.Count(),
	)

	go c.writePump(ctx)
	err = c.readLoop(ctx)

	status := websocket.CloseStatus(err)
	attrs := []any{
		"duration_ms", time.Since(started).Milliseconds(),
		"close_code", int(status),
	}
	if normalClose(err, status) {
		log.Info("connection closed", attrs...)
		_ = conn.Close(websocket.StatusNormalClosure, "")
		return
	}
	log.Error("connection failed", append(attrs, "err", err)...)
}

// normalClose keeps ordinary disconnects off the error log.
func normalClose(err error, status websocket.StatusCode) bool {
	switch status {
	case websocket.StatusNormalClosure, websocket.StatusGoingAway, websocket.StatusNoStatusRcvd:
		return true
	}
	return errors.Is(err, io.EOF) || errors.Is(err, context.Canceled)
}
