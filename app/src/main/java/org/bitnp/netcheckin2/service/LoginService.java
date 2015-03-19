package org.bitnp.netcheckin2.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.network.LoginStateListener;
import org.bitnp.netcheckin2.util.ConnTest;
import org.bitnp.netcheckin2.util.ConnTestCallBack;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

import java.util.Timer;
import java.util.TimerTask;

public class LoginService extends Service implements ConnTestCallBack,LoginStateListener{

    private final static String TAG = "LoginService";

    public final static String START_LISTEN = "START LISTEN";

    public final static String STOP_LISTEN = "STOP_LISTEN";

    private boolean listeningFlag = false;

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
        LoginHelper.registerListener(this);

        /*
        interval = mManager.getAutoCheckTime();
        keepAliveFlag = mManager.getIsAutoCheck();
        */
        //TODO only for debug
        interval = 5 * 60 * 1000;
        keepAliveFlag = true;
        // TODO

        LoginHelper.setAccount(mManager.getUsername(), mManager.getPassword());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Log.d(TAG, "receive message in onStartCommand " + intent.getAction());
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case START_LISTEN:
                        startListen();
                        break;
                    case STOP_LISTEN:
                        stopListen();
                        break;
                    default:
                        Log.e(TAG, "Unknown action received");
                }
            }
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
        if(keepAliveFlag && (listeningFlag == false)) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "Run in timer task");
                    ConnTest.test(LoginService.this);
                }
            };
            timer.schedule(timerTask, 0, interval);
        }
        listeningFlag = true;
    }

    private void stopListen(){
        if(timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
        listeningFlag = false;
    }

    @Override
    public void onLoginStateChanged(String message, int state) {
        Log.d(TAG, "Login state is : " + message);

        switch (state) {
            case LoginHelper.OFFLINE:
                stopListen();
                break;
            case LoginHelper.LOGIN_MODE_1:
                Log.i(TAG, "login in mode 1");
                break;
            case LoginHelper.LOGIN_MODE_2:
                Log.i(TAG, "login in mode 2");
                break;
            default:
                Log.e(TAG, "unknown login state");
        }
    }
}
