package com.fusedlocationproviderclient_srs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.fusedlocationproviderclient_srs.util.LocationProviderPerDistance;
import com.google.android.gms.maps.model.LatLng;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityA extends AppCompatActivity implements LocationProviderPerDistance.ILocationCallback{

    int time;
    TextView timerTV;
    TextView locTV;
    StringBuilder textLocation = new StringBuilder();
    CountDownTimer c = null;
    private LocationProviderPerDistance locProvider;

    private static final int DISTANCE_IN_METER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        lbm();
        timerTV = findViewById(R.id.timer);
        locTV = findViewById(R.id.locations);
        runTimer();
        locProvider = new LocationProviderPerDistance(this, this, DISTANCE_IN_METER);
        updateLocationTextView("OnCreate");
    }

    //______________________________________________________________________________________________
    @Override
    protected void onResume() {
        super.onResume();
        locProvider.requestLocationUpdates();
        updateLocationTextView("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateLocationTextView("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateLocationTextView("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locProvider.removeLocationUpdates();
        updateLocationTextView("onDestroy");
    }

    //______________________________________________________________________________________________
    public void btnB(View view) {
        startActivity(new Intent(this, ActivityB.class));
    }

    //______________________________________________________________________________________________
    void runTimer() {
        time = 30;
        c = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTV.setText("0:" + (time <= 9 ? "0" + time : String.valueOf(time)));
                time--;
            }
            public void onFinish() {
                c.cancel();
                c = null;
                timerTV.setText("again");
                runTimer();
            }
        }.start();
    }

    void updateLocationTextView(String text) {
        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate= dateFormat.format(date);

        textLocation.append(formattedDate + ": ");
        textLocation.append(text + "\n");
        locTV.setText(textLocation);
    }

    //______________________________________________________________________________________________
    @Override
    public void onLocationChanged(LatLng location) {
        updateLocationTextView("Location: " + location.latitude + " , " + location.longitude);
    }

    void lbm() {
        LocalBroadcastManager.getInstance(this).registerReceiver(lbm, new IntentFilter("LBM"));
    }
    private BroadcastReceiver lbm = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent in) {
            updateLocationTextView(in.getStringExtra("STATUS"));
        }
    };
}
