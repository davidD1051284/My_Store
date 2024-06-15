package com.example.mystore.ui.home;

public class HomeItem {
    private String productId;
    private String productName;
    private int productPrice;
    private String imageUrl;
    private int productQuantity;
    private String productDescription;
    private String seller;
    public HomeItem() {

    }

    public HomeItem(String productId, String productName, int productPrice, String imageUrl, int quantity, String description, String seller) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.imageUrl = imageUrl;
        this.productQuantity = quantity;
        this.productDescription = description;
        this.seller = seller;
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

    public String getProductId() {
        return productId;
    }

    public String getSeller() {
        return seller;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
}
