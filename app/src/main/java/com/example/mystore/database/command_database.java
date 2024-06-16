package com.example.mystore.database;

public class command_database {
    private String user;
    private String comment;
    private float rating;

    public command_database() {
    }

    public command_database(String user, String comment, float rating) {
        this.user = user;
        this.comment = comment;
        this.rating = rating;
    }

    public String getUser(){
        return user;
    }

    public void setUser(String user){
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
