package org.bitnp.netcheckin2.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.network.LoginStateListener;
import org.bitnp.netcheckin2.util.ConnTest;
import org.bitnp.netcheckin2.util.ConnTestCallBack;
import org.bitnp.netcheckin2.util.Global;
import org.bitnp.netcheckin2.util.NotifTools;
import org.bitnp.netcheckin2.util.PreferenceChangedListener;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

import java.util.Timer;
import java.util.TimerTask;

public class LoginService extends Service implements ConnTestCallBack, LoginStateListener, PreferenceChangedListener{
    private final static String TAG = "LoginService";

    public final static String BROADCAST_ACTION = "org.bitnp.netcheckin2.LOGINSERVICE";

    public final static String COMMAND_START_LISTEN = "START LISTEN";
    public final static String COMMAND_STOP_LISTEN = "STOP LISTEN";
    public final static String COMMAND_STATE_CHANGE = "STATE CHANGE";

    public final static String COMMAND_DO_TEST = "DO TEST";

    /** Force logout and login */
    public final static String COMMAND_RE_LOGIN = "RE LOGIN";

    private boolean listeningFlag = false;

    private static NetworkState status = NetworkState.OFFLINE;

    private SharedPreferencesManager mManager;
    private static boolean keepAliveFlag;
    private static boolean autoLogoutFlag;
    private static long interval;
    private static boolean autoLoginFLag;
    private static String uid;
    private static long relog_interval;

    Intent broadcast = new Intent(BROADCAST_ACTION);

    private NotifTools mNotifTools;

    /** Timer for listening network connection state.*/
    private Timer timer;
    private TimerTask timerTask;

    public static float getmBalance() {
        return mBalance;
    }

    private static float mBalance;

    public LoginService() {
    }


    public static NetworkState getStatus() {
        return status;
    }

    public static boolean isKeepAlive() {
        return keepAliveFlag;
    }

    public static long getInterval() {
        return interval;
    }

    public boolean isAutoLogin(){
        WifiManager mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String currentSSID = mWifiManager.getConnectionInfo().getSSID();
        if(!mManager.isAutoLogin(currentSSID))
            return false;
        if(!mWifiManager.isWifiEnabled())
            return false;
        if(!autoLoginFLag)
            return false;
        return true;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Service started");
        mManager = new SharedPreferencesManager(this.getApplicationContext());
        timer = new Timer(true);
        LoginHelper.registerListener(this);
        mNotifTools = NotifTools.getInstance(this.getApplicationContext());

        interval = mManager.getAutoCheckTime();
        keepAliveFlag = mManager.getIsAutoCheck();
        autoLogoutFlag = mManager.getIsAutoLogout();
        autoLoginFLag = mManager.getIsAutoLogin();
        uid = mManager.getUID();
        relog_interval = mManager.getRelogInterval();

        updateBalance();

        LoginHelper.setAccount(mManager.getUsername(), mManager.getPassword());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            String action = intent.getStringExtra("command");
            Log.d(TAG, "receive message in onStartCommand " + action);
            if (action != null) {
                if(action.equals(COMMAND_START_LISTEN))
                    startListen();
                else if(action.equals(COMMAND_STOP_LISTEN))
                    stopListen();
                else if(action.equals(COMMAND_DO_TEST)) {
                    if (((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).isWifiEnabled()) {
                        Log.d(TAG, "Test conn request is sending to ConnTest");
                        ConnTest.test(this);
                        updateBalance();
                    }
                }
                else if(action.equals(COMMAND_RE_LOGIN)) {
                       asyncRelog();
                    }
                else
                    Log.e(TAG, "Unknown action received");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }




    @Override
    public void onTestOver(boolean result) {
        Log.d(TAG, "Connection test : " + (result ? "Connected" : "Disconnected"));
        if(!result){
            status = NetworkState.OFFLINE;
            if(isAutoLogin())
                //asyncRelog();
                LoginHelper.asyncLogin();
        } else {
            status = NetworkState.ONLINE;
            startListen();
            updateBalance();
        }
        broadcastState();

    }

    private void startListen(){
        if(keepAliveFlag && !listeningFlag
        && ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).isWifiEnabled() ) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "Run in timer task");
                    ConnTest.test(LoginService.this);
                    updateBalance();
                }
            };
            timer.schedule(timerTask, 0, interval);
            listeningFlag = true;
        }
    }

    private void stopListen(){
        if(timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
        listeningFlag = false;

        //if(status == NetworkState.ONLINE)
        status = NetworkState.OFFLINE;
        broadcastState();

        mNotifTools.cancelNotification();
    }

    private void broadcastState(){
        Log.v(TAG, "network status change");
        broadcast.putExtra("command", COMMAND_STATE_CHANGE);
        sendBroadcast(broadcast);
    }

    @Override
    public void onLoginStateChanged(String message, int state) {
        Log.d(TAG, "Login state is : " + message);

        if(state ==  LoginHelper.OFFLINE) {

            status = NetworkState.OFFLINE;
            broadcastState();
            stopListen();
            if(message.equals("LOGOUT_OK"))
                sendNotif(getString(R.string.toast_loggedout), getString(R.string.toast_seedetail));
        }
        else if((state == LoginHelper.LOGIN_MODE_1) || (state == LoginHelper.LOGIN_MODE_2)) {
            Log.i(TAG, "login in mode 1");
            uid = LoginHelper.getUid();
            mManager.setUID(uid);

            updateBalance();

            startListen();
            if (message.equals(getString(R.string.login_error_messages_limit))) {
                if(!autoLogoutFlag)
                    mNotifTools.sendSimpleNotificationAndReLogin(getApplicationContext(),
                            getString(R.string.notif_forcelogout_title), String.format(getString(R.string.notif_forcelogout_desc), relog_interval / 1000));
                else
                    asyncRelog();
            } else if(!message.equals("") && (message.length() < 60)){
                if(message.equals(getString(R.string.login_toast_success_matcher))){
                    sendNotif(message, getString(R.string.toast_seedetail));
                }else {
                    sendNotif(getString(R.string.login_toast_failure), message);
                }
            }
            status = NetworkState.ONLINE;
            broadcastState();
        }
        else
            Log.e(TAG, "unknown login state");
    }

    private void sendNotif(String title, String message){
        if(!mManager.getIsSilent())
            mNotifTools.sendSimpleNotification(getApplicationContext(), title, message);

    }

    @Override
    public void onPreferenceChanged(PreferenceKey key) {
        switch (key){
            case IS_AUTO_LOGIN:
                autoLoginFLag = mManager.getIsAutoLogin();
            case IS_AUTO_LOGOUT:
                autoLogoutFlag = mManager.getIsAutoLogout();
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "service destroyed");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateBalance(){
        float balance = LoginHelper.getBalance(uid);
        if(balance > Global.INF){
            mBalance = balance;
        }
    }

    private void asyncRelog() {
        LoginHelper.asyncForceLogout();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(relog_interval);
                    LoginHelper.asyncLogin();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
