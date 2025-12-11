package org.dotsandboxessoap.soap;

import jakarta.xml.ws.Endpoint;

public class ServerMain {
    public static void main(String[] args) {
        String address = "http://0.0.0.0:9000/game";
        GameServiceImpl impl = new GameServiceImpl();
        System.out.println("[SOAP-SERVER] Publishing endpoint at " + address);
        Endpoint.publish(address, impl);
        System.out.println("[SOAP-SERVER] Endpoint published. WSDL: " + address + "?wsdl");
        System.out.println("[SOAP-SERVER] Server running. Press CTRL+C to stop.");
    }
}
