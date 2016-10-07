package com.example.dayshift_2.traveyy;

class SingleMessage {

    private String name;
    private String message;
    private String time;
    private String senderEmail;

    public SingleMessage() {

    }

    public SingleMessage(String name, String message,String time,String senderEmail) {
        this.name = name;
        this.message = message;
        this.time = time;
        this.senderEmail = senderEmail;
    }

    public String getName() {
        return name;
    }
    public String getMessage() {
        return message;
    }
    public String getTime() {
        return time;
    }
    public String getSenderEmail() {
        return senderEmail;
    }

}
