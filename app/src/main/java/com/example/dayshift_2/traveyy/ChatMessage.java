package com.example.dayshift_2.traveyy;

public class ChatMessage {

    private String message;
    private String userEmail;
    private String guideEmail;
    private String QueryEmail;
    private String date;
    private String sender;

    public ChatMessage() {
    }

    public ChatMessage(String message, String userEmail, String guideEmail, String queryEmail, String date,String sender) {
        this.message = message;
        this.userEmail = userEmail;
        this.guideEmail = guideEmail;
        this.QueryEmail = queryEmail;
        this.date = date;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }
    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {this.sender = sender;}
    public void setMessage(String message) {
        this.message = message;
    }
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {this.userEmail = userEmail;}
    public String getGuideEmail() {
        return guideEmail;
    }
    public void setGuideEmail(String guideEmail) {this.guideEmail = guideEmail;}
    public String getQueryEmail() {
        return QueryEmail;
    }
    public void setQueryEmail(String queryEmail) {
        QueryEmail = queryEmail;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

}
