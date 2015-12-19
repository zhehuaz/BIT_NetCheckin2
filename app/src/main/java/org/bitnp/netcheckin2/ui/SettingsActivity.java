package org.bitnp.netcheckin2.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.cengalabs.flatui.views.FlatButton;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

public class SettingsActivity extends ActionBarActivity{

    CheckBox autoLogin;
    CheckBox autoLogout;
    CheckBox silenceMode;
    SharedPreferencesManager manager;
    FlatButton logoutButton;
    FlatButton submitButton;
    FlatButton helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        manager = new SharedPreferencesManager(SettingsActivity.this);

        autoLogin = (CheckBox) findViewById(R.id.cb_auto_login);
        autoLogout = (CheckBox) findViewById(R.id.cb_auto_logout);
        logoutButton = (FlatButton) findViewById(R.id.bt_logout);
        submitButton = (FlatButton) findViewById(R.id.bt_submit);
        helpButton = (FlatButton) findViewById(R.id.bt_help);
        silenceMode = (CheckBox) findViewById(R.id.cb_silence);

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

        silenceMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                manager.setIsSilent(isChecked);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this)
                        .setMessage(getString(R.string.confirm_exit_account))
                        .setPositiveButton(getString(R.string.confirm_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginHelper.asyncForceLogout();
                                manager.setUsername("");
                                manager.setPassword("");
                                MainActivity.instance.finish();
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.confirm_nope), null);
                        //.setTitle("退出帐号");
                dialog.show();

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

        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });


        autoLogin.setChecked(manager.getIsAutoLogin());
        autoLogout.setChecked(manager.getIsAutoLogout());
        silenceMode.setChecked(manager.getIsSilent());
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
