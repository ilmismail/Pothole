package com.example.shakedemowithgraph;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
//import androidx.room.Room;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_UPDATE_INTERVAL = 30;
    private static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSION_FINE_LOCATION = 99;
    TextView txtLng, txtLat, txt_acceleration;

    //define the sensor variables
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagneticField;
    private Sensor mGravity;
    private long lastUpdate;
//    private LocationDatabase db;
//    private LocationDao dao;


    //getcurrentlatlon

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


//    private float[] deviceRelativeAcceleration = new float[4];
//    private float[] gravityValues = new float[3];
//    private float[] magneticValues = new float[3];

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];
    private float[] mGravityData = new float[3];

    private float[] linear_acceleration = new float[3];

    //    private float[] gravity = new float[3];
//    private float[] geomagnetic = new float[3];
    float[] rotation = new float[9];
    float[] inclination = new float[9];

    private static double magneticDeclination;
    private static double bearing;
    private static double teta;


//    private final float[] rotationMatrix = new float[9];
//    private final float[] orientationAngles = new float[3];

    private float accelerationCurrentValue = 2f;
    private float accelerationPreviousValue = 2f;

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
//            deviceRelativeAcceleration[0] = x;
//            deviceRelativeAcceleration[1] = y;
//            deviceRelativeAcceleration[2] = z;
//            deviceRelativeAcceleration[3] = 0;
//
            //convert Accelerometer from phone coordinate system to earth coordinate system.
//            float[] R = new float[16], I = new float[16], earthAcc = new float[16];
//
//            SensorManager.getRotationMatrix(R, I, gravityValues, magneticValues);
//
//            float[] inv = new float[16];
//
//            Matrix.invertM(inv, 0, R, 0);
//            Matrix.multiplyMV(earthAcc, 0, inv, 0, deviceRelativeAcceleration, 0);
//
//            float[] earthAccFinal = new float[3];
//            earthAccFinal[0] = earthAcc[0];
//            earthAccFinal[1] = earthAcc[1];
//            earthAccFinal[2] = earthAcc[2];

//            final float alpha = (float) 0.8;
////
//            // Isolate the force of gravity with the low-pass filter.
//            gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
//            gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
//            gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];
//
//            // Remove the gravity contribution with the high-pass filter.
//            linear_acceleration[0] = sensorEvent.values[0] - gravity[0];
//            linear_acceleration[1] = sensorEvent.values[1] - gravity[1];
//            linear_acceleration[2] = sensorEvent.values[2] - gravity[2];

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
//            final float alpha = (float) 0.8;
////
//            // Isolate the force of gravity with the low-pass filter.
//            mGravityData[0] = alpha * mGravityData[0] + (1 - alpha) * sensorEvent.values[0];
//            mGravityData[1] = alpha * mGravityData[1] + (1 - alpha) * sensorEvent.values[1];
//            mGravityData[2] = alpha * mGravityData[2] + (1 - alpha) * sensorEvent.values[2];
//
//            // Remove the gravity contribution with the high-pass filter.
//            linear_acceleration[0] = sensorEvent.values[0] - mGravityData[0];
//            linear_acceleration[1] = sensorEvent.values[1] - mGravityData[1];
//            linear_acceleration[2] = sensorEvent.values[2] - mGravityData[2];


//            float[] rotationMatrix = new float[9];
//            boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,
//                    null, mAccelerometerData, mMagnetometerData);
////
//            float orientationValues[] = new float[3];
//            if (rotationOK) {
//                SensorManager.getOrientation(rotationMatrix, orientationValues);
//            }

            SensorManager.getRotationMatrix(rotation, inclination, mGravityData,
                    mMagnetometerData);
            double geometryAx = rotation[0] * sensorEvent.values[0] + rotation[1] * sensorEvent.values[1] + rotation[2] * sensorEvent.values[2];
            double geometryAy = rotation[3] * sensorEvent.values[0] + rotation[4] * sensorEvent.values[1] + rotation[5] * sensorEvent.values[2];
            double geometryAz = rotation[6] * sensorEvent.values[0] + rotation[7] * sensorEvent.values[1] + rotation[8] * sensorEvent.values[2];

