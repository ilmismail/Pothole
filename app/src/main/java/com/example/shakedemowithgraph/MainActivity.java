package com.example.shakedemowithgraph;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
//import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_UPDATE_INTERVAL = 30;
    private static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;
    TextView txtLng, txtLat, txt_acceleration;
    Button btn_showMap;

    //define the sensor variables
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagneticField;
    private Sensor mGravity;


    //request location
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;

    //current location
    Location currentLocation;
    //List of saved location
    List<Location> savedLocation;

    private Location lastLocation;
    DataBaseHelper dataBaseHelper;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference("pothole");

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];
    private float[] mGravityData = new float[3];

    float[] rotation = new float[9];
    float[] inclination = new float[9];

    private int pointsPlotted = 5;
    private int graphIntervalCounter = 0;

    private int threshold = 0;

    private Viewport viewport;


    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
    });

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
//

            int sensorType = sensorEvent.sensor.getType();
            switch (sensorType) {
                case Sensor.TYPE_ACCELEROMETER:
                    mAccelerometerData = sensorEvent.values.clone();
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mMagnetometerData = sensorEvent.values.clone();
                    break;
                case Sensor.TYPE_GRAVITY:
                    mGravityData = sensorEvent.values.clone();
                    break;
                default:
                    return;

            }

            SensorManager.getRotationMatrix(rotation, inclination, mGravityData,
                    mMagnetometerData);
            double geometryAx = rotation[0] * sensorEvent.values[0] + rotation[1] * sensorEvent.values[1] + rotation[2] * sensorEvent.values[2];
            double geometryAy = rotation[3] * sensorEvent.values[0] + rotation[4] * sensorEvent.values[1] + rotation[5] * sensorEvent.values[2];
            double geometryAz = rotation[6] * sensorEvent.values[0] + rotation[7] * sensorEvent.values[1] + rotation[8] * sensorEvent.values[2];


            double az = geometryAz;
//            double ay = geometryAy * Math.cos(teta1) - geometryAx * Math.sin(teta1);
//            double ax = geometryAy * Math.sin(teta1) + geometryAx * Math.cos(teta1);
//            accelerationCurrentValue = (float) (az);
            double changeInAcceleration = (float) (az);
//            accelerationPreviousValue = accelerationCurrentValue;

            //update text views
//            txt_currentAccel.setText("Current = " + (int)accelerationCurrentValue);
//            txt_prevAccel.setText("Prev = " + (int)accelerationPreviousValue);
            txt_acceleration.setText("Acceleration change = " + changeInAcceleration);


//            long actualTime = System.currentTimeMillis();


            if (changeInAcceleration <= threshold) {
//                if (actualTime - lastUpdate < 200){
//                    return;
//                }
//                lastUpdate = actualTime;
                Toast.makeText(MainActivity.this, "Pothole Detected", Toast.LENGTH_SHORT).show();
                txt_acceleration.setBackgroundColor(Color.parseColor("#fcad03"));
                startLocUpdates();
                MyApplication myApplication = (MyApplication) getApplicationContext();
                savedLocation = myApplication.getMyLocations();
                savedLocation.add(currentLocation);

                String lat = null;
                String lng = null;
                if (currentLocation != null) {
                    lat = Double.toString(currentLocation.getLatitude());
                    lng = Double.toString(currentLocation.getLongitude());
                }

                dataBaseHelper.insert((float) changeInAcceleration, lat, lng);

                String accel = Float.toString((float) changeInAcceleration);
                Date date = new Date();
                SimpleDateFormat DateFor = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String stringDate= DateFor.format(date);

                HashMap<String, String> potholeMap = new HashMap<>();

                potholeMap.put("Accel", accel);
                potholeMap.put("Latitude", lat);
                potholeMap.put("Longitude", lng);
                potholeMap.put("Timestamp", java.text.DateFormat.getDateTimeInstance().format(date));
                root.child(stringDate).setValue(potholeMap);

                Log.d("test", potholeMap.toString());
            }
            else {
                txt_acceleration.setBackgroundColor(Color.parseColor("#ffffff"));
            }

            //update the graph
            pointsPlotted++;
            series.appendData(new DataPoint(pointsPlotted, changeInAcceleration), true, pointsPlotted);
            viewport.setMaxX(pointsPlotted);
            viewport.setMinX(pointsPlotted - 200);

        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        db = Room.databaseBuilder(getApplicationContext(), LocationDatabase.class, "locationDb").build();
//        dao = db.locationDao();

        txt_acceleration = findViewById(R.id.txt_accel);
        txtLat = findViewById(R.id.txtLatValues);
        txtLng = findViewById(R.id.txtLngValues);
        btn_showMap = findViewById(R.id.btnMaps);


        //db
        dataBaseHelper = new DataBaseHelper(MainActivity.this);

        //initialize sensor objects
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);


        //sample graph code
        GraphView graph = (GraphView) findViewById(R.id.graph);
        viewport = graph.getViewport();
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        graph.addSeries(series);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);


        //set all properties
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //callback
        locationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateValues(locationResult.getLastLocation());
            }
        };

        btn_showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });



    }


    private void startLocUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_FINE_LOCATION:
                if (grantResults [0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                } else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void updateGPS() {
        //get permission


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(@NonNull Location location) {
                            updateValues(location);
                            currentLocation = location;
                        }
                    });
        } else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_FINE_LOCATION);
            }
        }
    }
//
    private void updateValues(Location location) {
        //update a new location
//        LocationData data = new LocationData();
//        double lat = location.getLatitude();
//        double lng = location.getLongitude();
        txtLat.setText(String.valueOf(location.getLatitude()));
        txtLng.setText(String.valueOf(location.getLongitude()));

    }


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(sensorEventListener, mMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(sensorEventListener, mGravity, SensorManager.SENSOR_DELAY_NORMAL);

    }



    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

}
