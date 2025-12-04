package org.dotsandboxestcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class GameServer {
    private final int port;
    private final ServerState state = new ServerState();
    private final ExecutorService clientsPool = Executors.newCachedThreadPool();
    private ServerSocket serverSocket;
    private volatile boolean running = true;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("[SERVER] Started on port " + port);

            while (running) {
                Socket socket = serverSocket.accept();
                System.out.println("[SERVER] Incoming connection from: " + socket.getRemoteSocketAddress());

                ClientHandler handler = new ClientHandler(socket, state);
                clientsPool.submit(handler);
            }

        } catch (IOException e) {
            System.out.println("[ERROR] Server exception: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        System.out.println("[SERVER] Shutting down...");

        running = false;
        clientsPool.shutdownNow();
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {
        }

        System.out.println("[SERVER] Shutdown complete.");
    }
}
