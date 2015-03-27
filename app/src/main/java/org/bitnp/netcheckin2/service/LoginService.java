package org.bitnp.netcheckin2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.network.LoginStateListener;
import org.bitnp.netcheckin2.util.ConnTest;
import org.bitnp.netcheckin2.util.ConnTestCallBack;
import org.bitnp.netcheckin2.util.NotifTools;
import org.bitnp.netcheckin2.util.PreferenceChangedListener;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

import java.util.ArrayList;
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

    Intent broadcast = new Intent(BROADCAST_ACTION);

    private NotifTools mNotifTools;

    private Timer timer;
    private TimerTask timerTask;

    public LoginService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(intent.getAction() != null) {
            Log.d(TAG, "Get intent in onBind " + intent.getAction());
            if (intent.getStringExtra("command").equals(LoginService.COMMAND_DO_TEST))
                ConnTest.test(this);
        }
        return new LoginServiceBinder();
    }

    public static NetworkState getStatus() {
        return status;
    }


    public class LoginServiceBinder extends Binder{

        public LoginService getLoginService(){
            return LoginService.this;
        }

    }

    public static boolean isKeepAlive() {
        return keepAliveFlag;
    }

    public static long getInterval() {
        return interval;
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

       /*  only for debug
        interval = 30 * 1000;
        keepAliveFlag = true;
        autoLogoutFlag = false;
       */

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
                else if(action.equals(COMMAND_DO_TEST))
                    if(((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).isWifiEnabled())
                        ConnTest.test(this);
                else if(action.equals(COMMAND_RE_LOGIN))
                    LoginHelper.asyncForceLogout();
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
            if(autoLoginFLag)
                LoginHelper.asyncLogin();
        } else {
            status = NetworkState.ONLINE;
            startListen();
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
                mNotifTools.sendSimpleNotification(getApplicationContext(), "已断开", "点击查看详情");
        }
        else if((state == LoginHelper.LOGIN_MODE_1) || (state == LoginHelper.LOGIN_MODE_2)) {
            Log.i(TAG, "login in mode 1");
            status = NetworkState.ONLINE;
            broadcastState();
            startListen();
            if (message.equals("该帐号的登录人数已超过限额\n" +
                    "如果怀疑帐号被盗用，请联系管理员。")) {
                if(!autoLogoutFlag)
                    mNotifTools.sendButtonNotification(getApplicationContext(), "是否强制断开", "将登出所有在线用户，并在一段时间后自动重连");
                else
                    LoginHelper.asyncForceLogout();
            } else if(!message.equals("") && (message.length() < 60))
                mNotifTools.sendSimpleNotification(getApplicationContext(), message, "点击查看详情");
        }
        else
            Log.e(TAG, "unknown login state");
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
}
