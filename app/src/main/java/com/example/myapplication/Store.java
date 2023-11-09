package com.example.myapplication;


import com.google.gson.annotations.SerializedName;
//klash store
public class Store  {
    //serializableName einai to onoma pou erxetai apo to json apo ton server
   @SerializedName("id")
    int id;
   @SerializedName("lat")
    double lat;
   @SerializedName("lon")
    double lon;
   @SerializedName("name")
    String name;
   @SerializedName("phone")
    String phone;
   @SerializedName("address")
    String address;

    public Store(int id, double lat, double lon, String name, String phone, String address) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}


//[
//        {
//        "id": 1,
//        "name": "Kotsovolos",
//        "phone": "2103456390",
//        "address": "Perikleous 14",
//        "lat": 37.989676,
//        "lon": 23.729342
//
//        },
//        {
//        "id": 2,
//        "name": "Plaisio",
//        "phone": "2106534907",
//        "address": "Perikleous 159",
//        "lat": 37.985658,
//        "lon":23.728430
//
//        },
//        {
//        "id":3,
//        "name": "Public",
//        "phone": "2106656190",
//        "address": "3ης Σεπτεμβρίου 5",
//        "lat": 37.985286,
//        "lon": 23.728216
//
//        }
//        ]