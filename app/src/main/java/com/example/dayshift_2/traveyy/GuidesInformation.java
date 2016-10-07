package com.example.dayshift_2.traveyy;


import java.util.List;

class GuidesInformation {


    String guideName;
    String guideInfo;
    String guideStatus;
    String guidePic;
    int guideRating;
    String guideLicenseNumber;
    int age;
    String gender;
    String qualification;
    int tours;
    String about;
    String email;
    boolean gym, car, dine, wifi, paw;
    String who;
    String picsUrls[];
    List<ReviewSingleMessage> reviews;

    public GuidesInformation(String guideName, String guideInfo, String guidePic,
                             int guideRating, String guideLicenseNumber, int age, String gender, String qualification,
                             int tours, String about, String email, String guideStatus,
                             boolean gym, boolean car, boolean dine, boolean paw, boolean wifi,
                             String who, String[] picsUrls, List<ReviewSingleMessage> reviews) {
        this.guideName = guideName;
        this.guideInfo = guideInfo;
        this.guidePic = guidePic;
        this.guideLicenseNumber = guideLicenseNumber;
        this.age = age;
        this.guideRating = guideRating;
        this.gender = gender;
        this.qualification = qualification;
        this.tours = tours;
        this.about = about;
        this.email = email;
        this.guideStatus = guideStatus;
        this.gym = gym;
        this.car = car;
        this.dine = dine;
        this.paw = paw;
        this.wifi = wifi;
        this.who = who;
        this.picsUrls = picsUrls;
        this.reviews = reviews;
    }
}
