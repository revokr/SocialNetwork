package main.labjavafx;
import Domain.Friendship;
import Domain.User;
import Repository.Paging.Page;
import Repository.Paging.Pageable;
import Service.MessageService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import Service.UserService;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class HelloController {
    private UserService srv;
    private User targetedUser;

    public TableView<User> friendsTable = new TableView<User>();
    public TableView<User> usersTable = new TableView<User>();
    public TableView<Friendship> friendRequestsTable = new TableView<Friendship>();
    private Button prevButton;
    private Button nextButton;
    private Label pageNrLabel = new Label();

    private int currentPage = 0;
    private static final int PAGE_SIZE = 2;

    public void setUserService(UserService srv) {
        this.srv = srv;
    }

    public void setTargetedUser(User targetedUser) {
        this.targetedUser = targetedUser;
    }

    public User getTargetedUser() {
        return targetedUser;
    }

    public UserService getSrv() {
        return srv;
    }

    public void initializeUI(AnchorPane root) {
        /// Friends Table
        final Label label = new Label("Friends");
        label.setFont(new Font("Arial", 20));

        friendsTable.setEditable(true);

        TableColumn idCol = new TableColumn("UID");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        TableColumn firstNameCol = new TableColumn("First Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        TableColumn lastNameCol = new TableColumn("Last Name");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        friendsTable.getColumns().addAll(idCol, firstNameCol, lastNameCol);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, friendsTable);

        root.getChildren().add(vbox);
        AnchorPane.setTopAnchor(vbox, 0.0);
        AnchorPane.setLeftAnchor(vbox, 0.0);

        prevButton = new Button("Previous");
        nextButton = new Button("Next");
        HBox hbox = new HBox();
        hbox.setSpacing(70);
        hbox.setPadding(new Insets(10, 0, 0, 10));
        hbox.getChildren().addAll(prevButton, pageNrLabel, nextButton);
        root.getChildren().add(hbox);
        AnchorPane.setTopAnchor(hbox, 440.0);
        AnchorPane.setLeftAnchor(hbox, 0.0);

        prevButton.setOnAction(e -> onPrevClick());
        nextButton.setOnAction(e -> onNextClick());

        /// Users Table

        final Label label2 = new Label("Users");
        label2.setFont(new Font("Arial", 20));
        usersTable.setEditable(true);

        TableColumn usrCol = new TableColumn("UID");
        usrCol.setMinWidth(100);
        usrCol.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        TableColumn friendCol = new TableColumn("First Name");
        friendCol.setMinWidth(100);
        friendCol.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        TableColumn friendFromCol = new TableColumn("Last Name");
        friendFromCol.setMinWidth(100);
        friendFromCol.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        ObservableList<User> data2 = FXCollections.observableArrayList();
        for (User usr : srv.getUsers()) {
            data2.add(usr);
        }

        usersTable.setItems(data2);
        usersTable.getColumns().addAll(usrCol, friendCol, friendFromCol);

        // Coloană pentru acceptarea cererilor de prietenie
        TableColumn<Friendship, Void> acceptColumn = new TableColumn<>("Action");
        acceptColumn.setMinWidth(100);

        // Definește un factory pentru celule personalizate
        Callback<TableColumn<Friendship, Void>, TableCell<Friendship, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Friendship, Void> call(final TableColumn<Friendship, Void> param) {
                return new TableCell<>() {
                    private final Button acceptButton = new Button("Accept");

                    {
                        acceptButton.setOnAction(event -> {
                            Friendship friendship = getTableView().getItems().get(getIndex());
                            if (!friendship.getAccepted()) {
                                // Acceptă cererea de prietenie
                                friendship.setAccepted(true);
                                srv.addFriend(targetedUser.getId(), srv.getUser(friendship.getId1()));
                                friendRequestsTable.refresh();
                                extractFriends();
                                // Afișează o alertă de succes
                                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Friend request accepted!");
                                alert.show();
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Friendship friendship = getTableView().getItems().get(getIndex());
                            // Arată butonul doar pentru cererile neacceptate
                            if (!friendship.getAccepted()) {
                                setGraphic(acceptButton);
                            } else {
                                setGraphic(null);
                            }
                        }
                    }
                };
            }
        };

        acceptColumn.setCellFactory(cellFactory);
        friendRequestsTable.getColumns().add(acceptColumn);

        final VBox vbox2 = new VBox();
        vbox2.setSpacing(5);
        vbox2.setPadding(new Insets(10, 0, 0, 10));
        vbox2.getChildren().addAll(label2 , usersTable);

        root.getChildren().add(vbox2);

        AnchorPane.setTopAnchor(vbox2, 0.0);
        AnchorPane.setLeftAnchor(vbox2, 400.0);

        /// Friend Requests Table

        final Label label3 = new Label("Friend Requests");
        label3.setFont(new Font("Arial", 20));
        friendRequestsTable.setEditable(true);

        TableColumn fromUsr = new TableColumn("From User");
        fromUsr.setMinWidth(100);
        fromUsr.setCellValueFactory(new PropertyValueFactory<Friendship, Long>("id1"));
        TableColumn toUsr = new TableColumn("To User");
        toUsr.setMinWidth(100);
        toUsr.setCellValueFactory(new PropertyValueFactory<Friendship, Long>("id2"));
        TableColumn friendFrom = new TableColumn("Last Name");
        friendFrom.setMinWidth(100);
        friendFrom.setCellValueFactory(new PropertyValueFactory<Friendship, LocalDate>("friendFrom"));
        TableColumn status = new TableColumn("Accepted");
        status.setMinWidth(100);
        status.setCellValueFactory(new PropertyValueFactory<Friendship, Boolean>("accepted"));


        friendRequestsTable.getColumns().addAll(fromUsr, toUsr, friendFrom, status);

        final VBox vbox3 = new VBox();
        vbox3.setSpacing(5);
        vbox3.setPadding(new Insets(10, 0, 0, 10));
        vbox3.getChildren().addAll(label3 , friendRequestsTable);

        root.getChildren().add(vbox3);
        AnchorPane.setTopAnchor(vbox3, 0.0);
        AnchorPane.setLeftAnchor(vbox3, 800.0);

        extractFriends();

        /// Add friend and show the new added friend in the table
        TextField fN = new TextField();
        fN.setText("");
        TextField lN = new TextField();
        lN.setText("");
        Button addFriendBtn = new Button("Add Friend");

        Label addFriendLblF = new Label("First Name");
        Label addFriendLblL = new Label("Last Name");
        HBox addFriendBox = new HBox();
        addFriendBox.setSpacing(10);
        addFriendBox.setPadding(new Insets(10));
        root.getChildren().add(addFriendBox);
        AnchorPane.setTopAnchor(addFriendBox, 600.0);
        AnchorPane.setLeftAnchor(addFriendBox, 10.0);
        addFriendBox.getChildren().addAll(addFriendLblF,fN,addFriendLblL, lN, addFriendBtn);

        addFriendBtn.setOnAction(e -> onAddFriendButtonClick(fN, lN));

        /// Show friend requests for targetedUser
        Button friendRequestsBtn = new Button("Show Friend Requests");
        HBox friendRequestsBox = new HBox();
        friendRequestsBox.setSpacing(10);
        friendRequestsBox.setPadding(new Insets(10));
        root.getChildren().add(friendRequestsBox);
        AnchorPane.setTopAnchor(friendRequestsBox, 700.0);
        AnchorPane.setLeftAnchor(friendRequestsBox, 10.0);
        friendRequestsBox.getChildren().addAll(friendRequestsBtn);

        friendRequestsBtn.setOnAction(e -> extractFriendRequests());

        /// Delete selected friend from friends table
        Button deleteFriendBtn = new Button("Delete Friend");
        deleteFriendBtn.setDisable(true);

        friendsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            deleteFriendBtn.setDisable(newValue == null);
        });

        deleteFriendBtn.setOnAction(e -> onDeleteButtonClick());

        VBox deleteFriendBox = new VBox(deleteFriendBtn);
        deleteFriendBox.setSpacing(10);
        deleteFriendBox.setPadding(new Insets(10));
        root.getChildren().add(deleteFriendBox);
        AnchorPane.setLeftAnchor(deleteFriendBox, 10.0);
        AnchorPane.setTopAnchor(deleteFriendBox, 750.0);

        VBox chatBox = createChatBox();
        AnchorPane.setBottomAnchor(chatBox, 10.0);
        AnchorPane.setRightAnchor(chatBox, 10.0);
        root.getChildren().add(chatBox);
    }

    private VBox createChatBox() {
        VBox chatBox = new VBox();
        chatBox.setSpacing(10);
        chatBox.setPadding(new Insets(10));

        Label chatLabel = new Label("Chat");
        chatLabel.setFont(new Font("Arial", 20));

        ListView<String> chatListView = new ListView<>();
        TextField chatInputField = new TextField();
        Button sendButton = new Button("Send");

        chatBox.getChildren().addAll(chatLabel, chatListView, chatInputField, sendButton);

        sendButton.setOnAction(e -> {
            String messageContent = chatInputField.getText();
            if (!messageContent.isEmpty()) {
                // Send the message to friends
                List<User> friends = srv.getUser(targetedUser.getId()).getFriends(); // Method to fetch user's friends
                MessageService.getInstance().sendMessage(targetedUser, friends, messageContent, null);

                // Add the message locally
                chatListView.getItems().add("You: " + messageContent);
                chatInputField.clear();
            }
        });

        // Register listener to update the chat UI when new messages are received
        MessageService.getInstance().registerListener(targetedUser.getId(), message -> {
            if (message.getToUsers().contains(targetedUser)) {
                chatListView.getItems().add(message.getFromUser().getFirstName() + ": " + message.getMessage());
            }
        });

        return chatBox;
    }

    public void extractFriends() {
        ObservableList<User> friendsData = FXCollections.observableArrayList();
        Page<User> pageFriends =  srv.getFriendsOnPage(new Pageable(currentPage, PAGE_SIZE), targetedUser.getId());
        List<User> listFriends = StreamSupport.stream(pageFriends.getElementsOnPage().spliterator(), false).collect(Collectors.toList());
        friendsData.addAll(listFriends);
        friendsTable.setItems(friendsData);
        friendsTable.refresh();

        int nrOfPages = (int) Math.ceil((double) pageFriends.getTotalNrOfElements()/PAGE_SIZE);
        pageNrLabel.setText("Page " + (currentPage + 1) + " of " + nrOfPages);
        prevButton.setDisable(currentPage == 0);
        nextButton.setDisable(currentPage + 1 == nrOfPages);
    }

    public void extractFriendRequests() {
        ObservableList<Friendship> friendsData = FXCollections.observableArrayList();
        List<Long> check = new ArrayList<>();
        for (Friendship fr : srv.getFriendships()) {
            if (fr.getId1() == targetedUser.getId()) {
                check.add(fr.getId2());
            }
        }
        for (Friendship fr : srv.getFriendships()) {
            if (fr.getId2() == targetedUser.getId()) {
                if (check.contains(fr.getId1())) {
                    fr.setAccepted(true);
                }
                friendsData.add(fr);
            }
        }
        friendRequestsTable.setItems(friendsData);
        friendRequestsTable.refresh();
    }

    private void onPrevClick() {
        currentPage--;
        extractFriends();
    }

    private void onNextClick() {
        currentPage++;
        extractFriends();
    }

    public void onSearchButtonClick(TextField searchFirstNameField, TextField searchLastNameField) {
        String firstName = searchFirstNameField.getText();
        String lastName = searchLastNameField.getText();

        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            targetedUser = srv.getUserByName(firstName, lastName);
            if (targetedUser != null) {
                extractFriends();
            } else {
                friendsTable.getItems().clear();
                showAlert(Alert.AlertType.ERROR, "User not found!");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Please enter both first name and last name.");
        }
    }

    public void onAddFriendButtonClick(TextField addFriendFirstNameField, TextField addFriendLastNameField) {
        String firstName = addFriendFirstNameField.getText();
        String lastName = addFriendLastNameField.getText();

        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            User user = srv.getUserByName(firstName, lastName);
            if (user != null) {
                if (targetedUser != null) {
                    srv.addFriend(targetedUser.getId(), user);
                    extractFriends();
                    extractFriendRequests();
                } else {
                    friendsTable.getItems().clear();
                    showAlert(Alert.AlertType.ERROR, "Targeted User is not found!");
                }
            } else {
                friendsTable.getItems().clear();
                showAlert(Alert.AlertType.ERROR, "User not found!");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Please enter both first name and last name.");
        }
    }

    public void onDeleteButtonClick() {
        User selectedFriend = (User) friendsTable.getSelectionModel().getSelectedItem();
        if (selectedFriend != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this friend?",
                    ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                try {
                    srv.removeFriend(targetedUser.getId(), selectedFriend.getId());
                    friendsTable.getItems().remove(selectedFriend);
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Friend deleted successfully!");
                    successAlert.show();
                } catch (Exception ex) {
                    Alert error = new Alert(Alert.AlertType.ERROR, "Failed to delete friend!");
                    error.show();
                }
            }
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.show();
    }


}