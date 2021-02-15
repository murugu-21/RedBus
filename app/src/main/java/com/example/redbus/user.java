package com.example.redbus;

import java.util.HashMap;
import java.util.Map;

public class user {
    public String name, email, photoUrl;
    public user(){
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }
    public user(String name, String email, String photoUrl) {
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }
}
