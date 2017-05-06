package edu.csulb.android.friendfinder;

import java.util.List;

/**
 * Created by krist on 4/7/2017.
 */

public class User {
    public String username;
    public List<String> friends;
    public double latitude;
    public double longitude;
    public String phonenumber;
    public User(){
    }

    public User(String username/*, String phoneNumber */){
        this.username = username;
        //.this.phoneNumber = phoneNumber;
    }

}