//            float latitude = MobileSensors.getLat();
//            float longitude = MobileSensors.getLon();
//            float altitude = MobileSensors.getAlt();
//            long timeMilis = System.currentTimeMillis();
//
//            GeomagneticField geomagneticField = new GeomagneticField(latitude, longitude, altitude,timeMilis);
//
//            float magneticDeclination = geomagneticField.getDeclination();
//
//            float bearing  = currentLocation.bearingTo(location);


//            float teta1 = (float) getTeta();
//            double ay = geometryAy * Math.cos(teta1) - geometryAx * Math.sin(teta1);
//            double ax = geometryAy * Math.sin(teta1) + geometryAx * Math.cos(teta1);
            double az = geometryAz;


//            double ay = linear_acceleration[1] * Math.cos(teta) - linear_acceleration[0] * Math.sin(teta);
//            double ax = linear_acceleration[1] * Math.sin(teta) + linear_acceleration[0] * Math.cos(teta);
//            double az = linear_acceleration[2];
//
//            float azimuth = orientationValues[0];
//            float pitch = orientationValues[1];
//            float roll = orientationValues[2];
//
//
//            float trueAccelx =(float) (sensorEvent.values[0]*(Math.cos(orientationValues[2])*Math.cos(orientationValues[0])+Math.sin(orientationValues[2])*Math.sin(orientationValues[1])*Math.sin(orientationValues[0])) + sensorEvent.values[1]*(Math.cos(orientationValues[1])*Math.sin(orientationValues[0])) + sensorEvent.values[2]*(-Math.sin(orientationValues[2])*Math.cos(orientationValues[0])+Math.cos(orientationValues[2])*Math.sin(orientationValues[1])*Math.sin(orientationValues[0])));
//            float trueAccely = (float) (sensorEvent.values[0]*(-Math.cos(orientationValues[2])*Math.sin(orientationValues[0])+Math.sin(orientationValues[2])*Math.sin(orientationValues[1])*Math.cos(orientationValues[0])) + sensorEvent.values[1]*(Math.cos(orientationValues[1])*Math.cos(orientationValues[0])) + sensorEvent.values[2]*(Math.sin(orientationValues[2])*Math.sin(orientationValues[0])+ Math.cos(orientationValues[2])*Math.sin(orientationValues[1])*Math.cos(orientationValues[0])));
//            float trueAccelz = (float) (sensorEvent.values[0]*(Math.sin(orientationValues[2])*Math.cos(orientationValues[1])) + sensorEvent.values[1]*(-Math.sin(orientationValues[1])) + sensorEvent.values[2]*(Math.cos(orientationValues[2])*Math.cos(orientationValues[1])));


//            double mag = Math.sqrt(geometryAx*geometryAx + geometryAy*geometryAy + geometryAz*geometryAz);
//            double mag = Math.sqrt(x*x + y*y + z*z);
//            double mag = Math.sqrt(linear_acceleration[0]*linear_acceleration[0] + linear_acceleration[1]*linear_acceleration[1] + linear_acceleration[2]*linear_acceleration[2]);
//            double mag = Math.sqrt(azimuth*azimuth + pitch*pitch + roll*roll);
//            double mag = Math.sqrt(earthAccFinal[0]*earthAccFinal[0] + earthAccFinal[1]*earthAccFinal[1] + earthAccFinal[2]*earthAccFinal[2]);
//            double mag = Math.sqrt(earthAcc[0]*earthAcc[0] + earthAcc[1]*earthAcc[1] + earthAcc[2]*earthAcc[2]);
//            double mag = Math.sqrt(ay*ay + ax*ax + az*az);
//            double mag = Math.sqrt(sensorEvent.values[0]*sensorEvent.values[0] + sensorEvent.values[1]*sensorEvent.values[1] + sensorEvent.values[2]*sensorEvent.values[2]);
//            double mag = Math.sqrt(sensorEvent.values[2]*sensorEvent.values[2]);
//            double mag = Math.sqrt(trueAccelx*trueAccelx + trueAccely*trueAccely + trueAccelz*trueAccelz);

//            double changeInAcceleration = (float) (az);

//            accelerationCurrentValue = (float) (az);
            double changeInAcceleration = (float) (az);
//            accelerationPreviousValue = accelerationCurrentValue;

            //update text views
