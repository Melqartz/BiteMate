package com.example.food_app.models;

public class CartItem {
    private int itemId;
    private String itemName;
    private double itemPrice;
    private String userId;
    private int itemQty;
    private double totalPrice;

    public CartItem() {
        // Default constructor required for Firebase
    }


    public CartItem(int itemId, String userId, String itemName, double itemPrice, int itemQty) {
        this.itemId = itemId;
        this.userId = userId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemQty = itemQty;
        this.totalPrice = itemPrice * itemQty;
    }

    public String getName() {
        return itemName;
    }
    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public int getItemQty() {
        return itemQty;
    }

    public void setItemQty(int itemQty) {
        this.itemQty = itemQty;
        this.totalPrice = itemPrice * itemQty;
    }
}

