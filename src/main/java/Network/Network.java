package Network;

import Domain.Entity;
import Domain.Friendship;

import java.util.*;

public abstract class Network<ID, E extends Entity<ID>> {
    protected Map<ID, E> entities = new HashMap<>();

    // Add an entity (user or other types extending Entity
    public abstract void addEntity(E usr);

    public abstract void clearEntt();

    // Get the connections (friends) of a specific entity
    public abstract List<E> getConnections(E usr);

    protected abstract void dfsFirst(E user, Set<ID> visited, Deque<E> finishStack);

    protected abstract Map<ID, List<E>> reverseGraph();

    protected abstract void dfsSecond(E user, Set<ID> visited, Set<E> component, Map<ID, List<E>> reverseGraph);

    public abstract List<Set<E>> getCommunities();

    public abstract Set<E> getSCCWithLongestRoad();
}
