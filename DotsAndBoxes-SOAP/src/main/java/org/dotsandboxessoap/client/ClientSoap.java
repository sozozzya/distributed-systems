package org.dotsandboxessoap.client;

import org.dotsandboxessoap.game.dto.GameStateDTO;
import org.dotsandboxessoap.game.dto.MoveDTO;

import org.dotsandboxessoap.soap.GameService;

import jakarta.xml.ws.Service;

import javax.xml.namespace.QName;
import java.net.URL;

public class ClientSoap {
    private final GameService port;
    private String clientId;
    private String assignedColor;

    public ClientSoap(String wsdlUrl) throws Exception {
        URL url = new URL(wsdlUrl);
        QName serviceName = new QName("http://soap/", "GameService");
        Service service = Service.create(url, serviceName);
        QName portName = new QName("http://soap/", "GameServicePort");
        this.port = service.getPort(portName, GameService.class);
    }

    public void register() {
        String res = port.registerClient();
        if (res != null && res.startsWith("ERROR")) {
            throw new RuntimeException("Failed to register: " + res);
        }
        String[] parts = res.split(":", 2);
        if (parts.length >= 2) {
            clientId = parts[0];
            assignedColor = parts[1];
        } else {
            clientId = res;
        }
        System.out.println("[SOAP-CLIENT] Registered as " + clientId + " color=" + assignedColor);
    }

    public String getAssignedColor() {
        return assignedColor;
    }

    public GameStateDTO getState() {
        try {
            return port.getState(clientId);
        } catch (Exception e) {
            System.err.println("[SOAP-CLIENT] Error getState(): " + e.getMessage());
            return null;
        }
    }

    public GameStateDTO makeMove(MoveDTO dto) {
        try {
            return port.makeMove(clientId, dto);
        } catch (Exception e) {
            System.err.println("[SOAP-CLIENT] Error makeMove(): " + e.getMessage());
            return null;
        }
    }

    public GameStateDTO restart() {
        try {
            return port.restart(clientId);
        } catch (Exception e) {
            System.err.println("[SOAP-CLIENT] Error restart(): " + e.getMessage());
            return null;
        }
    }
}
