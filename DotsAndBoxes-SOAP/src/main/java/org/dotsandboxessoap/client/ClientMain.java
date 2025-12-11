package org.dotsandboxessoap.client;

import org.dotsandboxessoap.ui.GameController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("[Client] INFO Launching client UI...");

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/dotsandboxessoap/game.fxml")
        );

        Scene scene = new Scene(loader.load());
        GameController controller = loader.getController();
        controller.setStage(stage);

        stage.setTitle("Dots and Boxes");
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
