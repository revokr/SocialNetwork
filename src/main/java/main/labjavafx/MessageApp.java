package main.labjavafx;

import Domain.Message;
import Domain.User;
import Service.UserService;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDateTime;
import java.util.List;

public class MessageApp extends VBox {
    private User currentUser;
    private UserService userService;

    public MessageApp(User currentUser, UserService userService) {
        this.currentUser = currentUser;
        this.userService = userService;

        // Inițializează interfața grafică
        initializeUI();
    }

    private void initializeUI() {
        // Aici creezi toate elementele interfeței (ComboBox, TextArea, etc.)
        ComboBox<User> friendsComboBox = new ComboBox<>();
        TextArea messageArea = new TextArea();
        Button sendButton = new Button("Send Message");

        // Crează lista de prieteni
        friendsComboBox.getItems().addAll(currentUser.getFriends());

        // Logica pentru trimiterea mesajului
        sendButton.setOnAction(e -> sendMessage(messageArea, friendsComboBox));

        // Adăugăm elementele în layout
        this.getChildren().addAll(new Label("Send Message"), friendsComboBox, messageArea, sendButton);
    }

    // Logica pentru trimiterea mesajului
    private void sendMessage(TextArea messageArea, ComboBox<User> friendsComboBox) {
        String messageText = messageArea.getText();
        User selectedFriend = friendsComboBox.getValue();

        if (messageText.isEmpty() || selectedFriend == null) {
            // Validare pentru a asigura că există un mesaj și un prieten selectat
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a message and select a friend.");
            alert.show();
            return;
        }

        // Creăm și trimitem mesajul
        Message message = new Message(
                generateMessageId(),
                currentUser,
                List.of(selectedFriend),
                messageText,
                LocalDateTime.now(),
                null
        );

        // Afișăm un mesaj de succes
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Message sent successfully!");
        successAlert.show();
    }

    // Metoda pentru generarea unui ID pentru mesaj
    private Long generateMessageId() {
        // Exemplu de generare ID (poți implementa o metodă de generare unică a ID-urilor)
        return System.currentTimeMillis();
    }
}
