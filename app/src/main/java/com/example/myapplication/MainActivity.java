package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.Retrofit.DataClientInstance;
import com.example.myapplication.Retrofit.DataServices;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.GeoApiContext;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final int CALL_PERMISSION_REQUEST_CODE = 1111;

    private static final int CUSTOM_STORE_REQUEST_CODE = 1;

    public static final String SAVED_STORES = "saved_stores";
    public static final String SAVED_STORES_KEY = "saved_stores_key";

    GoogleMap map;
    ArrayList<Store> stores = new ArrayList<>();
    ArrayList<Store> customStores;
    LatLng userMarkerLocation;
    Location currentLocation;
    double custom_store_lat;
    double custom_store_lon;
    private boolean locationPermission = false;
    boolean callPermission = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String storePhoneNumber;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("custom_stores", MODE_PRIVATE);

        //LOAD MAP


        userPermissions();


        if (locationPermission) {

            initMap();
        }

    }



    private void fetchStores() {


//kalei to service gia na ferei ta magazia apo to server

        DataServices service = DataClientInstance.getRetrofitDataInstance().create(DataServices.class);
        Call<List<Store>> callList = service.getStores();

        callList.enqueue(new Callback<List<Store>>() {
            //otan to call einai swsto
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                stores = (ArrayList<Store>) response.body();
                showStores();
            }

            ///otan den einai swsto to call
            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error fetching stores " + t, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: " + t);
            }
        });


    }

    public void userPermissions() {
        String[] permissions = { //permission pou xreiazontai gia thn topo8esia
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        //chekarei ean o xrhsths exei dwsei ta dikaiomata
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationPermission = true;

                //ean den exei dwsei zhtaei ta permission
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationPermission = false;
        switch (requestCode) {

            //ean exei pathsei na dwsei permission
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermission = true;
                    initMap();
                }
            }

            case CALL_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (storePhoneNumber != null) //to avoid unknown crash
                    {
                        callPermission = true;
                        makeCall(storePhoneNumber);
                    }
                }
            }


        }
    }

    public void initMap() {
//initialize map me vash to api key
        Log.d(TAG, "initMap: Initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);//preparing map



    }

    //otan einai etoimos o xarths...
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();

        fetchStores();

        //fortonei ta custom magazia ean yparxoun
        customStores = new ArrayList<>(loadCustomStores());

        if (customStores.size() > 0) showCustomStores();

        if (locationPermission) {
            getDeviceLocation();

            //DEFAULT -> elegxei gia dikaiomata topo8esias pou xreiazetai gia na emafnisei thn topo8esia  (setMyLocationEnabled)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            map.setMyLocationEnabled(true);//emfanizei simadi ston xarth pou vrisketai o xthsths



            //kaleitai otan pati8ei o xarths epimona
            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                @Override
                public void onMapLongClick(LatLng latLng) {

                    Intent intent = new Intent(MainActivity.this, EditActivity.class);

//apo8ikevei syntetagmenes times se metavlites tou magaziou pou 8es na pros8eseis se auth thn topo8esia

                    custom_store_lat = latLng.latitude;
                    custom_store_lon = latLng.longitude;

//xekinaei activity gia na dwsei stoixeia magaziou o xrhsths

                    startActivityForResult(intent, CUSTOM_STORE_REQUEST_CODE);

                }
            });
