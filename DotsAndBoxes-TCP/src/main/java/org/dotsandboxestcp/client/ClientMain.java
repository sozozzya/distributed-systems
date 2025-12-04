package org.dotsandboxestcp.client;

import org.dotsandboxestcp.ui.GameController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("[Client] INFO Launching client UI...");

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/dotsandboxestcp/game.fxml")
        );

        Scene scene = new Scene(loader.load());
        GameController controller = loader.getController();
        controller.setStage(stage);

        stage.setTitle("Dots and Boxes - Client");
        stage.setScene(scene);

        stage.setWidth(600);
        stage.setHeight(600);
        stage.setMinWidth(600);
        stage.setMinHeight(600);
        stage.setResizable(true);

        System.out.println("[Client] INFO UI initialized");

        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("[Client] INFO Starting application...");
        launch(args);
    }
}
