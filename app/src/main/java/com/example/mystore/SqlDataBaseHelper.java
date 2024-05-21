package com.example.mystore;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqlDataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "my_store.db";
    private static final int DATABASE_VERSION = 1;

    public SqlDataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, String TableName) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_users_sql = "CREATE TABLE IF NOT EXISTS users (" +
                "userId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userName TEXT not null unique," +
                "userPassword TEXT not null" +
                ")";
        db.execSQL(create_users_sql);

        String create_shop_cart_sql = "CREATE TABLE IF NOT EXISTS shop_cart (" +
                "cartId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "productId INTEGER not null," +
                "username TEXT not null," +
                "productAmount INTEGER not null," +
                "FOREIGN KEY (productId) REFERENCES products(_id)," +
                "FOREIGN KEY (username) REFERENCES users(username)" +
                ")";

        db.execSQL(create_shop_cart_sql);

        String create_products_sql = "CREATE TABLE IF NOT EXISTS products (" +
                "productId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "productName TEXT," +
                "productPrice INTEGER," +
                "productAmount INTEGER," +
                "productDescription TEXT," +
                "productImage BLOB" +
                ")";
        db.execSQL(create_products_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String shop_cart_SQL = "DROP TABLE IF EXISTS shop_cart";
        db.execSQL(shop_cart_SQL);
        final String products_SQL = "DROP TABLE IF EXISTS products";
        db.execSQL(products_SQL);
        final String users_SQL = "DROP TABLE IF EXISTS users";
        db.execSQL(users_SQL);
        onCreate(db);
    }

    public void insertProduct(String name, int price, int amount, String description, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("productName", name);
        values.put("productPrice", price);
        values.put("productAmount", amount);
        values.put("productDescription", description);
        values.put("productImage", image);

        db.insert("products", null, values);
        db.close();
    }
}
