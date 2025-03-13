package main.labjavafx;

import Domain.Message;
import Domain.User;
import Service.MessageService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class MainWindow extends Application {
    private HelloController controller;
    public MainWindow(HelloController controller) {
        this.controller = controller;
    }

    @Override
    public void start(Stage stage) {
        AnchorPane root = new AnchorPane();

        // Use the controller to initialize and set up the UI
        controller.initializeUI(root);

        Scene scene = new Scene(root, 1280, 900);
        stage.setScene(scene);
        stage.setTitle("Main Window - User: " + controller.getTargetedUser().getFirstName());
        stage.show();
    }

}
