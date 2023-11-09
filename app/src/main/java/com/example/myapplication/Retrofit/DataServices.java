package com.example.myapplication.Retrofit;

import com.example.myapplication.Store;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


//url tou service pou travame ta stores apo to json host (BASE_URL+ 17uru3 = oloklhro to url pou xthpaei to retrofit gia na ferei ta stores
public interface DataServices {
    @GET("17uru3")

    Call<List<Store>> getStores();
}
