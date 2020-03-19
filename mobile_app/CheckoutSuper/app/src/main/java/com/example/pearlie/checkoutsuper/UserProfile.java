package com.example.pearlie.checkoutsuper;

import java.util.List;

/**
 * Created by Pearlie on 13-Sep-17.
 */

public class UserProfile{

    private String username; //is this user a manager or customer
    private String first_name;
    private String last_name;
    private String email;
    private List<Integer> manager;

    public UserProfile(String username, String first_name, String last_name, String email, List<Integer> manager) {
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.manager = manager;
    }

    public List<Integer> getManager() {
        return manager;
    }

    public String getUsername() {
        return username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }
}
