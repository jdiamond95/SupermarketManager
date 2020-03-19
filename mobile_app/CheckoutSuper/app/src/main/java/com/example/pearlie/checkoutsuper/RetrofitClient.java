package com.example.pearlie.checkoutsuper;

import android.text.TextUtils;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nicolasjoukhdar on 3/10/17.
 */

public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static  OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static Boolean authenticate(String username, String password){
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            String authToken = Credentials.basic(username, password);
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);
            }
            return true;
        }
        return false;
    }

    public static void logOut(){
        retrofit = null;
        httpClient.interceptors().clear();
    }

    public static Retrofit getClient(String baseUrl) {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

}
