package com.example.pearlie.checkoutsuper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nicolasjoukhdar on 3/10/17.
 */

public class Product {
    @SerializedName("barcode")
    @Expose
    private String barcode;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("category")
    @Expose
    private String category;

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Product(String barcode, String name, String category) {
        this.barcode = barcode;
        this.name = name;
        this.category = category;
    }

    @Override
    public String toString(){
        return "{ +" +
                "barcode='" + barcode + '\'' +
                ", name ='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
