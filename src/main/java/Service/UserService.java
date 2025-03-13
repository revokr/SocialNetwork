package Service;

import Domain.Friendship;
import Domain.User;
import Domain.Validator.ValidationException;
import Network.Network;
import Network.UserNetwork;
import Repository.DB.DBRepository;
import Repository.Paging.Page;
import Repository.Paging.Pageable;
import Repository.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

public class UserService {
    private DBRepository repo;
    private Network<Long, User> network = new UserNetwork();

    public UserService(DBRepository repo) {
        this.repo = repo;
        for (User usr : repo.findAll()) {
            this.network.addEntity(usr);
        }
    }

    public User getUserByName(String firstName, String lastName) {
        try {
            Optional<User> user = repo.findByName(firstName, lastName);
            User user1 = user.get();
            return user1;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public User getUser(Long ID) {
        try {
            Optional<User> user = repo.findOne(ID);
            User user1 = user.get();
            return user1;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Iterable<User> getUsers() {
        try {
            Iterable<User> users = repo.findAll();
            return users;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public Page<User> getFriendsOnPage(Pageable pageable, Long userid1) {
        return repo.findAllFriendsOnPage(pageable, userid1);
    }

    public Iterable<Friendship> getFriendships() {
        return repo.getFriendships();
    }

    public User save(User user) {
        try {
            Optional<User> u = repo.save(user);
            if (u.isEmpty()) return null;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    public User delete(Long id) {
        try {
            Optional<User> rtrn = repo.delete(id);
            if (rtrn.isPresent()){
                for (User user : repo.findAll()) {
                    for (User friend : user.getFriends()) {
                        if (friend.getId() == id) {
                            repo.removeFriend(user.getId(), id);
                        }
                    }
                }
                return rtrn.get();
            } else {
                return null;
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public User update(User user) {
        try {
            repo.update(user);
            return null;
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }

        return user;
    }

    public User addFriend(Long userID, User friend) {
        try {
            if (repo.addFriend(userID, friend).isEmpty()) {
                NotificationService.getInstance().notifyUser(friend.getId(), friend.getFirstName() + " a new user added you:" + getUser(userID).getFirstName());
                return null;
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
        }

        return friend;
    }

    public User removeFriend(Long userID, Long friendID) {
        try {
            if (this.repo.removeFriend(userID, friendID).isEmpty()) return null;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        return getUser(friendID);
    }

    public List<Set<User>> getCommunities() {
        network.clearEntt();
        for (User user : repo.findAll()) {
            network.addEntity(user);
        }
        return network.getCommunities();
    }

    public Set<User> getSSC() {
        return network.getSCCWithLongestRoad();
    }

}
