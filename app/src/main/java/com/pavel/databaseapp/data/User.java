package com.pavel.databaseapp.data;

import com.google.firebase.firestore.QueryDocumentSnapshot;

public class User {
    public static final String USER_COLLECTION = "users";
    public static final String USER_NAME = "name";
    public static final String USER_LOGIN = "login";
    public static final String USER_PASS = "password";

    public String name;
    public String login;
    public String password;
    public User (String name, String login, String password){
        this.name = name;
        this.login = login;
        this.password = password;
    }
    public static User parse(QueryDocumentSnapshot document){
        String name = document.getString(USER_NAME);
        String login = document.getString(USER_LOGIN);
        String pass = document.getString(USER_PASS);
        return new User(name,login,pass);
    }
}
