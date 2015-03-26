package org.bitnp.netcheckin2.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.network.LoginStateListener;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

import java.util.ArrayList;

public class SettingsActivity extends ActionBarActivity{

    CheckBox autoLogin;
    //EditText autoCheckTime;
    CheckBox autoLogout;
    SharedPreferencesManager manager;

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

        autoLogin = (CheckBox) findViewById(R.id.checkBox2);
        autoLogout = (CheckBox) findViewById(R.id.cb_auto_flogout);


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

}