//            txt_currentAccel.setText("Current = " + (int)accelerationCurrentValue);
//            txt_prevAccel.setText("Prev = " + (int)accelerationPreviousValue);
            txt_acceleration.setText("Acceleration change = " + changeInAcceleration);

//            txt_currentAccel.setText("Current = " + accelerationCurrentValue);
//            txt_prevAccel.setText("Prev = " + accelerationPreviousValue);
//            txt_acceleration.setText("Acceleration change = " + changeInAcceleration);

//            prog_shakeMeter.setProgress((int) changeInAcceleration);
//            long actualTime = System.currentTimeMillis();

            //change colors based on amount of shaking
//            if (changeInAcceleration > 14) {
//                txt_acceleration.setBackgroundColor(Color.RED);
//            }
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
//                dataBaseHelper.insert((float) changeInAcceleration);

            }
//            else if (changeInAcceleration > 2) {
//                txt_acceleration.setBackgroundColor(Color.YELLOW);
//            }
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


        //db
        dataBaseHelper = new DataBaseHelper(MainActivity.this);

        //initialize sensor objects
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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


//        LocationListener locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(@NonNull Location location) {
//                if (lastLocation == null) {
//                    lastLocation = location;
//                }
//                GeomagneticField geoField = new GeomagneticField(
//                        Double.valueOf(location.getLatitude()).floatValue(),
//                        Double.valueOf(location.getLongitude()).floatValue(),
//                        Double.valueOf(location.getAltitude()).floatValue(),
//                        System.currentTimeMillis()
//                );
//                magneticDeclination = geoField.getDeclination();
//                bearing  = location.bearingTo(lastLocation);
//                teta = bearing - magneticDeclination;
//            }
//        };
//
//        LocationManager locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                1000,
//                1,
//                locationListener);
    }

//    public void getLoc(){
//        if (ActivityCompat.checkSelfPermission(MainActivity.this
//                , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(MainActivity.this
//                ,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//
//            getCurrentLocation();
//        }else {
//            ActivityCompat.requestPermissions(MainActivity.this
//                    ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION
//                            ,Manifest.permission.ACCESS_COARSE_LOCATION}
//                    ,100);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        //check condition
//        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1]
//        == PackageManager.PERMISSION_GRANTED)){
//            getCurrentLocation();
//        }else{
//            Toast.makeText(getApplicationContext(),"Permission denied", Toast.LENGTH_SHORT).show();
//        }
//    }

//    @SuppressLint("MissingPermission")
//    private void getCurrentLocation() {
//        LocationManager locationManager = (LocationManager) getSystemService(
//                Context.LOCATION_SERVICE
//        );
//        //check condition
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
//            //when location service is enabled
//            //Get last location
//            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//                @Override
//                public void onComplete(@NonNull Task<Location> task) {
//                    Location location = task.getResult();
//                    //check condition
//                    if (location != null) {
//                        txtLat.setText(String.valueOf(location.getLatitude()));
//                        txtLng.setText(String.valueOf(location.getLongitude()));
//                    }else {
//                        LocationRequest locationRequest = new LocationRequest()
//                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                                .setInterval(10000)
//                                .setFastestInterval(1000)
//                                .setNumUpdates(1);
//                        //initialize location call back
//                        LocationCallback locationCallback = new LocationCallback() {
//                            @Override
//                            public void onLocationResult(@NonNull LocationResult locationResult) {
//                                Location location1 = locationResult.getLastLocation();
//                                txtLat.setText(String.valueOf(location1.getLatitude()));
//                                txtLng.setText(String.valueOf(location1.getLongitude()));
//                            }
//                        };
//                        //Request location updates
//                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
//                    }
//                }
//            });
//        }else{
//            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//        }
//    }

    //    @SuppressLint("MissingPermission")
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
//
//    private void stopLocUpdates() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//    }


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
//        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        if (accelerometer != null) {
//            mSensorManager.registerListener((SensorEventListener) this, accelerometer,
//                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
//        }
//        Sensor magneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        if (magneticField != null) {
//            mSensorManager.registerListener((SensorEventListener) this, magneticField,
//                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
//        }
//        Sensor gravity = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        if (gravity != null) {
//            mSensorManager.registerListener((SensorEventListener) this, magneticField,
//                    SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
//        }
    }



    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }

//    public static double getTeta(){
//        return teta;
//    }
//
}
