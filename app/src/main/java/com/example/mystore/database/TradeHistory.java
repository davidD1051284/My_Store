package com.example.mystore.database;

import java.util.Date;

public class TradeHistory {
    private String seller;
    private String buyer;
    private Date tradeDate;
    private String productName;
    private int totalPrice;
    private int quantity;

    public TradeHistory() {
    }

    public TradeHistory(String seller, String buyer, Date tradeDate, String productName, int totalPrice, int quantity) {
        this.seller = seller;
        this.buyer = buyer;
        this.tradeDate = tradeDate;
        this.productName = productName;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}