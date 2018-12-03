package com.main.retrace.retrace.supportClasses;

import com.google.android.gms.maps.model.LatLng;

/**
 * Custom class to allow the location to be uploaded to the database.
 */
public class LatLngCus {

    /**
     * Latitude of the location.
     */
    private Double latitude;
    /**
     * Longitude of the location.
     */
    private Double longitude;
    /**
     * Longitude of the location.
     */
    private String place;

    /**
     * Empty constructor for Firebase.
     */
    public LatLngCus() {
    }

    /**
     * Constructor with all the parameters.
     *
     * @param latitude  of the location.
     * @param longitude of the location.
     * @param place name.
     */
    public LatLngCus(Double latitude, Double longitude, String place) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.place = place;
    }

    /**
     * Constructor with only coordinates.
     *
     * @param latitude  of the location.
     * @param longitude of the location.
     */
    public LatLngCus(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.place = place;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    /**
     * Custom getter to convert it back to the Google Maps LatLng.
     *
     * @return {@link com.google.android.gms.maps.model.LatLng} LatLng object.
     */
    public LatLng getLatLng() {
        return new com.google.android.gms.maps.model.LatLng(latitude, longitude);
    }
}
