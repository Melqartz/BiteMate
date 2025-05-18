
package com.example.food_app;

import com.example.food_app.models.Address;
import com.example.food_app.models.CartItem;

import java.util.List;

public class Order {
    private String orderId;
    private String userId;
    private Address address;
    private List<CartItem> cartItems;
    private double totalPrice;
    private String date;

    public Order() {
        // Default constructor required for Firebase database
    }

    public Order(String orderId, String userId, Address address, List<CartItem> cartItems, double totalPrice, String date) {
        this.orderId = orderId;
        this.userId = userId;
        this.address = address;
        this.cartItems = cartItems;
        this.totalPrice = totalPrice;
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}


