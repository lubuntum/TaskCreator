package com.pavel.databaseapp.data;

import android.graphics.Bitmap;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pavel.databaseapp.services.BitmapStoreService;

import javax.annotation.Nullable;

public class Employee {
    public static final String NAME = "name";
    private static final String SECOND_NAME = "secondName";
    public static final String DATE_OF_BIRTHDAY = "birthday";
    public static final String POSITION = "position";
    public static final String MAIL = "mail";
    public static final String PHONE = "phone";
    public static final String PROFILE_ICON = "profileIcon";

    public String id;
    public String name;
    public String secondName;
    public String birthday;
    public String position;
    public String mail;
    public String phone;
    public Bitmap profileIcon;

    private String password;

    public Employee(String name, String secondName,
                    String birthday, String position,
                    String mail, String phone, @Nullable Bitmap profileIcon) {
        this.name = name;
        this.secondName = secondName;
        this.birthday = birthday;
        this.position = position;
        this.mail = mail;
        this.phone = phone;
        this.profileIcon = profileIcon;
    }

    public static Employee parse(QueryDocumentSnapshot doc){
        Bitmap profileIcon = parseIconProfile(doc.getString(PROFILE_ICON));
        return new Employee(doc.getString(NAME),doc.getString(SECOND_NAME),doc.getString(DATE_OF_BIRTHDAY),
                doc.getString(POSITION),doc.getString(MAIL),doc.getString(PHONE),profileIcon);
    }
    public static Bitmap parseIconProfile(String encodeIcon){
        if(encodeIcon == null) return null;
        return BitmapStoreService.decodeBitmap(encodeIcon);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
