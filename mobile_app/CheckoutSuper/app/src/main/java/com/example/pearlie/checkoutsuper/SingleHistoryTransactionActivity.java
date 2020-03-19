package com.example.pearlie.checkoutsuper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleHistoryTransactionActivity extends AppCompatActivity {
    private ListView itemList;
    private Button retBut;
    private int transactionID;
    private ArrayList<String> dataList;
    private ArrayAdapter adapter;
    private Double finalprice;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_history_transaction);

        itemList = (ListView)findViewById(R.id.listView1);
        retBut = (Button)findViewById(R.id.button16);
        retBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        transactionID = extras.getInt("transID");
        finalprice = extras.getDouble("totPrice");
        dataList = new ArrayList<>();


        MyWebappServices myWebappServices = ApiUtils.getAPIService();
        final Call<Transaction> item = myWebappServices.getSingHist(transactionID);
        item.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (response.isSuccessful()){
                    List<Transaction.TransactionData> currData = response.body().getTransactionData();
                    for (Transaction.TransactionData t: currData){
                        dataList.add("Item: " + t.getItem_name() + "     Quantity: " + t.getQuantity());
                    }
                    dataList.add("Total Price: "+ Double.toString(finalprice));
                    adapter = new ArrayAdapter<>(itemList.getContext(), android.R.layout.simple_list_item_1,dataList);
                    itemList.setAdapter(adapter);
                } else {
                    Toast.makeText(SingleHistoryTransactionActivity.this, "Could not get Transaction with http request", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                Toast.makeText(SingleHistoryTransactionActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
