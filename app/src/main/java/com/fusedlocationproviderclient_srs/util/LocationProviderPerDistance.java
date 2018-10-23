package com.fusedlocationproviderclient_srs.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

/**
 * continuously request location per distance travelled in x metres
 * tutorial : https://github.com/codepath/android_guides/wiki/Retrieving-Location-with-LocationServices-API
 * official : https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient#requestLocationUpdates(com.google.android.gms.location.LocationRequest,%20com.google.android.gms.location.LocationCallback)
 * how 2 pause : https://stackoverflow.com/questions/46552087/fusedlocationproviderclient-when-and-how-to-stop-looper
 * developer blog: https://android-developers.googleblog.com/2017/06/reduce-friction-with-new-location-apis.html
 *
 * The "LocationRequest.setSmallestDisplacement" doesn't matter for accuracy, it's just to avoid unnecessary updates, you can continuing using that.
 * https://stackoverflow.com/questions/22365188/locationrequest-smallest-displacement-and-accuracy
 */
public class LocationProviderPerDistance {

    //The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000; //30 seconds
    //The fastest rate for active location updates. Exact. Updates will never be more frequent than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2; //5 seconds

    private ILocationCallback callback;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private Context context;


    public LocationProviderPerDistance(Context context, ILocationCallback callback, float distanceInMeters) {
        this.context = context;
        this.callback = callback;
        setupFusedLocationClient();
        setupLocationRequest(distanceInMeters);

    }
    private void setupFusedLocationClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult lr) {
                LatLng latLng = new LatLng(lr.getLastLocation().getLatitude(), lr.getLastLocation().getLongitude());
                callback.onLocationChanged(latLng);
            }
        };
    }

    private void setupLocationRequest(float distanceInMeters) {
        //Log.e("-->", "setupLocationRequest");
        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //we are only distance bounding the request, req when distance is change
        //1 foot = 0.3048 meter  | 1 meter = 3.280
        //https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest
        mLocationRequest.setSmallestDisplacement(distanceInMeters);
    }

    //______________________________________________________________________________________________
    /*
        Requests location updates with a callback on the specified Looper thread.
        This method is suited for the foreground use cases. For background use cases, the PendingIntent version of the method is recommended, see requestLocationUpdates(LocationRequest, PendingIntent).
     */
    public void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    public void removeLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    //______________________________________________________________________________________________
    public interface ILocationCallback {
        void onLocationChanged(LatLng location);
    }
}

