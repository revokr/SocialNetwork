package Service;

import Domain.Message;
import Domain.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class MessageService {
    private static MessageService instance;
    private final List<Message> messages = new ArrayList<>();
    private final ConcurrentHashMap<Long, List<Message>> userMessages = new ConcurrentHashMap<>();
    private Map<Long, List<Consumer<Message>>> listeners = new HashMap<>();

    public static MessageService getInstance() {
        if (instance == null) {
            instance = new MessageService();
        }
        return instance;
    }

    public void sendMessage(User fromUser, List<User> toUsers, String messageContent, Message reply) {
        Message message = new Message(
                (long) (messages.size() + 1), // Generate ID
                fromUser,
                toUsers,
                messageContent,
                LocalDateTime.now(),
                reply
        );
        messages.add(message);

        for (User u : toUsers) {
            userMessages.computeIfAbsent(u.getId(), k -> new ArrayList<>()).add(message);
        }

        userMessages.computeIfAbsent(fromUser.getId(), k -> new ArrayList<>()).add(message);

        for (User u : toUsers) {
            if (listeners.containsKey(u.getId())) {
                for (Consumer<Message> listener : listeners.get(u.getId())) {
                    listener.accept(message);
                }
            }
        }
    }

    public void registerListener(Long userId, Consumer<Message> listener) {
        listeners.computeIfAbsent(userId, k -> new ArrayList<>()).add(listener);
    }

    public void removeListener(Long userId, Consumer<Message> listener) {
        if (listeners.containsKey(userId)) {
            listeners.get(userId).remove(listener);
        }
    }

}
