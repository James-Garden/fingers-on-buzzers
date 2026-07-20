package ws

import (
	"bytes"
	"context"
	"fmt"
	"io"
	"log/slog"
	"net/http"
	"net/http/httptest"
	"strings"
	"sync"
	"testing"
	"time"

	"github.com/coder/websocket"
)

const (
	testTimeout = 5 * time.Second
	// Exceeds the library's 5s close-handshake cap, so a wedged peer fails
	// loudly rather than as a timeout.
	shutdownTimeout = 15 * time.Second
)

var testOrigins = []string{"localhost:5173", "127.0.0.1:5173"}

// srv.Close blocks on hijacked connections, so it is registered first and
// therefore runs last — cleanups are LIFO.
func newTestServer(t *testing.T) (*httptest.Server, *Hub) {
	t.Helper()

	log := slog.New(slog.NewTextHandler(io.Discard, nil))
	hub := NewHub(log)
	srv := httptest.NewServer(NewHandler(hub, log, testOrigins))

	t.Cleanup(srv.Close)
	t.Cleanup(func() {
		ctx, cancel := context.WithTimeout(context.Background(), shutdownTimeout)
		defer cancel()
		_ = hub.Shutdown(ctx)
	})

	return srv, hub
}

func wsURL(srv *httptest.Server) string {
	return "ws" + strings.TrimPrefix(srv.URL, "http")
}

func dial(t *testing.T, srv *httptest.Server) *websocket.Conn {
	t.Helper()
	ctx, cancel := context.WithTimeout(context.Background(), testTimeout)
	defer cancel()

	conn, _, err := websocket.Dial(ctx, wsURL(srv), nil)
	if err != nil {
		t.Fatalf("dial: %v", err)
	}
	t.Cleanup(func() { _ = conn.CloseNow() })
	return conn
}

func send(t *testing.T, ctx context.Context, conn *websocket.Conn, s string) {
	t.Helper()
	if err := conn.Write(ctx, websocket.MessageText, []byte(s)); err != nil {
		t.Fatalf("write %q: %v", s, err)
	}
}

func recvText(t *testing.T, ctx context.Context, conn *websocket.Conn) string {
	t.Helper()
	typ, data, err := conn.Read(ctx)
	if err != nil {
		t.Fatalf("read: %v", err)
	}
	if typ != websocket.MessageText {
		t.Fatalf("message type = %v, want text", typ)
	}
	return string(data)
}

func testCtx(t *testing.T) context.Context {
	t.Helper()
	ctx, cancel := context.WithTimeout(context.Background(), testTimeout)
	t.Cleanup(cancel)
	return ctx
}

// eventually covers server-side state that settles after a client-side call
// returns.
func eventually(t *testing.T, cond func() bool, msg string) {
	t.Helper()
	deadline := time.Now().Add(2 * time.Second)
	for time.Now().Before(deadline) {
		if cond() {
			return
		}
		time.Sleep(10 * time.Millisecond)
	}
	t.Fatalf("condition never held: %s", msg)
}

func TestEchoRoundTrip(t *testing.T) {
	srv, _ := newTestServer(t)
	ctx := testCtx(t)
	conn := dial(t, srv)

	send(t, ctx, conn, "hello")
	if got, want := recvText(t, ctx, conn), "echo: hello"; got != want {
		t.Errorf("got %q, want %q", got, want)
	}
}

func TestPingCommand(t *testing.T) {
	srv, _ := newTestServer(t)
	ctx := testCtx(t)
	conn := dial(t, srv)

	send(t, ctx, conn, "ping")
	if got, want := recvText(t, ctx, conn), "pong"; got != want {
		t.Errorf("got %q, want %q", got, want)
	}
}

func TestTimeCommand(t *testing.T) {
	srv, _ := newTestServer(t)
	ctx := testCtx(t)
	conn := dial(t, srv)

	send(t, ctx, conn, "time")
	got := recvText(t, ctx, conn)

	stamp, ok := strings.CutPrefix(got, "time ")
	if !ok {
		t.Fatalf("got %q, want a %q prefix", got, "time ")
	}
	parsed, err := time.Parse(time.RFC3339Nano, stamp)
	if err != nil {
		t.Fatalf("parse %q: %v", stamp, err)
	}
	if d := time.Since(parsed); d > time.Minute || d < -time.Minute {
		t.Errorf("timestamp %v is %v away from now", parsed, d)
	}
}

func TestWhoamiCommand(t *testing.T) {
	srv, _ := newTestServer(t)
	ctx := testCtx(t)
	conn := dial(t, srv)

	send(t, ctx, conn, "whoami")
	if got := recvText(t, ctx, conn); !strings.HasPrefix(got, "whoami ") {
		t.Errorf("got %q, want a %q prefix", got, "whoami ")
	}
}

