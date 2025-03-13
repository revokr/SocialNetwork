package UI;

import Domain.User;
import Service.UserService;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class UI {
    private UserService srv;
    public UI(UserService srv) {
        this.srv = srv;
    }

    private void printMenu() {
        System.out.println("adduser      - add a new user");
        System.out.println("deleteuser   - delete a user");
        System.out.println("updateuser   - delete a user");
        System.out.println("addfriend    - add a new friend to an existing user");
        System.out.println("deletefriend - delete a friend");
        System.out.println("print        - print all users and their friend list");
        System.out.println("printcomm    - print all communities");
        System.out.println("printlcomm   - print the most sociable community");
        System.out.println("menu         - print this menu");
    }

    public void run() {
        printMenu();
        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.print(">>>");
            String command = sc.nextLine();
            if (command.equals("quit")) break;
            switch (command) {
                case "adduser":
                    addUser();
                    break;
                case "deleteuser":
                    deleteUser();
                    break;
                case "updateuser":
                    updateUser();
                    break;
                case "addfriend":
                    addFirendship();
                    break;
                case "deletefriend":
                    deleteFriendship();
                    break;
                case "print":
                    printUsers();
                    break;
                case "printcomm":
                    printComunites();
                    break;
                case "printlcomm":
                    printLongestSSC();
                    break;
                case "menu":
                    printMenu();
                    break;
            }
        }
    }

    public void addUser() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter First Name: ");
        String firstName = sc.nextLine();
        System.out.print("Enter Last Name: ");
        String lastName = sc.nextLine();
        /*System.out.print("Enter ID: ");
        Long id = sc.nextLong();*/
        User user = new User(firstName, lastName);
        this.srv.save(user);
    }

    public void addFirendship() {
        System.out.print("Enter the userID to which you want to add the friend: ");
        Scanner sc = new Scanner(System.in);
        Long userId = sc.nextLong();

        System.out.print("Enter FriendID: ");
        Long friendID = sc.nextLong();
        User friend2 = this.srv.getUser(friendID);
        if (friend2 == null) {
            System.out.print("The friend does not exist!!");
            System.out.println("Command stopped");
        } else {
            this.srv.addFriend(userId, friend2);
            System.out.println("Friend with ID: " + friendID + " was added!");
        }
    }

    public void deleteFriendship() {
        System.out.print("Enter the userID to which you want to remove the friend: ");
        Scanner sc = new Scanner(System.in);
        Long userId = sc.nextLong();
        System.out.print("Enter FriendID: ");
        Long friendID = sc.nextLong();
        User friend2 = this.srv.getUser(friendID);
        if (friend2 == null) {
            System.out.print("The friend does not exist!!");
            System.out.println("Command stopped");
        } else {
            this.srv.removeFriend(userId, friendID);
            System.out.println("Friend deleted successfully!");
        }
    }

    public void deleteUser() {
        System.out.print("Enter ID: ");
        Scanner sc = new Scanner(System.in);
        Long id = sc.nextLong();
        User usr = this.srv.delete(id);
        if (usr != null) {
            System.out.println("User with id " + id + " was deleted succesfully.");
        } else {
            System.out.println("There isn't a user with id : " + id);
        }
    }

    public void updateUser() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter First Name: ");
        String firstName = sc.nextLine();
        System.out.print("Enter Last Name: ");
        String lastName = sc.nextLine();
        System.out.print("Select the ID of the user you want to update: ");
        Long id = sc.nextLong();
        User usr = new User(id, firstName, lastName);
        User result = this.srv.update(usr);
        if (result == null) {
            System.out.println("User with id " + id + " was updated succesfully.");
        } else {
            System.out.println("The user with id " + id + " does not exist.");
        }
    }

    public void printUsers() {
        for (User usr : srv.getUsers()) {
            System.out.println("ID: " + usr.getId() + " ," + usr);
            System.out.println("Friends List: ");
            for (User friend : usr.getFriends()) {
                System.out.println("\t\t" + "ID: " + friend.getId() + " ," + friend);
            }
            System.out.println('\n');
        }
    }

    public void printComunites() {
        List<Set<User>> ssc = this.srv.getCommunities();
        System.out.println("Comunities: " + ssc.size());
        for (Set<User> ss : ssc) {
            for (User usr : ss) {
                System.out.print(" | " + usr.getFirstName() + " " + usr.getLastName() + " ");
            }
            System.out.println();
        }
    }

    public void printLongestSSC() {
        Set<User> sscWithLongestRoad = this.srv.getSSC();
        System.out.println("Comunitiy with longest road: " + sscWithLongestRoad.size()
        );
        for (User usr : sscWithLongestRoad) {
            System.out.print(" | " + usr.getFirstName() + " " + usr.getLastName() + " ");
        }
        System.out.println();
    }
}
