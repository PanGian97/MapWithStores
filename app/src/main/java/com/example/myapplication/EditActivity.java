package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.example.myapplication.MainActivity.SAVED_STORES;
import static com.example.myapplication.MainActivity.SAVED_STORES_KEY;

public class EditActivity extends AppCompatActivity {


    TextView name;
    TextView phone;
    TextView address;
    Button saveBtn;

    String markerName;
    String markerPhone;
    String markerAddress;


    private static final String TAG = "EditActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        //vriskei ta editexts apo to xml
        name = findViewById(R.id.cust_store_name);
        phone = findViewById(R.id.cust_store_phone);
        address = findViewById(R.id.cust_store_address);

        saveBtn = findViewById(R.id.save_store_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();


//otan pathsoume to koumpi save pairnoume tis times
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markerName = name.getText().toString();
                markerPhone = phone.getText().toString();
                markerAddress = address.getText().toString();
            backToMap();

            }
        });
    }

    private void backToMap() {

        //epistrefoume ston xarth me tis times apo mta pedia
        Intent intent = getIntent();
        intent.putExtra("custom_store_name",markerName);
        intent.putExtra("custom_store_phone",markerPhone);
        intent.putExtra("custom_store_address",markerAddress);
        setResult(RESULT_OK,intent);
        finish();
    }



}
