package com.example.mystore.database;

public class Comment {
    private String userEmail;
    private String productId;
    private String commentText;

    public Comment() {
    }

    public Comment(String userEmail, String productId, String commentText) {
        this.userEmail = userEmail;
        this.productId = productId;
        this.commentText = commentText;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}
