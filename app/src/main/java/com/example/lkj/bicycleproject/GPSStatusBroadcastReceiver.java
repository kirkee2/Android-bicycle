package com.example.lkj.bicycleproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

/**
 * Created by leegunjoon on 2016. 11. 18..
 */
public class GPSStatusBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
        if(isEntering)
            Toast.makeText(context, "목표 지점에 접근중..", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "목표 지점에서 벗어납니다.", Toast.LENGTH_LONG).show();
    }
}
