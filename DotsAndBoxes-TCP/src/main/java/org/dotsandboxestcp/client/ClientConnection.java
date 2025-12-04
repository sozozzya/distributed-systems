package org.dotsandboxestcp.client;

import org.dotsandboxestcp.game.Move;
import org.dotsandboxestcp.protocol.Message;
import org.dotsandboxestcp.protocol.MessageType;
import org.dotsandboxestcp.protocol.MessageSerializer;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class ClientConnection {
    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final Consumer<Message> onMessage;

    public ClientConnection(String host, int port, Consumer<Message> onMessage) {
        this.host = host;
        this.port = port;
        this.onMessage = onMessage;
    }

    public void connect() throws IOException {
        System.out.println("[Client] INFO Connecting to server " + host + ":" + port + "...");

        socket = new Socket(host, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

        System.out.println("[Client] INFO Connected successfully");

        Thread readerThread = new Thread(this::readLoop);
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private void readLoop() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("[Client] INFO Received raw: " + line);

                Message msg = MessageSerializer.fromJson(line);

                if (msg == null) {
                    System.out.println("[Client] ERROR Failed to parse TCPMessage");
                    continue;
                }

                System.out.println("[Client] INFO Parsed message: " + msg.getType());
                onMessage.accept(msg);
            }
        } catch (IOException e) {
            System.out.println("[Client] WARN Connection closed or read error: " + e.getMessage());
        } finally {
            close();
        }
    }

    public void sendMove(Move m) {
        System.out.println("[Client] INFO Sending MOVE: " + m);
        Message msg = new Message(MessageType.MOVE)
                .add("move", MessageSerializer.toJson(m));
        send(msg);
    }

    public void sendRestart() {
        System.out.println("[Client] INFO Sending RESTART");
        Message msg = new Message(MessageType.RESTART);
        send(msg);
    }

    public void send(Message msg) {
        if (out != null) {
            System.out.println("[Client] INFO Sending message type=" + msg.getType());
            out.println(MessageSerializer.toJson(msg));
        } else {
            System.out.println("[Client] ERROR Cannot send: output stream is null");
        }
    }

    public void close() {
        System.out.println("[Client] INFO Closing client connection...");
        try {
            if (in != null) in.close();
        } catch (IOException ignored) {
        }

        if (out != null) out.close();

        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.out.println("[Client] ERROR Error while closing socket: " + e.getMessage());
        }
    }
}
