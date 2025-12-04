module org.dotsandboxestcp {
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires com.google.gson;

    opens org.dotsandboxestcp.ui to javafx.fxml;
    opens org.dotsandboxestcp.protocol to com.google.gson;

    exports org.dotsandboxestcp.client;
    exports org.dotsandboxestcp.ui;
    exports org.dotsandboxestcp.protocol;
    exports org.dotsandboxestcp.game;
}