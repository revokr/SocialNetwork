package Domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message {
    private Long id;
    private User fromUser;
    private List<User> toUsers;
    private String message;
    private LocalDateTime date;

    private Message reply = null;

    // Constructor
    public Message(Long id, User fromUser, List<User> toUsers, String message, LocalDateTime date, Message reply) {
        this.id = id;
        this.fromUser = fromUser;
        this.toUsers = toUsers;
        this.message = message;
        this.date = date;
        this.reply = reply;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public User getFromUser() {
        return fromUser;
    }

    public List<User> getToUsers() {
        return toUsers;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Message getReply() {
        return reply;
    }
}
