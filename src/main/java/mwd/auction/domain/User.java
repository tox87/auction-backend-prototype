package mwd.auction.domain;

/**
 * Created by yfain11 on 4/4/14.
 */
public class User {

    private int id;
    private String name;
    private String email;
    private boolean getOverbidNotifications;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isGetOverbidNotifications() {
        return getOverbidNotifications;
    }

    public void setGetOverbidNotifications(boolean getOverbidNotifications) {
        this.getOverbidNotifications = getOverbidNotifications;
    }
}
