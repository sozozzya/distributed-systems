module org.dotsandboxessoap {
    requires java.base;

    requires javafx.controls;
    requires javafx.fxml;

    requires java.logging;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    requires jakarta.xml.ws;
    requires jakarta.xml.bind;
    requires jakarta.annotation;

    requires com.sun.xml.ws;
    requires com.sun.xml.bind;

    opens org.dotsandboxessoap.client to javafx.fxml;
    exports org.dotsandboxessoap.client;

    opens org.dotsandboxessoap.ui to javafx.fxml;
    exports org.dotsandboxessoap.ui;

    opens org.dotsandboxessoap.game to com.sun.xml.bind, com.sun.xml.ws, jakarta.xml.bind;
    exports org.dotsandboxessoap.game;

    opens org.dotsandboxessoap.soap to com.sun.xml.bind, com.sun.xml.ws, jakarta.xml.bind;
    exports org.dotsandboxessoap.soap;
    exports org.dotsandboxessoap.game.model;
    opens org.dotsandboxessoap.game.model to com.sun.xml.bind, com.sun.xml.ws, jakarta.xml.bind;
    exports org.dotsandboxessoap.game.dto;
    opens org.dotsandboxessoap.game.dto to com.sun.xml.bind, com.sun.xml.ws, jakarta.xml.bind;
    exports org.dotsandboxessoap.game.mapping;
    opens org.dotsandboxessoap.game.mapping to com.sun.xml.bind, com.sun.xml.ws, jakarta.xml.bind;
}
