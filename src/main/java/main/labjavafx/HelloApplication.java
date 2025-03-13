package main.labjavafx;

import Domain.User;
import Domain.Validator.UserValidator;
import Repository.DB.DBRepository;
import Service.MessageService;
import Service.NotificationService;
import Service.UserService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {
    private TableView table = new TableView();
    private TableView users = new TableView();
    private TableView friendRequests = new TableView();
    private User targetedUser = null;
    private UserService srv = null;
    private List<User> connectedUsers = new ArrayList<>();
    private MessageService messageService = MessageService.getInstance();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        String username = "postgres";
        String password = "rafael";
        String url = "jdbc:postgresql://localhost:5432/labMAP";
        DBRepository dbrepo = new DBRepository(url, username, password, new UserValidator());
        srv = new UserService(dbrepo);

        HelloController controller = new HelloController();
        controller.setUserService(srv);

        // Create Login UI
        stage.setTitle("Login");
        GridPane loginPane = new GridPane();
        loginPane.setPadding(new Insets(20));
        loginPane.setHgap(10);
        loginPane.setVgap(10);

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();

        Button loginButton = new Button("Login");

        loginPane.add(firstNameLabel, 0, 0);
        loginPane.add(firstNameField, 1, 0);
        loginPane.add(lastNameLabel, 0, 1);
        loginPane.add(lastNameField, 1, 1);
        loginPane.add(loginButton, 1, 2);

        loginButton.setOnAction(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            if (!firstName.isEmpty() && !lastName.isEmpty()) {
                User user = srv.getUserByName(firstName, lastName);
                if (user != null) {
                    openMainWindow(user);
                    onUserLogin(user);
                    firstNameField.clear();
                    lastNameField.clear();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid name! Please try again.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Please enter both first and last names.");
            }
        });

        Scene scene = new Scene(loginPane, 300, 200);
        stage.setScene(scene);
        stage.show();
    }
    private void openMainWindow(User user) {
        Stage mainStage = new Stage();
        HelloController controller = new HelloController();
        controller.setUserService(srv);
        controller.setTargetedUser(user); // Pass the logged-in user

        MainWindow mainWindow = new MainWindow(controller); // Encapsulates UI logic for main window
        mainWindow.start(mainStage);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.show();
    }

    public void onUserLogin(User user) {
        connectedUsers.add(user);
        NotificationService.getInstance().registerListener(user.getId(), message -> {
            // Afișează notificarea ca pop-up
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Friend Request Notification");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            });
        });

    }

    public void onUserLogout(Long userId) {
        NotificationService.getInstance().removeListener(userId);
    }

}