package com.example.pearlie.checkoutsuper;
/**
 * Created by nicolasjoukhdar on 3/10/17.
 */

public class ApiUtils {
    private ApiUtils() {}

    public static final String BASE_URL = "http://192.168.43.120:8000/";

    public static MyWebappServices getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(MyWebappServices.class);
    }
}
