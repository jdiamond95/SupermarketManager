package com.example.pearlie.checkoutsuper;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.dift.ui.SwipeToAction;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;

public class CheckoutShoppingCartActivity extends AppCompatActivity {

    private ExpandableListView receipt;
    private ArrayList<String> shopCartLine;
    private RecyclerView recyclerView;
    private SwipeToAction swipeToAction;
    private CurItemAdapter entryAdaper;
    private ArrayAdapter adapter;
    private List<SCEntry> transaction;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listDataChild;
    private ProgressDialog waitForEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);

        setContentView(R.layout.activity_checkout_shopping_cart);
        Bundle extras = getIntent().getExtras();
        final String username = extras.getString("User");

        transaction = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        entryAdaper = new CurItemAdapter(transaction);
        recyclerView.setAdapter(entryAdaper);

        swipeToAction = new SwipeToAction(recyclerView, new SwipeToAction.SwipeListener<SCEntry>() {
            @Override
            public boolean swipeLeft(final SCEntry itemData) {
                MyWebappServices services = ApiUtils.getAPIService();
                Call<ResponseBody> del = services.scDelete(username, itemData.getBarcode());
                del.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(CheckoutShoppingCartActivity.this, "Delete was successfull!", Toast.LENGTH_SHORT).show();
                            int pos = transaction.indexOf(itemData);
                            transaction.remove(itemData);
                            entryAdaper.notifyItemRemoved(pos);
                            update_total_price();
                        }else{
                            Toast.makeText(CheckoutShoppingCartActivity.this, "Failed To delete", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                return true;
            }

            @Override
            public boolean swipeRight(final SCEntry itemData) {
                MyWebappServices services = ApiUtils.getAPIService();
                Call<ResponseBody> del = services.scDelete(username, itemData.getBarcode());
                del.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(CheckoutShoppingCartActivity.this, "Delete was successfull!", Toast.LENGTH_SHORT).show();
                            int pos = transaction.indexOf(itemData);
                            transaction.remove(itemData);
                            entryAdaper.notifyItemRemoved(pos);
                            update_total_price();
                        }else{
                            Toast.makeText(CheckoutShoppingCartActivity.this, "Failed To delete", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
                return true;
            }

            @Override
            public void onClick(SCEntry itemData) {

            }

            @Override
            public void onLongClick(SCEntry itemData) {

            }
        });


        shopCartLine = new ArrayList<String>();
        final MyWebappServices myWebappServices = ApiUtils.getAPIService();
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        Call<List<SCEntry>> shoppingCartCall = myWebappServices.getCart(username);
        shoppingCartCall.enqueue(new Callback<List<SCEntry>>() {
            @Override
            public void onResponse(Call<List<SCEntry>> call, Response<List<SCEntry>> response) {
                if(response.isSuccessful()){
//                    double total_price = 0;
                    transaction = response.body();
//                    for(SCEntry e : response.body()){
//                        String entry = "Barcode: " + e.getItem() + ", Quantity " + e.getQuantity();
////                        shopCartLine.add(entry);
////                        listDataHeader.add(entry);
////                        List<String> child = new ArrayList<String>();
////                        child.add("Some random string goes here lol");
////                        child.add("Some other random string lol");
////                        listDataChild.put(entry, child);
//                    }
                    entryAdaper = new CurItemAdapter(transaction);
                    recyclerView.setAdapter(entryAdaper);
                    update_total_price();
                }else{
                    Toast.makeText(CheckoutShoppingCartActivity.this, "Failed to get shopping cart with http code " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SCEntry>> call, Throwable t) {
                Toast.makeText(CheckoutShoppingCartActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        waitForEmail = new ProgressDialog(this,R.style.AppCompatAlertDialogStyle);
        Button confirm = (Button) findViewById(R.id.button3);
        confirm.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                MyWebappServices myWebappServices1 = ApiUtils.getAPIService();
                Call<Transaction> checkout = myWebappServices.checkout(username);
                waitForEmail.setProgressStyle(R.style.AppCompatAlertDialogStyle);
                waitForEmail.setMessage("Waiting for checkout to complete...");
                waitForEmail.show();
                checkout.enqueue(new Callback<Transaction>() {
                    @Override
                    public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                        waitForEmail.dismiss();
                        if(response.isSuccessful()){
                            setResult(RESULT_OK);
                            finish();
                        }else{
                            Toast.makeText(CheckoutShoppingCartActivity.this, "Failed to checkout with http code " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Transaction> call, Throwable t) {
                        Toast.makeText(CheckoutShoppingCartActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button ret = (Button) findViewById(R.id.button18);
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    void update_total_price(){
        double total_price = 0;
        for(SCEntry e : transaction){
            total_price += e.getPrice() * e.getQuantity();
        }
        TextView total = (TextView) findViewById(R.id.textView6);
        total.setText("Total Price:\n" + total_price);
    }

}
