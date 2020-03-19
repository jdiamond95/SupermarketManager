package com.example.pearlie.checkoutsuper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class ManagerHomeActivity extends AppCompatActivity {

    Button retBut, addBut, removeBut;
    UserProfile user;
    private static final int RC_BARCODE_CAPTURE = 9001;
    String barc;
    private SItem curitem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);
        retBut = (Button) findViewById(R.id.button10);
        addBut = (Button) findViewById(R.id.button11);
        //removeBut = (Button) findViewById(R.id.button12);

        Bundle extras = getIntent().getExtras();

//        user = new UserProfile("Customer",extras.getString("UserID"));

        retBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity("Return");
                RetrofitClient.logOut();
            }
        });

        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity("Scanner");
            }
        });
        /*
        removeBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });
        */
//        myWebappServices = ApiUtils.getAPIService();
    }
    private void launchActivity(String s) {
        if (s.equals("Scanner")){
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
            intent.putExtra(BarcodeCaptureActivity.AutoCapture, true);
            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        } else if (s.equals("Addproduct")) {
            Intent intent = new Intent(this, AddProductActivity.class);
            intent.putExtra("Barcode", barc);
            intent.putExtra("UserID", getIntent().getExtras().getInt("UserID"));
            startActivity(intent);
        } else if (s.equals("Editproduct")){
            Intent intent = new Intent(this, EditProductActivity.class);
            intent.putExtra("barName", curitem.getName());
            intent.putExtra("Barcode", barc);
            intent.putExtra("UserID", getIntent().getExtras().getInt("UserID"));
            intent.putExtra("barPrice", curitem.getPrice());
            intent.putExtra("barQuantity", curitem.getQuantity());
            startActivity(intent);
        } else if (s.equals("Return")){
//            Intent intent = new Intent(this, LoginActivity.class);
            RetrofitClient.logOut();
            finish();
//            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    barc = barcode.displayValue;
                } else {
                    barc = "no barcode found";
                }
            } else {
                barc = "no barcode found";
            }
            MyWebappServices myWebappServices = ApiUtils.getAPIService();
            Call<SItem> lookup = myWebappServices.lookup(getIntent().getExtras().getInt("UserID"), barc);
            lookup.enqueue(new Callback<SItem>() {
                @Override
                public void onResponse(Call<SItem> call, Response<SItem> response) {
                    if(response.isSuccessful()) {
                        curitem = response.body();
                        launchActivity("Editproduct");

                    } else {
                        if(response.code() == 404)
                            launchActivity("Addproduct");
                    }
                }

                @Override
                public void onFailure(Call<SItem> call, Throwable t) {

                }
            });
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        RetrofitClient.logOut();
        finish();
    }
}
