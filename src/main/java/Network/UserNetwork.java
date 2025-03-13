package Network;

import Domain.Friendship;
import Domain.User;

import java.util.*;

public class UserNetwork extends Network<Long, User> {

    @Override
    public void addEntity(User usr) {
        if (!entities.containsKey(usr.getId())) {
            entities.putIfAbsent(usr.getId(), usr);
        }
    }

    @Override
    public void clearEntt() {
        entities.clear();
    }

    @Override
    public List<User> getConnections(User user) {
        List<User> friends = new ArrayList<>();
        for (User friend : user.getFriends()) {
            friends.add(friend);
        }
        return friends;
    }

    @Override
    protected void dfsFirst(User user, Set<Long> visited, Deque<User> finishStack) {
        visited.add(user.getId());

        for (User firend : user.getFriends()) {
            if (!visited.contains(firend.getId())) {
                dfsFirst(firend, visited, finishStack);
            }
        }

        finishStack.push(user);
    }

    @Override
    protected Map<Long, List<User>> reverseGraph() {
        Map<Long, List<User>> reverseGraph = new HashMap<>();

        for (User user : entities.values()) {
            for (User friend : getConnections(user)) {
                reverseGraph.putIfAbsent(friend.getId(), new ArrayList<>());
                reverseGraph.get(friend.getId()).add(user);
            }
        }

        return reverseGraph;
    }

    @Override
    protected void dfsSecond(User user, Set<Long> visited, Set<User> component, Map<Long, List<User>> reverseGraph) {
        visited.add(user.getId());
        component.add(user);

        if (reverseGraph.containsKey(user.getId())) {
            for (User reverseFriend : reverseGraph.get(user.getId())) {
                if (!visited.contains(reverseFriend.getId())) {
                    dfsSecond(reverseFriend, visited, component, reverseGraph);
                }
            }
        }
    }

    @Override
    public List<Set<User>> getCommunities() {
        Set<Long> visited = new HashSet<>();
        Deque<User> finishStack = new ArrayDeque<>();

        // perform dfs to get finish times
        for (User user : entities.values()) {
            if (!visited.contains(user.getId())) {
                dfsFirst(user, visited, finishStack);
            }
        }

        Map<Long, List<User>> reverseGraph = reverseGraph();

        // perform dfs on the reversed graph in the order of decreasing finish times
        visited.clear();
        List<Set<User>> scc = new ArrayList<>();

        while (!finishStack.isEmpty()) {
            User user = finishStack.pop();

            if (!visited.contains(user.getId())) {
                Set<User> component = new HashSet<>();
                dfsSecond(user, visited, component, reverseGraph);
                scc.add(component);
            }
        }

        return scc;
    }

    // Find the longest path in a strongly connected component using DFS
    private int findLongestPathInSCC(Set<User> scc) {
        int longestPath = 0;

        // Iterate over each user in the SCC and perform DFS to find the longest path
        for (User user : scc) {
            Set<User> visited = new HashSet<>();
            longestPath = Math.max(longestPath, dfsLongestPath(user, visited, scc));
        }

        return longestPath;
    }

    // Helper method to perform DFS and calculate the longest path from the current user
    private int dfsLongestPath(User user, Set<User> visited, Set<User> scc) {
        visited.add(user);

        int longest = 0;
        for (User friend : getConnections(user)) {
            if (scc.contains(friend) && !visited.contains(friend)) {
                // Recur for the friend's longest path
                longest = Math.max(longest, 1 + dfsLongestPath(friend, visited, scc));
            }
        }

        visited.remove(user);  // Remove from visited after processing
        return longest;
    }

    // Find the SCC with the longest road (longest path)
    public Set<User> getSCCWithLongestRoad() {
        List<Set<User>> sccs = getCommunities();
        Set<User> longestSCC = new HashSet<>();
        int maxPathLength = 0;

        for (Set<User> scc : sccs) {
            int pathLength = findLongestPathInSCC(scc);
            if (pathLength > maxPathLength) {
                maxPathLength = pathLength;
                longestSCC = scc;
            }
        }

        return longestSCC;
    }


}
