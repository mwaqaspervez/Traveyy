package com.example.dayshift_2.traveyy;

public class NewUser {

    String email;
    String password;
    String phoneNumber;
    String country;
    String picUrl;
    String name;

    public NewUser() {

    }

    public NewUser(String email, String password, String phoneNumber, String country, String picUrl, String name) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.picUrl = picUrl;
        this.name = name;
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
    public String getPicUrl() {
        return picUrl;
    }
}
