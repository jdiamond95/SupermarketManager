package com.example.pearlie.checkoutsuper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nicolasjoukhdar on 3/10/17.
 */

public class Item {

    @SerializedName("item")
    @Expose
    private String item;
    @SerializedName("supermarketBranch")
    @Expose
    private int supermarketBranch;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("price")
    @Expose
    private double price;
    @SerializedName("aisle")
    @Expose
    private int aisle;

    public Item(String item, int supermarketBranch, int quantity, double price, int aisle) {
        this.item = item;
        this.supermarketBranch = supermarketBranch;
        this.quantity = quantity;
        this.price = price;
        this.aisle = aisle;
    }

    public String getItem() {
        return item;
    }

    public int getSupermarketBranch() {
        return supermarketBranch;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public int getAisle() {
        return aisle;
    }


}
