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
import org.bitnp.netcheckin2.util.NotifTools;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

import java.util.Timer;
import java.util.TimerTask;

public class LoginService extends Service implements ConnTestCallBack,LoginStateListener{

    private final static String TAG = "LoginService";

    public final static String START_LISTEN = "START LISTEN";
    public final static String STOP_LISTEN = "STOP_LISTEN";

    private boolean listeningFlag = false;

    public static NetworkState getStatus() {
        return status;
    }

    private static NetworkState status;

    private SharedPreferencesManager mManager;
    private static boolean keepAliveFlag;
    private static boolean autoLogoutFlag;
    private static long interval;

    private NotifTools mNotifTools;

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
        mNotifTools = NotifTools.getInstance(this.getApplicationContext());

        /*
        interval = mManager.getAutoCheckTime();
        keepAliveFlag = mManager.getIsAutoCheck();
        autoLogoutFlag = mManager.getIsAutoLogout();
        */
        //TODO only for debug
        interval = 5 * 60 * 1000;
        keepAliveFlag = true;
        autoLogoutFlag = true;
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
            status = NetworkState.OFFLINE;
            LoginHelper.asyncLogin();
        } else
            status = NetworkState.ONLINE;
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
                status = NetworkState.OFFLINE;
                stopListen();
                mNotifTools.sendSimpleNotification(getApplicationContext(), "已断开", "点击查看详情");
                break;
            case LoginHelper.LOGIN_MODE_1:
                Log.i(TAG, "login in mode 1");
                status = NetworkState.ONLINE;
                startListen();
                if(autoLogoutFlag && message.equals("该帐号的登录人数已超过限额\n" +
                        "如果怀疑帐号被盗用，请联系管理员。")){
                    //TODO notification
                    mNotifTools.sendButtonNotification(getApplicationContext(), "是否强制断开", message);
                }
                else
                    mNotifTools.sendSimpleNotification(getApplicationContext(), message, "点击查看详情");
                break;
            case LoginHelper.LOGIN_MODE_2:
                Log.i(TAG, "login in mode 2");
                status = NetworkState.ONLINE;
                startListen();
                mNotifTools.sendSimpleNotification(getApplicationContext(), message, "点击查看详情");
                break;
            default:
                Log.e(TAG, "unknown login state");
        }
    }
}
