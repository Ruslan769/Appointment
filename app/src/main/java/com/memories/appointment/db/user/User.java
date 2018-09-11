package com.memories.appointment.db.user;

import android.graphics.Bitmap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {

    private int id;
    private String name = "";
    private String email = "";
    private String hash = "";
    private Bitmap image;

    public User() {
    }

    public User(int id, String name, String email, String hash) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hash = hash;
    }

    public User(String name, String email, String hash) {
        this.name = name;
        this.email = email;
        this.hash = hash;
    }

    public User(String name, String email, String hash, Bitmap image) {
        this.name = name;
        this.email = email;
        this.hash = hash;
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getHash() {
        return hash;
    }

    public Bitmap getImage() {
        return image;
    }

    public static String md5(String s) {
        try {

            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
