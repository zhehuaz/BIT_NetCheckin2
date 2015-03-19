package org.bitnp.netcheckin2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.util.ConnTest;
import org.bitnp.netcheckin2.util.ConnTestCallBack;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

import java.util.Timer;
import java.util.TimerTask;

public class LoginService extends Service implements ConnTestCallBack{

    private final static String TAG = "LoginService";

    public final static String START_LISTEN = "START LISTEN";

    public final static String STOP_LISTEN = "STOP_LISTEN";

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
        Log.d(TAG, "Get intent in onBind " + intent.getAction());
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
        mManager = new SharedPreferencesManager(this.getApplicationContext());
        timer = new Timer(true);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Run in timer task");
                ConnTest.test(LoginService.this);
            }
        };
        /*
        interval = mManager.getAutoCheckTime();
        keepAliveFlag = mManager.getIsAutoCheck();
        */
        //TODO only for debug
        interval = 5 * 60 * 1000;
        keepAliveFlag = true;
        // TODO

        LoginHelper.setAccount(mManager.getUsername(), mManager.getPassword());

        startListen();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "receive message in onStartCommand " + intent.getAction());
        switch (intent.getAction()){
            case START_LISTEN : startListen();break;
            case STOP_LISTEN : stopListen();break;
            default : Log.e(TAG, "Unknown action received");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTestOver(boolean result) {
        Log.d(TAG, "Connection test : " + (result ? "Connected" : "Disconnected"));
        if(!result){
            LoginHelper.asyncLogin();
        }
    }

    private void startListen(){
        if(keepAliveFlag)
            timer.schedule(timerTask, 0, interval);
    }

    private void stopListen(){
        timerTask.cancel();
    }
}