func TestBinaryEcho(t *testing.T) {
	srv, _ := newTestServer(t)
	ctx := testCtx(t)
	conn := dial(t, srv)

	payload := []byte{0x00, 0x01, 0xfe, 0xff}
	if err := conn.Write(ctx, websocket.MessageBinary, payload); err != nil {
		t.Fatalf("write: %v", err)
	}

	typ, got, err := conn.Read(ctx)
	if err != nil {
		t.Fatalf("read: %v", err)
	}
	if typ != websocket.MessageBinary {
		t.Errorf("message type = %v, want binary", typ)
	}
	if !bytes.Equal(got, payload) {
		t.Errorf("got %v, want %v", got, payload)
	}
}

func TestBroadcastFanout(t *testing.T) {
	srv, hub := newTestServer(t)
	ctx := testCtx(t)

	const clients = 3
	conns := make([]*websocket.Conn, clients)
	for i := range conns {
		conns[i] = dial(t, srv)
	}
	eventually(t, func() bool { return hub.Count() == clients }, "all clients registered")

	// Read concurrently so no client's message is missed while awaiting another.
	results := make([]chan string, clients)
	for i := range conns {
		results[i] = make(chan string, 1)
		go func(conn *websocket.Conn, out chan<- string) {
			_, data, err := conn.Read(ctx)
			if err != nil {
				close(out)
				return
			}
			out <- string(data)
		}(conns[i], results[i])
	}

	send(t, ctx, conns[0], "broadcast hi")

	for i, ch := range results {
		select {
		case got, ok := <-ch:
			if !ok {
				t.Fatalf("client %d: read failed", i)
			}
			if !strings.HasSuffix(got, " hi") || !strings.HasPrefix(got, "broadcast ") {
				t.Errorf("client %d: got %q, want %q...%q", i, got, "broadcast ", " hi")
			}
		case <-ctx.Done():
			t.Fatalf("client %d: timed out waiting for broadcast", i)
		}
	}
}

func TestDisconnectBroadcast(t *testing.T) {
	srv, hub := newTestServer(t)
	ctx := testCtx(t)

	watcher := dial(t, srv)
	leaver := dial(t, srv)
	eventually(t, func() bool { return hub.Count() == 2 }, "both clients registered")

	// Learn the leaver's id so the notice can be matched exactly.
	send(t, ctx, leaver, "whoami")
	var leaverID uint64
	if _, err := fmt.Sscanf(recvText(t, ctx, leaver), "whoami %d", &leaverID); err != nil {
		t.Fatalf("parse whoami: %v", err)
	}

	got := make(chan string, 1)
	go func() {
		_, data, err := watcher.Read(ctx)
		if err != nil {
			close(got)
			return
		}
		got <- string(data)
	}()

	if err := leaver.Close(websocket.StatusNormalClosure, ""); err != nil {
		t.Fatalf("close: %v", err)
	}

	select {
	case msg, ok := <-got:
		if !ok {
			t.Fatal("watcher read failed")
		}
		if want := fmt.Sprintf("left %d", leaverID); msg != want {
			t.Errorf("got %q, want %q", msg, want)
		}
	case <-ctx.Done():
		t.Fatal("timed out waiting for the departure notice")
	}

	eventually(t, func() bool { return hub.Count() == 1 }, "leaver deregistered")
}

func TestConcurrentClients(t *testing.T) {
	srv, hub := newTestServer(t)
	ctx := testCtx(t)

	const (
		clients  = 20
		messages = 10
	)

	conns := make([]*websocket.Conn, clients)
	for i := range conns {
		conns[i] = dial(t, srv)
	}
	eventually(t, func() bool { return hub.Count() == clients }, "all clients registered")

	var wg sync.WaitGroup
	errs := make(chan error, clients)

	for i, conn := range conns {
		wg.Add(1)
		go func(i int, conn *websocket.Conn) {
			defer wg.Done()
			for j := range messages {
				want := fmt.Sprintf("c%d-m%d", i, j)
				if err := conn.Write(ctx, websocket.MessageText, []byte(want)); err != nil {
					errs <- fmt.Errorf("client %d write %d: %w", i, j, err)
					return
				}
				// A single connection guarantees ordering.
				_, data, err := conn.Read(ctx)
				if err != nil {
					errs <- fmt.Errorf("client %d read %d: %w", i, j, err)
					return
				}
				if got := string(data); got != "echo: "+want {
					errs <- fmt.Errorf("client %d: got %q, want %q", i, got, "echo: "+want)
					return
				}
			}
		}(i, conn)
	}

	wg.Wait()
	close(errs)
	for err := range errs {
		t.Error(err)
	}
}

