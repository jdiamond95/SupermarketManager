package com.example.pearlie.checkoutsuper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CusHistoryActivity extends AppCompatActivity {

    private ListView shopList;
    private Button retHome;
    private String user;
    private ArrayList<String> transactionList;
    private ArrayAdapter adapter;
    private ArrayList<Transaction> completeList;
    private Double totalPrice;
    private int transID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus_history);

        retHome = (Button) findViewById(R.id.button5);
        retHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity("return");
            }
        });


        shopList = (ListView) findViewById(R.id.listView);
        Bundle extras = getIntent().getExtras();
        transactionList = new ArrayList<String>();
        completeList = new ArrayList<Transaction>();

        user = extras.getString("User");
        MyWebappServices myWebappServices = ApiUtils.getAPIService();
        Call<List<Transaction>> item = myWebappServices.getAllHist(user);
        item.enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(Call<List<Transaction>> call, Response<List<Transaction>> response) {

                if (response.isSuccessful()){
                    List<Transaction> curlist = response.body();
                    for (Transaction t : curlist){
                        transactionList.add(0,"Time: " + t.getTime() + "     Total Price: " + Double.toString(t.getTotal_price()));
                        completeList.add(0,t);
                    }
                    adapter = new ArrayAdapter(shopList.getContext(),android.R.layout.simple_list_item_1 , transactionList);
                    shopList.setAdapter(adapter);
                } else {
                    Toast.makeText(CusHistoryActivity.this, "Failed to get shopping history with http code " + response.code(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<List<Transaction>> call, Throwable t) {
                Toast.makeText(CusHistoryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        shopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Transaction t = completeList.get(position);
                totalPrice = t.getTotal_price();
                transID = t.getId();
                launchActivity("transData");
            }
        });

    }
    private void launchActivity(String s){
        if (s.equals("transData")){
            Intent intent = new Intent(this, SingleHistoryTransactionActivity.class);
            intent.putExtra("transID",transID);
            intent.putExtra("totPrice", totalPrice);
            startActivity(intent);
        }else if(s.equals("return")){
            Intent intent = new Intent(this, CustomerHomeActivity.class);
//            intent.putExtra("UserID", user.retID());
            intent.putExtra("User", user);
            finish();
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        launchActivity("return");
    }
}
