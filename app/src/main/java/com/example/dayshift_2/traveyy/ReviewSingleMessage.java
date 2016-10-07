package com.example.dayshift_2.traveyy;

class ReviewSingleMessage {

    private String name;
    private String message;
    private int rating;
    private String imageUri;
    private String date;

    public ReviewSingleMessage(String name, String imageUri, String message, int rating, String date) {
        this.name = name;
        this.message = message;
        this.rating = rating;
        this.imageUri = imageUri;
        this.date = date;
    }

    public String getName() {
        return name;
    }
    public String getMessage() {
        return message;
    }
    public int getRating() {
        return rating;
    }
    public String getImageUri() {
        return imageUri;
    }
    public String getDate() {return date; }
}
