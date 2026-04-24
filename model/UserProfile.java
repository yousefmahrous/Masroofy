package model;

public class UserProfile {
    private String name;
    private String hashedPIN;

    public UserProfile(String name, String hashedPIN) {
        this.name = name;
        this.hashedPIN = hashedPIN;
    }

    public String getName() {
        return name;
    }
    public String getHashedPIN() {
        return hashedPIN;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHashedPIN(String hashedPIN) {
        this.hashedPIN = hashedPIN;
    }
}