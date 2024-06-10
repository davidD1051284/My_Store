package com.example.mystore.database;

public class TradeNotify {
    private String seller;
    private int totalPrice;

    public  TradeNotify() {
    }

    public TradeNotify(String seller, int totalPrice) {
        this.seller = seller;
        this.totalPrice = totalPrice;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
