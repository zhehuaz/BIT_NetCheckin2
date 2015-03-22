package org.bitnp.netcheckin2.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.service.LoginService;
import org.bitnp.netcheckin2.service.NetworkState;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    private static final String ACTION_MAINACTIVITY_START = "org.bitnp.netcheckin2.ui.MAIN_START";

    SharedPreferencesManager manager = new SharedPreferencesManager(MainActivity.this);
    String username;

    ProgressBar progressBar;
    TextView status, currentUser;
    Button buttonLogin, buttonLogout;
    ImageButton showSettings;
    ListView SSIDListView;
    ArrayList<String> SSIDList = new ArrayList<String>();
    StateChangeReceiver stateChangeReceiver;

    Intent intent;
    LoginService loginService;

    public class StateChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "network state change received");
            if(intent != null) {
                String command = intent.getStringExtra("command");
                if(command.equals(LoginService.ACTION_STATE_CHANGE))
                    if(LoginService.getStatus() == NetworkState.ONLINE)
                        status.setText("已登录");
                    else
                        status.setText("未登录");
            }
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            loginService = ((LoginService.LoginServiceBinder)service).getLoginService();
            status.setText(((LoginService.getStatus() == NetworkState.OFFLINE) ? "未登录" : "已登录"));
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

        stateChangeReceiver = new StateChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginService.BROADCAST_ACTION);
        registerReceiver(stateChangeReceiver, intentFilter);


        initUI();

        intent = new Intent(MainActivity.this, LoginService.class);
        //intent.setAction(LoginService.ACTION_DO_TEST);
        //Nothing to do with service object now...
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        intent = new Intent(ACTION_MAINACTIVITY_START);
        sendBroadcast(intent);
    }

    private void initUI() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        status = (TextView) findViewById(R.id.textView6);
        buttonLogin = (Button) findViewById(R.id.button5);
        buttonLogout = (Button) findViewById(R.id.button6);
        currentUser = (TextView) findViewById(R.id.textView5);
        showSettings = (ImageButton) findViewById(R.id.imageButton);
        SSIDListView = (ListView) findViewById(R.id.ls_SSID);
        SSIDList = manager.getAllCustomSSID();
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



        SSIDListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return SSIDList.size() >= 5 ? 5 : SSIDList.size() + 1;
            }

            @Override
            public Object getItem(int position) {return null;}

            @Override
            public long getItemId(int position) {return 0;}

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(position >= SSIDList.size()){
                    Button b = new Button(MainActivity.this);
                    b.setText("+");
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final EditText edit = new EditText(MainActivity.this);

                            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                                    .setView(edit)
                                    .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String newSSID = edit.getText().toString();
                                            manager.addCustomSSID(newSSID);
                                            SSIDList.add(newSSID);
                                            ((BaseAdapter)SSIDListView.getAdapter()).notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .setTitle("自定义SSID");
                            dialog.show();

                        }
                    });
                    return b;
                } else {
                    TextView text = new TextView(MainActivity.this);
                    text.setText(SSIDList.get(position));
                    text.setTextSize(45);
                    return text;
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        unregisterReceiver(stateChangeReceiver);
        super.onDestroy();

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
