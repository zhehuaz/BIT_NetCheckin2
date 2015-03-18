package org.bitnp.netcheckin2.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;



public class MainActivity extends ActionBarActivity {

    SharedPreferencesManager manager = new SharedPreferencesManager(MainActivity.this);
    String username, password;

    ProgressBar progressBar;
    TextView status, currentUser;
    Button buttonLogin, buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        username = manager.getUsername();
        if(username.length() == 0){
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }
        password = manager.getPassword();
        LoginHelper.setAccount(username, password);

        LoginHelper.setHandler(this.handler);

        initUI();
    }

    private void initUI() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        status = (TextView) findViewById(R.id.textView6);
        buttonLogin = (Button) findViewById(R.id.button5);
        buttonLogout = (Button) findViewById(R.id.button6);
        currentUser = (TextView) findViewById(R.id.textView5);

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


    }


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            status.setText((String) msg.obj);
            setProgress(false);
        }
    };

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
