package org.bitnp.netcheckin2.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.cengalabs.flatui.views.FlatButton;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

public class SettingsActivity extends ActionBarActivity{

    CheckBox autoLogin;
    //EditText autoCheckTime;
    CheckBox autoLogout;
    SharedPreferencesManager manager;
    FlatButton logoutButton;
    FlatButton submitButton;

    /*public void confirmTime(View v){
        String s = autoCheckTime.getText().toString();
        long val = 0;
        try{
            val = Long.parseLong(s);
            manager.setAutoCheckTime(val);
        } catch (Exception e) {
            Toast.makeText(SettingsActivity.this, "Invalid format", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        manager = new SharedPreferencesManager(SettingsActivity.this);

        autoLogin = (CheckBox) findViewById(R.id.cb_auto_login);
        autoLogout = (CheckBox) findViewById(R.id.cb_auto_logout);
        logoutButton = (FlatButton) findViewById(R.id.bt_logout);
        submitButton = (FlatButton) findViewById(R.id.bt_submit);

        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                manager.setIsAutoLogin(isChecked);
            }
        });

        autoLogout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                manager.setIsAutoLogout(isChecked);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setUsername("");
                manager.setPassword("");
                finish();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                Uri uri = Uri.parse("mailto:zhehuaxiao@gmail.com?subject=" + Uri.encode("Issues in BITion"));
                intent.setData(uri);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });
/*
        autoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                manager.setIsAutoCheck(isChecked);
                autoCheckTime.setEnabled(isChecked);
                autoCheckTime.setText(manager.getAutoCheckTime() + "");
            }
        });*/



        autoLogin.setChecked(manager.getIsAutoLogin());
        autoLogout.setChecked(manager.getIsAutoLogout());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
