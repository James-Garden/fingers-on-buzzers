// Package ws exposes the game's websocket endpoint. The message handling is a
// raw text/binary debug protocol; the protobuf protocol arrives with the
// session handshake work.
package ws

import (
	"context"
	"fmt"
	"log/slog"
	"sync"

	"github.com/coder/websocket"
)

// Hub is the registry of live connections.
type Hub struct {
	log *slog.Logger

	mu      sync.Mutex
	clients map[*client]struct{}
	closed  bool

	wg sync.WaitGroup
}

func NewHub(log *slog.Logger) *Hub {
	return &Hub{
		log:     log,
		clients: make(map[*client]struct{}),
	}
}

// add reports false once the hub has shut down, so connections racing shutdown
// are rejected rather than orphaned.
func (h *Hub) add(c *client) bool {
	h.mu.Lock()
	defer h.mu.Unlock()
	if h.closed {
		return false
	}
	h.clients[c] = struct{}{}
	h.wg.Add(2) // read loop + write pump
	return true
}

func (h *Hub) remove(c *client) {
	h.mu.Lock()
	delete(h.clients, c)
	h.mu.Unlock()
}

func (h *Hub) Broadcast(msg []byte) {
	for _, c := range h.snapshot() {
		c.send(msg)
	}
}

// announceDeparture stays quiet during shutdown, where every peer is closing
// anyway.
func (h *Hub) announceDeparture(id uint64) {
	h.mu.Lock()
	closed := h.closed
	h.mu.Unlock()
	if closed {
		return
	}
	h.Broadcast([]byte(fmt.Sprintf("left %d", id)))
}

func (h *Hub) Count() int {
	h.mu.Lock()
	defer h.mu.Unlock()
	return len(h.clients)
}

// snapshot lets callers send without holding h.mu, which would deadlock:
// send may kill a client, and dying clients call remove.
func (h *Hub) snapshot() []*client {
	h.mu.Lock()
	defer h.mu.Unlock()
	out := make([]*client, 0, len(h.clients))
	for c := range h.clients {
		out = append(out, c)
	}
	return out
}

// Shutdown drains every connection. Budget at least 6s: Close waits up to 5s
// for the peer's reply, and CloseNow cannot shorten that — the two share a
// compare-and-swap, so it would block on the in-flight Close.
func (h *Hub) Shutdown(ctx context.Context) error {
	h.mu.Lock()
	h.closed = true
	h.mu.Unlock()

	// Concurrently, so one wedged peer's timeout does not stack on the rest.
	for _, c := range h.snapshot() {
		go func() { _ = c.conn.Close(websocket.StatusGoingAway, "server shutting down") }()
	}

	done := make(chan struct{})
	go func() {
		h.wg.Wait()
		close(done)
	}()

	select {
	case <-done:
		return nil
	case <-ctx.Done():
		return ctx.Err()
	}
}
