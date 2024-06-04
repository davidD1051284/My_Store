package com.example.mystore.database;

public class UserInfos {
    private String email;
    private int balance;

    public UserInfos() {
    }

    public UserInfos(String email, int balance) {
        this.email = email;
        this.balance = balance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