//energopoieitai otan o xrhsths patisei sto infowindow enos magaziou
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    storePhoneNumber = marker.getSnippet(); //prepei na to apo8ikeusoume wste na mpei san parametro kata th diarkeia pou kanoume to call se auto to noumero
                    reqCallPermission();

                    //ean exei dwsei dikaioma gia call pregmatopoiei klish symfona me to noumero sto Snippet
                    if (callPermission) makeCall(marker.getSnippet());


                }

            });
        }

    }


    public void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting current location ");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            //ean exei dwsei dikaiomata topo8esias
            if (locationPermission) {
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {

                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            //kaleitai otan vrei topo8esia
                            Log.d(TAG, "onComplete: Found current location!");
                            currentLocation = (Location) task.getResult();
                       //apo8ikevoume to shmeio pou vrisketai o xthsths
                            userMarkerLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//kai kinoume thn kamera ekei me zoom wste na deixnei tis poleis(5f)
                            moveCamera(userMarkerLocation, 5f);


                        } else {
                            //den vrike topo8esia
                            Log.d(TAG, "onComplete: Current location is null");
                            Toast.makeText(MainActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException " + e.getMessage());
        }

    }

    private void showCustomStores() {
        //deixnei ta custom stores ean yparxoun
        for (Store store : customStores) {
//
//dhmiourgei marker gia to ka8e magazi
            LatLng markerLocation = new LatLng(store.getLat(), store.getLon());


            Marker marker = map.addMarker(new MarkerOptions()
                    .position(markerLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .title(store.getName() + " " + store.address)
                    .snippet(store.phone));

        }

        //energopoieitai otan pathsoume epimona to infowindow enos magaziou
        map.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                //psaxnei na vrei ean to store pou pathses einai custom kai an einai to diagrafei
                for (Store store : customStores) {
                    if (marker.getPosition().latitude == store.getLat()
                            && marker.getPosition().longitude == store.getLon()) {
                        customStores.remove(store);
                //apo8ykevei thn lista twn customstores xwris ekeino pou
                // diegrapses kai kanei refresh ton map wste na fygei kai to marker
                        saveCustomStores();
                        customStores = loadCustomStores();
                        map.clear();
                        showCustomStores();
                        showStores();
                    }
                }
            }
        });

    }


    private void showStores() {
        //deixnei ta magazia pou travixe apo to server
        for (final Store store : stores) {


            LatLng markerLocation = new LatLng(store.getLat(), store.getLon());

//ftiaxnei ena marker gia ka8e store
            Marker marker = map.addMarker(new MarkerOptions()
                    .position(markerLocation)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.store_marker))
                    .title(store.getName() + "  " + store.address)
                    .snippet(store.phone));


        }//

    }

    private void makeCall(String phoneNumber) {

//prsgmatopoiei klish sto thlefwno to noumero phoneNumber
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", " " + phoneNumber, null));
        startActivity(callIntent);

    }

    private void reqCallPermission() {
        //rwtame gia permission na kanei call(ean den exei eidh dwsei)
        String permissions[] = {Manifest.permission.CALL_PHONE};

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            callPermission = true;


        } else {
            ActivityCompat.requestPermissions(this, permissions, CALL_PERMISSION_REQUEST_CODE);
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        //metaferoume thn kamera symfona me ta lat lon
        Log.d(TAG, "moveCamera: Moving the camera to lat " + latLng.latitude +
                " lng " + latLng.longitude);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));


    }


    public ArrayList<Store> loadCustomStores() {
//fwrtwnei ta custom stores ean yparxoun apo thn mnhmh tou kinhtou

        Gson gson = new Gson();
//psaxnei me fash to key na vrei ta customstores
        String response = sharedPreferences.getString("custom_store_list", "");
        if (response.equals("")) {
            //ean den vrei tpt gyrnaei kenh thn lista
            return new ArrayList<>();
        } else {
            //allios thn gyrnaei me mesa ta custom stores
            ArrayList<Store> data = gson.fromJson(response, new TypeToken<ArrayList<Store>>() {
            }.getType());

            return data;
        }

    }

//kaleitai molis teleiwseis apo to activity pou dinei onoma sto custom store
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//elegxos oti metafer8ike apo to swsto activity
        if (requestCode == CUSTOM_STORE_REQUEST_CODE && resultCode == RESULT_OK) {
//apo8ykeush timwn apo to allo activity (name address kai phone pou edwse prin o xrhsths)
            String customStoreName = data.getStringExtra("custom_store_name");
            String customStorePhone = data.getStringExtra("custom_store_phone");
            String customStoreAddress = data.getStringExtra("custom_store_address");

            //pros8etei to custom store sthn lista me vash twn parapanw...Vazoume id=mege8os pinaka
            // me custom stores gia ena eidos monadikotitas (vevaia den einai panta monadiko)
            customStores.add(new Store(customStores.size(), custom_store_lat, custom_store_lon,
                    customStoreName, customStorePhone, customStoreAddress));

            saveCustomStores();
        //ananewnei thn customstores dioti molis pros8esame allo ena customstore
            customStores = loadCustomStores();
            //kai ta xanaemfanizei
            showCustomStores();
        }

    }

    private void saveCustomStores() {
//apo8ykevei to customstore sto kinhto
        SharedPreferences.Editor editor;

        Gson gson = new Gson();
        String json = gson.toJson(customStores);

        editor = sharedPreferences.edit();
        //diagrafei thn palia lista xwris to neo customstore
        editor.remove("custom_store_list").apply();
        //kai vazei thn kainourgia
        editor.putString("custom_store_list", json);
        editor.commit();
    }


}
