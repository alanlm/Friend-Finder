package edu.csulb.android.friendfinder;

import java.util.List;

/**
 * Created by krist on 4/7/2017.
 */

public class User {
    public String username;
    public List<String> friends;

    public User(){
    }

    public User(String username){
        this.username = username;
    }
}
