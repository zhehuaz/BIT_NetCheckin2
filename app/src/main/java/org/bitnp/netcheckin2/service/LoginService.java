package org.bitnp.netcheckin2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.util.ConnTest;
import org.bitnp.netcheckin2.util.ConnTestCallBack;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

import java.util.Timer;
import java.util.TimerTask;

public class LoginService extends Service implements ConnTestCallBack{

    private final static String TAG = "LoginService";

    public final static String START_LISTEN = "START LISTEN";

    private SharedPreferencesManager mManager;
    private static boolean keepAliveFlag;
    private static long interval;

    private Timer timer;
    private TimerTask timerTask;

    public LoginService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static boolean isKeepAlive() {
        return keepAliveFlag;
    }

    public static long getInterval() {
        return interval;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "Service started");
        mManager = new SharedPreferencesManager(LoginService.this);
        timer = new Timer(true);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                ConnTest.test(LoginService.this);
            }
        };
        // TODO read settings from mManager @keepAliveFlag @interval
        LoginHelper.setAccount(mManager.getUsername(), mManager.getPassword());

        if(keepAliveFlag)
            timer.schedule(timerTask, interval);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onTestOver(boolean result) {
        if(!result){


        }
    }
}
