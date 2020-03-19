package com.example.pearlie.checkoutsuper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductActivity extends AppCompatActivity {

    TextView pname;
    Button confirmBut, deleteBut;
    EditText priceSet, quantSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        pname = (TextView)findViewById(R.id.textView5);
        confirmBut = (Button)findViewById(R.id.button15);
        priceSet = (EditText)findViewById(R.id.editText7);
        quantSet = (EditText) findViewById(R.id.editText6);
        deleteBut = (Button) findViewById(R.id.button17);

        final Bundle extras = getIntent().getExtras();
        priceSet.setText(Double.toString(extras.getDouble("barPrice")));
        quantSet.setText(Integer.toString(extras.getInt("barQuantity")));
        pname.setText(extras.getString("barName"));


        confirmBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item = new Item(extras.getString("Barcode"), extras.getInt("UserID"), Integer.parseInt(quantSet.getText().toString()), Double.parseDouble(priceSet.getText().toString()), 1);
                MyWebappServices myWebappServices = ApiUtils.getAPIService();
                Call<Item> edit = myWebappServices.edit(extras.getInt("UserID"), item.getItem(), item);
                edit.enqueue(new Callback<Item>() {
                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        if(response.isSuccessful())
                            finish();
                        else
                            Toast.makeText(EditProductActivity.this, "Failed to edit Item with response " + response.code(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Item> call, Throwable t) {
                        Toast.makeText(EditProductActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item = new Item(extras.getString("Barcode"), extras.getInt("UserID"), Integer.parseInt(quantSet.getText().toString()), Double.parseDouble(priceSet.getText().toString()), 1);
                MyWebappServices myWebappServices = ApiUtils.getAPIService();
                Call<ResponseBody> blank = myWebappServices.delprod(extras.getInt("UserID"), item.getItem());
                blank.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            finish();
                        } else{
                            Toast.makeText(EditProductActivity.this, "Failed to del Item with response", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(EditProductActivity.this, t.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }
}
