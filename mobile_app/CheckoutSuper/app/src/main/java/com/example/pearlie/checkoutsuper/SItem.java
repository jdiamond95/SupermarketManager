package com.example.pearlie.checkoutsuper;

/**
 * Created by Pearlie on 26-Sep-17.
 */

public class SItem {
    private int id;
    private String barcode;
    private String name;
    private int quantity;
    private double price;
    private int aisle;

    public SItem(int id, String barcode, String name, int quantity, double price, int aisle) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.aisle = aisle;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getAisle() {
        return aisle;
    }

    public String getBarcode (){
        return barcode;
    }

    public String getName(){
        return name;
    }

    public Double getPrice(){
        return price;
    }
}
