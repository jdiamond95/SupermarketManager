package com.example.pearlie.checkoutsuper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nicolasjoukhdar on 4/10/17.
 */

public class SCEntry {
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("branch")//branch id
    @Expose
    private int branch;
    @SerializedName("item")
    @Expose
    private String item;
    @SerializedName("barcode")
    @Expose
    private String barcode;
    @SerializedName("quantity")
    @Expose
    private int quantity;
    @SerializedName("item_name")
    @Expose
    private String item_name;
    @SerializedName("price")
    @Expose
    private double price;

    public String getUser() {
        return user;
    }

    public double getPrice() {
        return price;
    }

    public String getBarcode() {
        return barcode;
    }

    public int getBranch() {
        return branch;
    }

    public String getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getItem_name() {
        return item_name;
    }

    public SCEntry(String user, int branch, String item, String barcode, int quantity, String item_name, double price) {

        this.user = user;
        this.branch = branch;
        this.item = item;
        this.barcode = barcode;
        this.quantity = quantity;
        this.item_name = item_name;
        this.price = price;
    }

}
