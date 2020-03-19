package com.example.pearlie.checkoutsuper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nicolasjoukhdar on 4/10/17.
 */

public class Transaction {


    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user")
    @Expose
    private int user;
    @SerializedName("supermarketBranch")
    @Expose
    private int supermarketBranch;
    @SerializedName("total_price")
    @Expose
    double total_price;
    @SerializedName("time")
    @Expose
    private String time;
    public class TransactionData{
        private String item;
        private String quantity;
        private String item_name;

        public TransactionData(String item, String quantity, String item_name) {
            this.item = item;
            this.quantity = quantity;
            this.item_name = item_name;
        }

        public String getItem_name() {
            return item_name;
        }

        public String getItem() {
            return item;
        }

        public String getQuantity() {
            return quantity;
        }
    }
    @SerializedName("transactionData")
    @Expose
    private List<TransactionData> transactionData;

    public int getId() {
        return id;
    }

    public int getUser() {
        return user;
    }

    public int getSupermarketBranch() {
        return supermarketBranch;
    }

    public double getTotal_price() {
        return total_price;
    }

    public String getTime() {
        return time;
    }

    public List<TransactionData> getTransactionData() {
        return transactionData;
    }
}
