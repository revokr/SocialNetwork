package Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class NotificationService {
    private static NotificationService instance;
    private final Map<Long, Consumer<String>> listeners = new HashMap<>();

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    // Înregistrare ascultător pentru un utilizator
    public void registerListener(Long userId, Consumer<String> listener) {
        listeners.put(userId, listener);
    }

    // Eliminare ascultător (când utilizatorul se deconectează)
    public void removeListener(Long userId) {
        listeners.remove(userId);
    }

    // Trimitere notificare
    public void notifyUser(Long userId, String message) {
        if (listeners.containsKey(userId)) {
            listeners.get(userId).accept(message);
        }
    }
}
