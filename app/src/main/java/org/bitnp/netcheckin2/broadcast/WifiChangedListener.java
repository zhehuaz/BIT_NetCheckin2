package org.bitnp.netcheckin2.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.service.LoginService;
import org.bitnp.netcheckin2.util.ConnTest;
import org.bitnp.netcheckin2.util.ConnTestCallBack;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

public class WifiChangedListener extends BroadcastReceiver {
    
    private final static String TAG = "WifiChangedListener";
    
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;

    public WifiChangedListener() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Wifi status changed");
        
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // TODO what's this?
        mWifiManager.getWifiState();
        if(!mWifiManager.isWifiEnabled()) {
            callBackToService(context, LoginService.ACTION_STOP_LISTEN);
            return;
        }

        mWifiInfo = mWifiManager.getConnectionInfo();
        String currentSSID = mWifiInfo.getSSID();
        Log.d(TAG, "Start to check ssid list");
        if(new SharedPreferencesManager(context).isAutoLogin(currentSSID) && LoginService.isKeepAlive()){
            Log.i(TAG, "WIFI check ok");
            callBackToService(context, LoginService.ACTION_DO_TEST);
        }
    }

    private void callBackToService(Context context, String action){
        Log.d(TAG, "Message to service " + action);
        Intent service = new Intent(context, LoginService.class);
        service.setAction(action);
        context.startService(service);
    }

}
