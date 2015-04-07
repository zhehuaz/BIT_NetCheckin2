package org.bitnp.netcheckin2.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.network.HttpRequest;
import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActionBarActivity {

    private View mProgressView;
    private View mLoginFormView;

    EditText textUsername, textPassword;
    Button confirm;
    String username, password;

    private final static int ERROR_USERNAME = 0x1;
    private final static int ERROR_PASSWORD = 0x2;
    private final static int CONFIRMED = 0x0;
    private final static int ERROR_UNKNOWN = 0x3;
    private final static int ERROR_LOGOUT = 0x4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressView = findViewById(R.id.login_progress);
        mLoginFormView = findViewById(R.id.email_login_form);
        textUsername = (EditText) findViewById(R.id.username);
        textPassword = (EditText) findViewById(R.id.password);
        confirm = (Button) findViewById(R.id.sign_in);

        SharedPreferencesManager manager = new SharedPreferencesManager(LoginActivity.this);
        textUsername.setText(manager.getUsername());
        textPassword.setText(manager.getPassword());

        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                username = textUsername.getText().toString();
                password = textPassword.getText().toString();
                if(username.length() == 0){
                    Toast.makeText(LoginActivity.this, "请填写用户名", Toast.LENGTH_SHORT).show();
                    textUsername.requestFocus();
                }
                if(password.length() == 0){
                    Toast.makeText(LoginActivity.this, "请填写密码", Toast.LENGTH_SHORT).show();
                    textPassword.requestFocus();
                }

                attemptLogin();
            }
        });
    }

    public void attemptLogin() {
        showProgress(true);
        confirm.setClickable(false);
        new Thread(new Runnable(){
            @Override
            public void run() {
                String url = "http://10.0.0.55/cgi-bin/force_logout";
                String res = HttpRequest.sendPost(url, "username=" + username + "&password=" + password + "&drop=" + "0" + "&type=1&n=1");
                Message msg = new Message();
                if(res.equals("username_error")){
                    msg.what = ERROR_USERNAME;
                } else if(res.equals("password_error")){
                    msg.what = ERROR_PASSWORD;
                } else if(res.equals("logout_ok") || res.equals("logout_error")){
                    msg.what = CONFIRMED;
                } else {
                    msg.what = ERROR_UNKNOWN;
                }

                /** testing account */
                if(username.equals("T2E3S65T6I3NG7") && password.equals("S2J353F3O422I2W35E"))
                    msg.what = CONFIRMED;

                handler.sendMessage(msg);
            }
        }).start();
    }



    private void saveAccountInfo() {
        SharedPreferencesManager manager = new SharedPreferencesManager(LoginActivity.this);
        manager.setPassword(password);
        manager.setUsername(username);
        LoginHelper.setAccount(username, password);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            mLoginFormView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            confirm.setClickable(true);
            switch (msg.what){
                case CONFIRMED:
                    Toast.makeText(LoginActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                    saveAccountInfo();
                    break;
                case ERROR_USERNAME:
                    Toast.makeText(LoginActivity.this, "用户名错误", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                    break;
                case ERROR_PASSWORD:
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    showProgress(false);
                    break;
                case ERROR_LOGOUT:
                    Toast.makeText(LoginActivity.this, "检测到未登录，在尝试登录", Toast.LENGTH_SHORT).show();
                    LoginHelper.asyncLogin();
                default:
                    Toast.makeText(LoginActivity.this, "连接失败,请检查网络\n如果不是网络问题，请联系我zhehuaxiao@gmail.com", Toast.LENGTH_LONG).show();
                    showProgress(false);
            }
        }


    };
}



