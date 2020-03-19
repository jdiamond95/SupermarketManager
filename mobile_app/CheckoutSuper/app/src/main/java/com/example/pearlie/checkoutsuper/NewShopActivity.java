package com.example.pearlie.checkoutsuper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewShopActivity extends AppCompatActivity {

    Button cancel, addItem, removeItem, Checkout, diffItem;
    private NumberPicker buyQuantity;
    private boolean inCart;
    private int cartQuantity;
    private String username;
    private int branchid;
    TextView displayItem;
    UserProfile user;
    String bar, newbarc;
    SItem currItem;
    SCEntry currSCEntry;
    HashMap<SItem,Integer> shoppingCart;

    private static final int RC_BARCODE_CAPTURE = 9001;
    Double runningTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shop);
        shoppingCart = new HashMap<SItem,Integer>();

        Bundle extras = getIntent().getExtras();

        username = extras.getString("User");
//        user = new UserProfile("Customer",extras.getString("UserID"));
        bar = extras.getString("Barcode");
        newbarc = bar;
        runningTotal = 0.00;

//        currItem = new SItem(newbarc,"Water","5.00");
        branchid = extras.getInt("UserID");
        currItem = new SItem(extras.getInt("UserID"), bar, extras.getString("Name"), extras.getInt("Quantity"), extras.getDouble("Price"), 1);
        inCart = extras.getBoolean("InCart");
        cartQuantity = extras.getInt("CartQuantity");

        cancel = (Button) findViewById(R.id.button7);
        addItem = (Button) findViewById(R.id.button8);
        removeItem = (Button) findViewById(R.id.button9);
        Checkout = (Button) findViewById(R.id.button6);
        diffItem = (Button) findViewById(R.id.button12);
        displayItem = (TextView) findViewById(R.id.textView4);
        buyQuantity = (NumberPicker) findViewById(R.id.numberPicker);

        buyQuantity.setWrapSelectorWheel(true);
        buyQuantity.setMinValue(0);
        buyQuantity.setMaxValue(currItem.getQuantity());
        buyQuantity.setValue(cartQuantity);



        displayItem.setText("Name: " + currItem.getName() +"\n\nPrice: " + currItem.getPrice() + "\n\nSet Quantity Below");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyWebappServices myWebappServices = ApiUtils.getAPIService();
                Call<ResponseBody> delCart = myWebappServices.delCart(username);
                delCart.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(NewShopActivity.this, "Deleted Cart!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(NewShopActivity.this, "Failed to delete cart!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                launchActivity("CancelShop");
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //add to shopping cart
                MyWebappServices myWebappServices = ApiUtils.getAPIService();
                SCEntry entry = new SCEntry(username, branchid, newbarc, newbarc, buyQuantity.getValue(), currItem.getName(), currItem.getPrice());
                if(inCart){
                    Call<SCEntry> scentry = myWebappServices.scUpdate(username, newbarc, entry);
                    scentry.enqueue(new Callback<SCEntry>() {
                        @Override
                        public void onResponse(Call<SCEntry> call, Response<SCEntry> response) {
                            if(response.isSuccessful()){
                                //launchActivity("Shop");
                            }else{
                                Toast.makeText(NewShopActivity.this, "Wasn't able to update item in cart with http code " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SCEntry> call, Throwable t) {
                            Toast.makeText(NewShopActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Call<SCEntry> scentry = myWebappServices.addCart(username, entry);
                    scentry.enqueue(new Callback<SCEntry>() {
                        @Override
                        public void onResponse(Call<SCEntry> call, Response<SCEntry> response) {
                            if(response.isSuccessful()){
//                                launchActivity("Shop");
                                Toast.makeText(NewShopActivity.this, "Added Item!!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(NewShopActivity.this, "Wasn't able to add item in cart with http code " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SCEntry> call, Throwable t) {
                            Toast.makeText(NewShopActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (shoppingCart.containsKey(currItem)){
//                    if (shoppingCart.get(currItem) > 1){
//                        shoppingCart.put(currItem,shoppingCart.get(currItem) - 1);
//                    } else {
//                        shoppingCart.remove(currItem);
//                    }
//                    runningTotal --;
//                    Toast confirmMessage = Toast.makeText(getApplicationContext(),
//                            "Product Successfully removed",Toast.LENGTH_SHORT);
//                    confirmMessage.show();
//                } else {
//                    Toast confirmMessage = Toast.makeText(getApplicationContext(),
//                            "Product did not exist in cart",Toast.LENGTH_SHORT);
//                    confirmMessage.show();
//                }

                MyWebappServices myWebappServices = ApiUtils.getAPIService();

                Call<ResponseBody> delItem = myWebappServices.scDelete(username, newbarc);

                delItem.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(NewShopActivity.this, "Deleted item!!", Toast.LENGTH_SHORT).show();
                            displayItem.setText("");
                            buyQuantity.setValue(0);
                            launchActivity("Shop");
                        }else{
                            Toast.makeText(NewShopActivity.this, "Failed to delete item!!!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });


            }
        });
        Checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                launchActivity("Checkout");
            }
        });
        diffItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity("Shop");
//                currItem = new SItem(newbarc, "Rice", "10.00");
            }
        });

    }

    private void launchActivity(String s) {
        if (s.equals("Shop")){
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
            intent.putExtra(BarcodeCaptureActivity.AutoCapture, true);
            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        } else if (s.equals("CancelShop")){
            Intent intent = new Intent(this, CustomerHomeActivity.class);
//            intent.putExtra("UserID", user.retID());
            intent.putExtra("User", username);
            finish();
            startActivity(intent);
        }else if(s.equals("Checkout")){
            Intent intent = new Intent(this, CheckoutShoppingCartActivity.class);
            intent.putExtra("User", username);
//            startActivity(intent);
            startActivityForResult(intent, 10);
        }else if(s.equals("Trans")){
            Intent intent = new Intent(this, CusHistoryActivity.class);
            intent.putExtra("User", username);
            finish();
            startActivity(intent);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    newbarc = barcode.displayValue;
                    MyWebappServices myWebappServices = ApiUtils.getAPIService();
                    Call<SItem> item = myWebappServices.lookup(branchid, newbarc);
                    item.enqueue(new Callback<SItem>() {
                        @Override
                        public void onResponse(Call<SItem> call, Response<SItem> response) {
                            if(response.isSuccessful()){
                                currItem = response.body();
                                displayItem.setText("Name: " + currItem.getName() +"\nPrice: " + currItem.getPrice() + "\nAvailable Quantity: " + currItem.getQuantity());
                                buyQuantity.setMinValue(0);
                                buyQuantity.setMaxValue(currItem.getQuantity());
                                MyWebappServices myWebappServices1 = ApiUtils.getAPIService();
                                Call<SCEntry> scentry = myWebappServices1.scLookup(username, newbarc);
                                scentry.enqueue(new Callback<SCEntry>() {
                                    @Override
                                    public void onResponse(Call<SCEntry> call, Response<SCEntry> response) {
                                        if(response.isSuccessful()){
                                            currSCEntry = response.body();
                                            inCart = true;
                                            cartQuantity = response.body().getQuantity();
                                        }else {
                                            inCart = false;
                                            cartQuantity = 0;
                                        }
                                        buyQuantity.setValue(cartQuantity);
                                    }

                                    @Override
                                    public void onFailure(Call<SCEntry> call, Throwable t) {
                                        Toast.makeText(NewShopActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                displayItem.setText("Item Not Found, Please Try Again");
                                currItem = new SItem(0, " ", " ", 0,0,0);
                                cartQuantity = 0;
                                buyQuantity.setValue(cartQuantity);

                            }
                        }

                        @Override
                        public void onFailure(Call<SItem> call, Throwable t) {
                            Toast.makeText(NewShopActivity.this, "Failed to lookup item", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    newbarc = "no barcode found";
                }
            } else {
                newbarc = "no barcode found";
            }
            //launchActivity("Additem");
        }
        else if(requestCode == 10){
            super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == RESULT_OK){
                launchActivity("Trans");
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        launchActivity("CancelShop");
    }

}