func TestCleanClose(t *testing.T) {
	srv, hub := newTestServer(t)
	ctx := testCtx(t)
	conn := dial(t, srv)

	// Round-trip once so the connection is definitely registered.
	send(t, ctx, conn, "ping")
	recvText(t, ctx, conn)

	if err := conn.Close(websocket.StatusNormalClosure, ""); err != nil {
		t.Fatalf("close: %v", err)
	}
	eventually(t, func() bool { return hub.Count() == 0 }, "client deregistered after clean close")
}

func TestServerShutdownClosesClients(t *testing.T) {
	srv, hub := newTestServer(t)
	ctx := testCtx(t)
	conn := dial(t, srv)

	send(t, ctx, conn, "ping")
	recvText(t, ctx, conn)

	// A client parked in Read replies to the close frame immediately, as a real
	// client does, keeping the handshake fast.
	readErr := make(chan error, 1)
	go func() {
		_, _, err := conn.Read(ctx)
		readErr <- err
	}()

	shutCtx, cancel := context.WithTimeout(context.Background(), shutdownTimeout)
	defer cancel()
	if err := hub.Shutdown(shutCtx); err != nil {
		t.Fatalf("shutdown: %v", err)
	}

	select {
	case err := <-readErr:
		if err == nil {
			t.Fatal("read succeeded after shutdown, want error")
		}
		if status := websocket.CloseStatus(err); status != websocket.StatusGoingAway {
			t.Errorf("close status = %v, want %v", status, websocket.StatusGoingAway)
		}
	case <-ctx.Done():
		t.Fatal("timed out waiting for the client to observe the close")
	}
}

func TestShutdownRejectsNewConnections(t *testing.T) {
	srv, hub := newTestServer(t)

	shutCtx, cancel := context.WithTimeout(context.Background(), shutdownTimeout)
	defer cancel()
	if err := hub.Shutdown(shutCtx); err != nil {
		t.Fatalf("shutdown: %v", err)
	}

	ctx := testCtx(t)
	conn, _, err := websocket.Dial(ctx, wsURL(srv), nil)
	if err != nil {
		return // Rejected at handshake, which is also acceptable.
	}
	defer func() { _ = conn.CloseNow() }()

	if _, _, err := conn.Read(ctx); err == nil {
		t.Fatal("read succeeded on a post-shutdown connection, want error")
	}
	if hub.Count() != 0 {
		t.Errorf("hub count = %d, want 0", hub.Count())
	}
}

func TestReadLimitExceeded(t *testing.T) {
	srv, hub := newTestServer(t)
	ctx := testCtx(t)
	conn := dial(t, srv)

	oversized := bytes.Repeat([]byte("a"), readLimit+1)
	// The write may land before the server closes, so assert on the read.
	_ = conn.Write(ctx, websocket.MessageText, oversized)

	if _, _, err := conn.Read(ctx); err == nil {
		t.Fatal("read succeeded after exceeding the read limit, want error")
	} else if status := websocket.CloseStatus(err); status != websocket.StatusMessageTooBig {
		t.Errorf("close status = %v, want %v", status, websocket.StatusMessageTooBig)
	}
	eventually(t, func() bool { return hub.Count() == 0 }, "client deregistered after oversized frame")
}

func TestOriginAllowed(t *testing.T) {
	srv, _ := newTestServer(t)
	ctx := testCtx(t)

	conn, _, err := websocket.Dial(ctx, wsURL(srv), &websocket.DialOptions{
		HTTPHeader: http.Header{"Origin": []string{"http://localhost:5173"}},
	})
	if err != nil {
		t.Fatalf("dial with allowed origin: %v", err)
	}
	defer func() { _ = conn.CloseNow() }()

	send(t, ctx, conn, "ping")
	if got, want := recvText(t, ctx, conn), "pong"; got != want {
		t.Errorf("got %q, want %q", got, want)
	}
}

func TestOriginRejected(t *testing.T) {
	srv, _ := newTestServer(t)
	ctx := testCtx(t)

	conn, resp, err := websocket.Dial(ctx, wsURL(srv), &websocket.DialOptions{
		HTTPHeader: http.Header{"Origin": []string{"http://evil.example"}},
	})
	if err == nil {
		_ = conn.CloseNow()
		t.Fatal("dial with disallowed origin succeeded, want rejection")
	}
	if resp == nil {
		t.Fatalf("no HTTP response on rejection: %v", err)
	}
	if resp.StatusCode != http.StatusForbidden {
		t.Errorf("status = %d, want %d", resp.StatusCode, http.StatusForbidden)
	}
}
