package com.example.mystore.ui.home;

public class HomeItem {
    private String productName;
    private int productPrice;
    private String imageUrl;
    private int productQuantity;
    private String productDescription;

    public HomeItem() {

    }

    public HomeItem(String productName, int productPrice, String imageUrl, int quantity, String description) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.imageUrl = imageUrl;
        this.productQuantity = quantity;
        this.productDescription = description;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public String getProductDescription() {
        return productDescription;
    }
}
