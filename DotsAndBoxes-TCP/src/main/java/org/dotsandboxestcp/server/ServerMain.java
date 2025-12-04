package org.dotsandboxestcp.server;

public class ServerMain {
    public static void main(String[] args) {
        int port = 9000;
        System.out.println("[SERVER] Launching...");
        GameServer server = new GameServer(port);
        server.start();
    }
}
