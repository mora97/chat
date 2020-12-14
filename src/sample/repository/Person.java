package sample.repository;

public class Person {
    public String name;
    public String id;
    public String email;
    public String password;
    static String loggedinUserId;

    public static void setLoggedinUserId(String id) {
        loggedinUserId = id;
    }

    public static String getLoggedinUserId() {
        return loggedinUserId;
    }

}
