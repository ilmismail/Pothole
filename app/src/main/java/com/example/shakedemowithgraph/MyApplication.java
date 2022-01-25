package com.example.shakedemowithgraph;

import android.app.Application;
import android.location.Location;

//import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    private static MyApplication singleton;

    public List<Location> getMyLocations() {
        return myLocations;
    }

    public void setMyLocations(List<Location> myLocations) {
        this.myLocations = myLocations;
    }

    private List<Location> myLocations;


    public MyApplication getInstance() {
        return singleton;
    }

//    public LocationDatabase db;

    public void onCreate() {
        super.onCreate();
        singleton = this;
        myLocations = new ArrayList<>();
//        db = Room.databaseBuilder(getApplicationContext(), LocationDatabase.class, "locationDb").build();
    }

}
