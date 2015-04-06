package org.bitnp.netcheckin2.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.bitnp.netcheckin2.service.LoginService;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

public class WifiChangedReceiver extends BroadcastReceiver {
    
    private final static String TAG = "WifiChangedReceiver";
    
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    public WifiChangedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Wifi status changed");
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(!mWifiManager.isWifiEnabled()) {
            callBackToService(context, LoginService.COMMAND_STOP_LISTEN);
            return;
        }

        String currentSSID = mWifiManager.getConnectionInfo().getSSID();
        Log.d(TAG, "Start to check ssid list");
        if(new SharedPreferencesManager(context).isAutoLogin(currentSSID)){
            Log.i(TAG, "WIFI check ok");
            callBackToService(context, LoginService.COMMAND_DO_TEST);
        }
    }

    private void callBackToService(Context context, String action){
        Log.d(TAG, "Message to service " + action);
        Intent service = new Intent(context, LoginService.class);
        service.putExtra("command", action);
        context.startService(service);
    }

}
