package com.example.pearlie.checkoutsuper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Double.parseDouble;

public class AddProductActivity extends AppCompatActivity {

    String bar;
    Button retHome, newAddition;
    TextView barcodeDisplay;
    UserProfile user;
    EditText productName, productPrice, productCategory, productQuantity, productAisle;


    private MyWebappServices myWebappServices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        final Bundle extras = getIntent().getExtras();
        bar = extras.getString("Barcode");


        barcodeDisplay = (TextView)findViewById(R.id.textView2);
        retHome = (Button)findViewById(R.id.button14);
        newAddition = (Button)findViewById(R.id.button13);
        productName = (EditText)findViewById(R.id.editText4);
        productPrice = (EditText)findViewById(R.id.editText5);
        productCategory = (EditText) findViewById(R.id.editText3);
        productQuantity = (EditText) findViewById(R.id.editText9);
        productAisle = (EditText) findViewById(R.id.editText10);



        Log.d("App",extras.getString("Barcode"));
        barcodeDisplay.setText("Barcode: " + extras.getString("Barcode"));

        myWebappServices = ApiUtils.getAPIService();

        retHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActitity();
            }
        });

        newAddition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast confirmMessage = Toast.makeText(getApplicationContext(),
//                        "Product Successfully added\n" + "Name: " + productName.getText() + "\n" +
//                        "Price: " + productPrice.getText() + "\nCategory: " + productCategory.getText() +
//                        "\nBarcode: " + extras.getString("Barcode"),Toast.LENGTH_SHORT);
//                confirmMessage.show();
                Product newProduct = new Product(bar,productName.getText().toString(),productCategory.getText().toString());
                create(newProduct);
            }
        });



    }

    public void create(Product product) {
        myWebappServices.create(product).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {

                if(response.isSuccessful()) {
                }else
                {
                    if(response.code() != 400){
                        Toast.makeText(AddProductActivity.this, "failed with response" + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Item item = new Item(bar, getIntent().getExtras().getInt("UserID"), Integer.parseInt(productQuantity.getText().toString()), Double.parseDouble(productPrice.getText().toString()), Integer.parseInt(productAisle.getText().toString()));
                myWebappServices.add(item.getSupermarketBranch(), item).enqueue(new Callback<Item>() {
                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        if(response.isSuccessful())
                            Toast.makeText(AddProductActivity.this, "Successfully added item to database!", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(AddProductActivity.this, "Failed to add item with response " + response.code(), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Item> call, Throwable t) {
                        Toast.makeText(AddProductActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast confirmMessage = Toast.makeText(getApplicationContext(), "Failed to create item!", Toast.LENGTH_LONG);
                confirmMessage.show();
            }
        });
    }


    private void launchActitity() {

//            Intent intent = new Intent(this, ManagerHomeActivity.class);
////            intent.putExtra("UserID", user.retID());
//            startActivity(intent);
        finish();
    }
}
