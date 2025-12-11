package org.dotsandboxessoap.soap;

import org.dotsandboxessoap.game.dto.GameStateDTO;
import org.dotsandboxessoap.game.dto.MoveDTO;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService(
        name = "GameService",
        targetNamespace = "http://soap/"
)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL)
public interface GameService {
    @WebMethod
    String registerClient();

    @WebMethod
    GameStateDTO getState(@WebParam(name = "clientId") String clientId);

    @WebMethod
    GameStateDTO makeMove(@WebParam(name = "clientId") String clientId, @WebParam(name = "move") MoveDTO move);

    @WebMethod
    GameStateDTO restart(@WebParam(name = "clientId") String clientId);
}
