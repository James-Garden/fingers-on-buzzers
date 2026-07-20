package ws

import (
	"context"
	"fmt"
	"log/slog"
	"strings"
	"sync"
	"time"

	"github.com/coder/websocket"
)

const (
	// Exceeding readLimit closes the connection with StatusMessageTooBig.
	readLimit = 8 << 10

	// How far a connection may fall behind before it is dropped.
	outBuffer = 16

	writeTimeout = 10 * time.Second
	pingInterval = 30 * time.Second
	pingTimeout  = 10 * time.Second
)

type frame struct {
	typ  websocket.MessageType
	data []byte
}

func textFrame(s string) frame {
	return frame{typ: websocket.MessageText, data: []byte(s)}
}

// client owns one connection: a read loop plus a write pump that owns every
// write, so a broadcast cannot block on a slow peer.
type client struct {
	id   uint64
	conn *websocket.Conn
	hub  *Hub
	log  *slog.Logger

	out  chan frame
	done chan struct{}

	closeOnce sync.Once
}

func newClient(id uint64, conn *websocket.Conn, hub *Hub, log *slog.Logger) *client {
	return &client{
		id:   id,
		conn: conn,
		hub:  hub,
		log:  log,
		out:  make(chan frame, outBuffer),
		done: make(chan struct{}),
	}
}

// send never blocks; a client that cannot keep up is dropped.
func (c *client) send(msg []byte) bool {
	return c.sendFrame(frame{typ: websocket.MessageText, data: msg})
}

func (c *client) sendFrame(f frame) bool {
	select {
	case <-c.done:
		return false
	default:
	}

	select {
	case c.out <- f:
		return true
	default:
		c.log.Warn("dropping slow client", "buffered", len(c.out))
		c.kill()
		return false
	}
}

// kill is idempotent and unblocks both goroutines: CloseNow errors out Read and
// Write, and closing done releases the pump.
func (c *client) kill() {
	c.closeOnce.Do(func() {
		close(c.done)
		_ = c.conn.CloseNow()
	})
}

// readLoop returns the error that ended the connection.
func (c *client) readLoop(ctx context.Context) error {
	defer c.hub.wg.Done()
	for {
		typ, data, err := c.conn.Read(ctx)
		if err != nil {
			return err
		}
		c.log.Debug("frame received", "type", typ.String(), "bytes", len(data))
		c.handle(typ, data)
	}
}

// writePump is the only goroutine that writes to the connection.
func (c *client) writePump(ctx context.Context) {
	defer c.hub.wg.Done()

	ticker := time.NewTicker(pingInterval)
	defer ticker.Stop()

	for {
		select {
		case <-ctx.Done():
			return
		case <-c.done:
			return
		case f := <-c.out:
			if err := c.write(ctx, f); err != nil {
				c.log.Debug("write failed", "err", err)
				c.kill()
				return
			}
		case <-ticker.C:
			// Ping blocks until the pong arrives, so failure means a dead peer.
			pingCtx, cancel := context.WithTimeout(ctx, pingTimeout)
			err := c.conn.Ping(pingCtx)
			cancel()
			if err != nil {
				c.log.Debug("ping failed", "err", err)
				c.kill()
				return
			}
		}
	}
}

func (c *client) write(ctx context.Context, f frame) error {
	writeCtx, cancel := context.WithTimeout(ctx, writeTimeout)
	defer cancel()
	return c.conn.Write(writeCtx, f.typ, f.data)
}

// handle is the seam where the real protocol lands: a decode plus a hand-off to
// the session's command channel. Keep it small and stateless.
func (c *client) handle(typ websocket.MessageType, data []byte) {
	if typ == websocket.MessageBinary {
		c.sendFrame(frame{typ: websocket.MessageBinary, data: data})
		return
	}

	text := string(data)
	switch {
	case text == "ping":
		c.sendFrame(textFrame("pong"))
	case text == "time":
		c.sendFrame(textFrame("time " + time.Now().UTC().Format(time.RFC3339Nano)))
	case text == "whoami":
		c.sendFrame(textFrame(fmt.Sprintf("whoami %d", c.id)))
	case strings.HasPrefix(text, "broadcast "):
		msg := strings.TrimPrefix(text, "broadcast ")
		c.hub.Broadcast([]byte(fmt.Sprintf("broadcast %d %s", c.id, msg)))
	default:
		c.sendFrame(textFrame("echo: " + text))
	}
}
