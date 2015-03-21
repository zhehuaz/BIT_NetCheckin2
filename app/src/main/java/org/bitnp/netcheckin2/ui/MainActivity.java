package org.bitnp.netcheckin2.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.service.LoginService;
import org.bitnp.netcheckin2.service.NetworkState;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;



public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    SharedPreferencesManager manager = new SharedPreferencesManager(MainActivity.this);
    String username;

    ProgressBar progressBar;
    TextView status, currentUser;
    Button buttonLogin, buttonLogout;
    ImageButton showSettings;

    LoginService loginService;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            loginService = ((LoginService.LoginServiceBinder)service).getLoginService();
            status.setText(((loginService.getStatus() == NetworkState.OFFLINE) ? "未登录" : "已登录"));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = manager.getUsername();
        if(username.length() == 0){
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }

        initUI();

        Intent intent = new Intent(MainActivity.this, LoginService.class);
        intent.setAction(LoginService.ACTION_DO_TEST);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


    }

    private void initUI() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        status = (TextView) findViewById(R.id.textView6);
        buttonLogin = (Button) findViewById(R.id.button5);
        buttonLogout = (Button) findViewById(R.id.button6);
        currentUser = (TextView) findViewById(R.id.textView5);
        showSettings = (ImageButton) findViewById(R.id.imageButton);

        progressBar.setVisibility(View.INVISIBLE);

        currentUser.setText(username);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgress(true);

                LoginHelper.asyncLogin();
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgress(true);

                if(LoginHelper.getLoginState() != 0) {
                    LoginHelper.asyncForceLogout();
                } else {
                    LoginHelper.asyncLogout();
                }
            }
        });

        showSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClass(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "on Resume");
        if(loginService != null)
            status.setText(((loginService.getStatus() == NetworkState.OFFLINE) ? "未登录" : "已登录"));
        else {
            status.setText(((LoginHelper.getLoginState() == LoginHelper.OFFLINE) ? "未登录" : "已登录"));
            Log.e(TAG, "login service is null");
        }
    }

    void setProgress(boolean show){
        if(show) {
            status.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            buttonLogin.setClickable(false);
            buttonLogout.setClickable(false);
        } else {
            status.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            buttonLogin.setClickable(true);
            buttonLogout.setClickable(true);
        }
    }



}
