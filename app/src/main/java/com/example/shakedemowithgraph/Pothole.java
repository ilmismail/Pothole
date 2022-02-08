package com.example.shakedemowithgraph;

public class Pothole {
    private String Accel;
    private String Latitude;
    private String Longitude;
    private String Timestamp;

    public Pothole(){}

    public Pothole(String accel, String latitude, String longitude, String timestamp) {
        Accel = accel;
        Latitude = latitude;
        Longitude = longitude;
        Timestamp = timestamp;
    }

    public String getAccel() {
        return Accel;
    }

    public void setAccel(String accel) {
        Accel = accel;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }
}
