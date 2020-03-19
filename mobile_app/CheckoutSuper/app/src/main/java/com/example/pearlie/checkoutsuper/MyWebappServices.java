package com.example.pearlie.checkoutsuper;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by nicolasjoukhdar on 3/10/17.
 */

public interface MyWebappServices{
    @GET("items/{barcode}/")
    Call<Product> product(@Path("bracode") String barcode);
    @POST("mobile_authenticate/")
    Call<UserProfile[]> auth();
    @POST("items/")
    Call<Product> create(@Body Product product);
    @POST("branches/{branchid}/inventory/")
    Call<Item> add(@Path("branchid") int branchid, @Body Item item);
    @GET("branches/{branchid}/inventory/{barcode}")
    Call<SItem> lookup(@Path("branchid") int branchid, @Path("barcode") String barcode);
    @PUT("branches/{branchid}/inventory/{barcode}/")
    Call<Item> edit(@Path("branchid") int branchid, @Path("barcode") String barcode, @Body Item item);
    @GET("branches/")
    Call<List<Branch>> branches();
    @GET("shoppingcart/{username}/{barcode}/")
    Call<SCEntry> scLookup(@Path("username") String username, @Path("barcode") String barcode);
    @PUT("shoppingcart/{username}/{barcode}/")
    Call<SCEntry> scUpdate(@Path("username") String username, @Path("barcode") String barcode, @Body SCEntry entry);
    @DELETE("shoppingcart/{username}/{barcode}/")
    Call<ResponseBody> scDelete (@Path("username") String username, @Path("barcode") String barcode);
    @POST("shoppingcart/{username}/")
    Call<SCEntry> addCart(@Path("username") String username, @Body SCEntry entry);
    @GET("shoppingcart/{username}/")
    Call<List<SCEntry>> getCart(@Path("username") String username);
    @GET("checkout/{username}/")
    Call<Transaction> checkout(@Path("username") String username);
    @GET("transactions/user/{username}/")
    Call<List<Transaction>> getAllHist (@Path("username") String username);
    @GET("transactions/transaction/{transaction_id}/")
    Call<Transaction> getSingHist (@Path("transaction_id") int transactionid);
    @DELETE("branches/{branchid}/inventory/{barcode}/")
    Call<ResponseBody> delprod(@Path("branchid") int branchid, @Path("barcode") String barcode);
    @DELETE("shoppingcart/{username}/")
    Call<ResponseBody> delCart(@Path("username") String username);
}
