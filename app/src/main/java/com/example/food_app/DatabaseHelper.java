package com.example.food_app;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.food_app.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shopping_cart.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CART = "shopping_cart";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_ITEM_PRICE = "item_price";
    private static final String COLUMN_ITEM_QUANTITY = "item_quantity"; // add a new column for quantity
    private GlobalVariable glob=new GlobalVariable();
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_CART + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER_ID + " TEXT," +
                COLUMN_ITEM_NAME + " TEXT," +
                COLUMN_ITEM_PRICE + " REAL," +
                COLUMN_ITEM_QUANTITY + " INTEGER DEFAULT 1)"; // set default quantity to 1
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }


    public List<CartItem> getCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Select all rows from the shopping_cart table
        String selectQuery = "SELECT " + COLUMN_ID + ", " + COLUMN_ITEM_NAME + ", SUM(" + COLUMN_ITEM_QUANTITY + ") AS qty, SUM(" + COLUMN_ITEM_PRICE + "*(" + COLUMN_ITEM_QUANTITY + ")) AS total_price FROM " +
                TABLE_CART + " WHERE " + COLUMN_USER_ID + " = ? GROUP BY " + COLUMN_ITEM_NAME;
        Cursor cursor = db.rawQuery(selectQuery, new String[] { glob.currentUserId });

        // Loop through cursor and create CartItem objects
        if (cursor.moveToFirst()) {
            do {

                @SuppressLint("Range") int qty = cursor.getInt(cursor.getColumnIndex("qty"));
                @SuppressLint("Range") double total_price = cursor.getDouble(cursor.getColumnIndex("total_price"));
                @SuppressLint("Range") int itemId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                //@SuppressLint("Range") String userId = cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID));
                @SuppressLint("Range") String itemName = cursor.getString(cursor.getColumnIndex(COLUMN_ITEM_NAME));

                // Create a CartItem object and add it to the list
                CartItem cartItem = new CartItem(itemId, glob.currentUserId, itemName, total_price,qty);
                cartItems.add(cartItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return cartItems;
    }





    public void insertCartItem(String userId, String itemName, double itemPrice) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the item already exists in the cart for the user
        String selectQuery = "SELECT * FROM " + TABLE_CART + " WHERE " + COLUMN_USER_ID + " = ? AND " +
                COLUMN_ITEM_NAME + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[] { userId, itemName });

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_ITEM_PRICE, itemPrice);

        if (cursor.getCount() > 0) {
            // Update the quantity of the item
            cursor.moveToFirst();
            @SuppressLint("Range") int itemId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_ITEM_QUANTITY)) + 1;
            values.put(COLUMN_ITEM_QUANTITY, quantity);
            db.update(TABLE_CART, values, COLUMN_ID + "=?", new String[] { String.valueOf(itemId) });
        } else {
            // Insert a new row for the item
            values.put(COLUMN_ITEM_QUANTITY, 1);
            db.insert(TABLE_CART, null, values);
        }

        cursor.close();
        db.close();
    }



    public void deleteCartItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_ID + " = ?", new String[] { String.valueOf(itemId) });
        db.close();
    }

    public void updateQuantity(int itemId, int quantity) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_QUANTITY, quantity);

        db.update(TABLE_CART, values, COLUMN_ID + "=?", new String[]{String.valueOf(itemId)});

        db.close();
    }


    public double getTotalPrice() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Select the total price of all items in the cart for the current user
        String selectQuery = "SELECT SUM(" + COLUMN_ITEM_PRICE + "*" + COLUMN_ITEM_QUANTITY + ") AS total_price FROM " +
                TABLE_CART + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String[] { glob.currentUserId });

        double totalPrice = 0;

        // Get the total price from the cursor if it exists
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") double total_price = cursor.getDouble(cursor.getColumnIndex("total_price"));
            totalPrice = total_price;
        }

        cursor.close();
        db.close();

        return totalPrice;
    }

    public void deleteCartItems(String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_USER_ID + " = ?", new String[]{userId});
        db.close();
    }



}

