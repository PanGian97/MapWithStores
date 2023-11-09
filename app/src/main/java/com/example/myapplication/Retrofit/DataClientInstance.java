package com.example.myapplication.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataClientInstance {


    private static Retrofit retrofit;

//base url apo tou server pou travame ta stores( einai demo giauto kai xrhsimopoioume ena aplo json host)

    private static final String BASE_URL = "https://api.myjson.com/bins/";

    public static Retrofit getRetrofitDataInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit;
    }
    //se periptwsh pou xreiastoume to base url
    public static String getBaseUrl() {
        return BASE_URL;
    }

}