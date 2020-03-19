package com.example.pearlie.checkoutsuper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button loginBut;
    private EditText userText, passText;
    private TextView errorText;
    private UserProfile user;
    private boolean authSuccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBut = (Button)findViewById(R.id.button);
        userText = (EditText)findViewById(R.id.editText);
        passText = (EditText)findViewById(R.id.editText2);
        errorText = (TextView)findViewById(R.id.textView);

        errorText.setVisibility(View.GONE);

        loginBut.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                correctlogin(userText.getText().toString().trim(),passText.getText().toString());
            }


        });


    }



    private void launchActitity(Boolean s) {
        System.out.println(s);
        if (!s){
            Intent intent = new Intent(this, CustomerHomeActivity.class);
//            intent.putExtra("UserID", user.getManager().get(0));
            intent.putExtra("User", user.getUsername());
            startActivity(intent);
        } else{
            Intent intent = new Intent(this, ManagerHomeActivity.class);
            intent.putExtra("UserID", user.getManager().get(0));
            startActivity(intent);
        }
    }

    private boolean correctlogin(String name, String password) {
        //TODO
        //change to correct userProfile
//        user = new UserProfile(name, "1");
        if(RetrofitClient.authenticate(name, password)){
            MyWebappServices myWebappServices = ApiUtils.getAPIService();
            Call<UserProfile[]> auth = myWebappServices.auth();
            authSuccess = false;
            auth.enqueue(new Callback<UserProfile[]>() {
                @Override
                public void onResponse(Call<UserProfile[]> call, Response<UserProfile[]> response) {
                    if(response.isSuccessful()){
                        user = response.body()[0];
                        errorText.setVisibility(View.GONE);
                        launchActitity(!user.getManager().isEmpty());
                    }else{
                        Toast.makeText(LoginActivity.this, "Failed to Authenticate", Toast.LENGTH_SHORT).show();
                        RetrofitClient.logOut();
                    }
                }

                @Override
                public void onFailure(Call<UserProfile[]> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return authSuccess;
        }
        return false;
    }


}
