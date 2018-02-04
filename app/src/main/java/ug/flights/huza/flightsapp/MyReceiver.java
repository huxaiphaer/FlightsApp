package ug.flights.huza.flightsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Huzy_Kamz on 2/4/2018.
 */

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int datapassed = intent.getIntExtra("fromLocation", 0);
        String orgData = intent.getStringExtra("toLocation");


    }
}
