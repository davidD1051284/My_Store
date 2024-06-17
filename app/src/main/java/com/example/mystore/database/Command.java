package com.example.mystore.database;

public class Command {
    private String user;
    private String comment;
    private String productId;
    private float rating;

    public Command() {
    }

    public Command(String user, String comment, String productId, float rating) {
        this.user = user;
        this.comment = comment;
        this.productId = productId;
        this.rating = rating;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
