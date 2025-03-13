package Repository.DB;

import Domain.Entity;
import Domain.Friendship;
import Domain.User;
import Domain.Validator.ValidationException;
import Domain.Validator.Validator;
import Repository.Paging.Page;
import Repository.Paging.Pageable;
import Repository.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.time.Instant;

public class DBRepository implements Repository<Long, User, Friendship> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;
    private Connection connection;

    public DBRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        try {
            this.connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> findByID(Long id) {
        String sql = "SELECT * FROM \"user\" WHERE uid=" + "\'" + id + "\'";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            String name = resultSet.getString("first_name");
            String last_name = resultSet.getString("last_name");

            User user = new User(id, name, last_name);
            return Optional.of(user);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Optional<User> findByName(String firstName, String lastName) {
        String sql = "SELECT * FROM \"user\" WHERE first_name = " + "\'" + firstName +"\'" + " AND last_name = " +"\'"+ lastName+"\'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            rs.next();
            Long id = rs.getLong("uid");
            String name = rs.getString("first_name");
            String last_name = rs.getString("last_name");
            return Optional.of(new User(id, name, last_name));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findOne(Long aLong) {
        User user = findByID(aLong).get();
        String sql = "SELECT * FROM \"friendship\"";
         try (PreparedStatement statement1 = connection.prepareStatement(sql);
                 ResultSet resultSet = statement1.executeQuery()) {

                while (resultSet.next()) {
                    Long id1 = resultSet.getLong("userid1");
                    Long id2 = resultSet.getLong("userid2");

                    User friend = findByID(id2).get();
                    if (id1 == user.getId() && !user.getFriends().contains(friend)) {
                        user.addFriend(friend);
                    }
                }

                return Optional.of(user);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }

         return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        Map<Long, User> users = new HashMap<>();
        String sql = "SELECT * FROM \"user\"";
        try (PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
            Long id = resultSet.getLong("uid");
            String name = resultSet.getString("first_name");
            String last_name = resultSet.getString("last_name");

            User user = new User(id, name, last_name);
            users.putIfAbsent(id, user);
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql = "SELECT * FROM \"friendship\"";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("userid1");
                Long id2 = resultSet.getLong("userid2");

                if (findOne(id2).isPresent()) {
                    users.get(id1).addFriend(findOne(id2).get());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users.values();
    }

    private int countFriends(Long userid1) {
        String sql = "SELECT COUNT(*) as count FROM \"friendship\" WHERE userid1 = " + "\'" + userid1 + "\'";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resulSet = statement.executeQuery()) {

            int totalNrOfFriends = 0;
            if (resulSet.next()) {
                totalNrOfFriends = resulSet.getInt("count");
            }
            return totalNrOfFriends;
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Page<User> findAllFriendsOnPage(Pageable pageable, Long userid1) {
        List<User> friends = new ArrayList<>();
        String sql = "SELECT * FROM \"friendship\" WHERE userid1 = "  + "\'" + userid1 +"\'" + " limit ? offset ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pageable.getPageNumber());
            statement.setInt(2, pageable.getPageSize() * pageable.getPageNumber());

            try (ResultSet resultSet = statement.executeQuery()) {
                /// Getiing friends
                while (resultSet.next()) {
                    Long id2 = resultSet.getLong("userid2");
                    friends.add(findByID(id2).get());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Page<>(friends, countFriends(userid1));
    }

    @Override
    public Iterable<Friendship> getFriendships() {
        List<Friendship> friendships = new ArrayList<>();
        String sql = "SELECT * FROM \"friendship\"";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("userid1");
                Long id2 = resultSet.getLong("userid2");
                Date date = resultSet.getDate("friendfrom");
                LocalDateTime ldt = Instant.ofEpochMilli(date.getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                Friendship fr = new Friendship(id1, id2, ldt);
                friendships.add(fr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }

        return friendships;
    }


    @Override
    public Optional<User> save(User entity) {
        int rez = -1;
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO \"user\" (first_name, last_name) VALUES (?, ?)");
        ) {
            validator.validate(entity);
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (rez > 0) {
            return Optional.empty();
        } else {
            return Optional.of(entity);
        }
    }

    @Override
    public Optional<User> delete(Long aLong) {
        int rez = -1;
        String sql = "DELETE FROM \"user\" WHERE uid=" +aLong;
        Optional<User> user = findOne(aLong);
        try (PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0) {
            for (Friendship fr : getFriendships()) {
                if (fr.getId1() == aLong) {
                    removeFriend(aLong, fr.getId2());
                } else if (fr.getId2() == aLong) {
                    removeFriend(fr.getId1(), aLong);
                }
            }
            return user;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> update(User entity) {
        int rez = -1;
        String sql = "UPDATE \"user\" SET first_name=" + "\'" +entity.getFirstName() + "\'" + ", last_name=" + "\'" + entity.getLastName() + "\'" + " WHERE uid=" +entity.getId();
        try (PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0) {
            return Optional.empty();
        } else {
            return Optional.of(entity);
        }
    }

    @Override
    public Optional<User> addFriend(Long userID, User friend) {
        Optional<User> usr = findOne(userID);
        if (usr.isEmpty()) throw new IllegalArgumentException("There isn't a user with given id!!");
        int rez = -1;
        if (!usr.get().getFriends().contains(friend)) {
            usr.get().addFriend(friend);
            String sql = "INSERT INTO \"friendship\" (userid1, userid2, friendfrom) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql);
            ) {
                validator.validate(friend);
                statement.setLong(1, userID);
                statement.setLong(2, friend.getId());
                LocalDateTime currentDateTime = LocalDateTime.now();
                statement.setDate(3, java.sql.Date.valueOf(currentDateTime.toLocalDate()));

                rez = statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (rez > 0) {
            return Optional.empty();
        } else {
            return Optional.of(friend);
        }
    }

    @Override
    public Optional<User> removeFriend(Long userID, Long friend) {
        int rez = -1;
        String sql = "DELETE FROM \"friendship\" WHERE userid1=" +userID + " AND userid2=" +friend;
        Optional<User> user = findOne(friend);
        try (PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            rez = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rez > 0) {
            return user;
        } else {
            return Optional.empty();
        }
    }


}
