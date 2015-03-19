package org.bitnp.netcheckin2.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.service.LoginService;
import org.bitnp.netcheckin2.network.LoginStateListener;
import org.bitnp.netcheckin2.util.NotifTools;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;



public class MainActivity extends ActionBarActivity implements LoginStateListener{

    SharedPreferencesManager manager = new SharedPreferencesManager(MainActivity.this);
    String username;

    ProgressBar progressBar;
    TextView status, currentUser;
    Button buttonLogin, buttonLogout;
    ImageButton showSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(MainActivity.this, LoginService.class);
        intent.setAction(LoginService.START_LISTEN);
        this.startService(intent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = manager.getUsername();
        if(username.length() == 0){
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }

        initUI();
    }

    private void initUI() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        status = (TextView) findViewById(R.id.textView6);
        buttonLogin = (Button) findViewById(R.id.button5);
        buttonLogout = (Button) findViewById(R.id.button6);
        currentUser = (TextView) findViewById(R.id.textView5);
        showSettings = (ImageButton) findViewById(R.id.imageButton);

        status.setText((LoginHelper.getLoginState() == 0 ? "未登录" : "已登录"));
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


    @Override
    public void onLoginStateChanged(String message, int state) {
        setProgress(false);
        status.setText(message);
    }
}
