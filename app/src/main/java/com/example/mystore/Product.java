package com.example.mystore;

public class Product {

    private String name;
    private String model;
    private int imageResId;

    public Product(String name, String model, int imageResId) {
        this.name = name;
        this.model = model;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public int getImageResId() {
        return imageResId;
    }
}
