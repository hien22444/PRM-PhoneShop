package com.example.phoneshop.data.model;

public class User {
    private final String id;
    private final String username;
    private final String rules;
    private boolean isBlocked;

    public User(String id, String username, String rules, boolean isBlocked) {
        this.id = id;
        this.username = username;
        this.rules = rules;
        this.isBlocked = isBlocked;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getRules() { return rules; }
    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }
}