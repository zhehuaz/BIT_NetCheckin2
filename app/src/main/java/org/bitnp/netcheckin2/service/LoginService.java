package org.bitnp.netcheckin2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

public class LoginService extends Service {

    private final static String TAG = "LoginService";

    private SharedPreferencesManager mManager;

    public LoginService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "Service started");
        mManager = new SharedPreferencesManager(LoginService.this);

        LoginHelper.setAccount(mManager.getUsername(), mManager.getPassword());


    }
}
