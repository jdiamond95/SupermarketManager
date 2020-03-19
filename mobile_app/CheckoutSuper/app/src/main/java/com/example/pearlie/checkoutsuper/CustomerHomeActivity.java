package com.example.pearlie.checkoutsuper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerHomeActivity extends AppCompatActivity {

    Button retHome, shopHist;
    ListView newShop;
    UserProfile user;
    private String username;
    private boolean inCart;
    private int cartQuantity;
    private static final int RC_BARCODE_CAPTURE = 9001;
    String barc;
    private List<Branch> branchlist;
    private int branchid;
    private SItem curitem;
    ProgressDialog progress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        final Bundle extras = getIntent().getExtras();
        progress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);


        retHome = (Button) findViewById(R.id.button2);
        newShop = (ListView) findViewById(R.id.list1);
        shopHist = (Button) findViewById(R.id.button4);

        username = extras.getString("User");

        MyWebappServices myWebappServices = ApiUtils.getAPIService();
        final Call<List<Branch>> branches = myWebappServices.branches();
        branches.enqueue(new Callback<List<Branch>>() {
            @Override
            public void onResponse(Call<List<Branch>> call, Response<List<Branch>> response) {
                if(response.isSuccessful()){
                    branchlist = response.body();
                    ArrayAdapter<String> branchAdaptor = new ArrayAdapter<String>(newShop.getContext(), android.R.layout.simple_list_item_1);
                    for(Branch b : branchlist){
                        branchAdaptor.add(b.getCompanyName());
                    }
                    newShop.setAdapter(branchAdaptor);
                }else
                    Toast.makeText(CustomerHomeActivity.this, "Failed to get branches with response " + response.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<Branch>> call, Throwable t) {
                Toast.makeText(CustomerHomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        retHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitClient.logOut();
                finish();
            }
        });

        newShop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                branchid = branchlist.get(i).getId();
                launchActivity("Shop");
            }
        });

        shopHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity("History");
            }
        });

    }

    @Override
    public void onBackPressed() {
        RetrofitClient.logOut();
        finish();
    }

    private void launchActivity(String s) {
        if (s.equals("Shop")){
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
            intent.putExtra(BarcodeCaptureActivity.AutoCapture, true);
            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        } else if (s.equals("Additem")){
            Intent intent = new Intent(this, NewShopActivity.class);
            intent.putExtra("Barcode", barc);
            intent.putExtra("Name", curitem.getName());
            intent.putExtra("Quantity", curitem.getQuantity());
            intent.putExtra("Price", curitem.getPrice());
            intent.putExtra("UserID", branchid);
            intent.putExtra("User", username);
            intent.putExtra("InCart", inCart);
            intent.putExtra("CartQuantity", cartQuantity);
            startActivity(intent);
        } else if (s.equals("History")){
            Intent intent = new Intent(this, CusHistoryActivity.class);
            intent.putExtra("User", username);
            finish();
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    barc = barcode.displayValue;
                    MyWebappServices myWebappServices = ApiUtils.getAPIService();
                    Call<SItem> item = myWebappServices.lookup(branchid, barc);
                    progress.setMessage("Searching Item....");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    progress.show();
                    item.enqueue(new Callback<SItem>() {
                        @Override
                        public void onResponse(Call<SItem> call, Response<SItem> response) {
                            if(response.isSuccessful()) {
                                curitem = response.body();
                                MyWebappServices myWebappServices1 = ApiUtils.getAPIService();
                                Call<SCEntry> scentry = myWebappServices1.scLookup(username, barc);
                                scentry.enqueue(new Callback<SCEntry>() {
                                    @Override
                                    public void onResponse(Call<SCEntry> call, Response<SCEntry> response) {
                                        if(response.isSuccessful()){
                                            inCart = true;
                                            cartQuantity = response.body().getQuantity();
                                        }else {
                                            inCart = false;
                                            cartQuantity = 0;
                                        }
                                        finish();
                                        launchActivity("Additem");
                                    }

                                    @Override
                                    public void onFailure(Call<SCEntry> call, Throwable t) {
                                        Toast.makeText(CustomerHomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else
                            {
                                Toast.makeText(CustomerHomeActivity.this, "Failed to find Scanned Item", Toast.LENGTH_SHORT).show();
                            }
                            progress.dismiss();
                        }

                        @Override
                        public void onFailure(Call<SItem> call, Throwable t) {
                            Toast.makeText(CustomerHomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            progress.dismiss();
                        }
                    });

                } else {
                    barc = "no barcode found";
                }
            } else {
                barc = "no barcode found";
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
