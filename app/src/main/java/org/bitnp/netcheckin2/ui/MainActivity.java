package org.bitnp.netcheckin2.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cengalabs.flatui.FlatUI;
import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.service.LoginService;
import org.bitnp.netcheckin2.service.NetworkState;
import org.bitnp.netcheckin2.ui.wave_progress.WaterWaveProgress;
import org.bitnp.netcheckin2.util.Global;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity{

    static MainActivity instance;
    private static final String TAG = "MainActivity";
    public static final String COMMAND_NO_SHOW_LAUNCH = "NO_SHOW_L";
    SharedPreferencesManager manager = new SharedPreferencesManager(MainActivity.this);
    String username;

    TextView status, currentUser;
    ListView ssidListView;
    ArrayList<String> SSIDList = new ArrayList<String>();
    StateChangeReceiver stateChangeReceiver;
    FilterMenuLayout filterMenuLayout;
    WaterWaveProgress waveProgress;


    Intent intent;

    public class StateChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "network state change received");
            if(intent != null) {
                String command = intent.getStringExtra("command");
                if(command.equals(LoginService.COMMAND_STATE_CHANGE))
                    setProgress();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        stateChangeReceiver = new StateChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginService.BROADCAST_ACTION);

        /** Prepare to receive messages from LoginService*/
        registerReceiver(stateChangeReceiver, intentFilter);

        username = manager.getUsername();
        if(username.length() == 0){

            /** first login
             *  show login activity and add default settings */
            FlatUI.initDefaultValues(this);
            FlatUI.setDefaultTheme(FlatUI.BLOOD);
            manager.addCustomSsid("BIT");
            manager.addCustomSsid("BeijingLG");
            manager.setIsAutoLogin(true);
            manager.setIsAutoCheck(true);

            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        initUI();

        /** start service*/
        intent = new Intent(MainActivity.this, LoginService.class);
        intent.putExtra("command", LoginService.COMMAND_DO_TEST);
        //Nothing to do with service object now...
        startService(intent);

        /** Send a broadcast to WifiChangedReceiver*/
        intent = new Intent(GlobalConstant.ACTION_BROADCAST_FROM_MAIN);
        sendBroadcast(intent);

        String s = getIntent().getStringExtra("command");
        if (s == null || !s.equals(COMMAND_NO_SHOW_LAUNCH)) {
            intent = new Intent(MainActivity.this, LaunchActivity.class);
            startActivity(intent);
        }
    }

    private void initUI() {
        status = (TextView) findViewById(R.id.textView6);
        currentUser = (TextView) findViewById(R.id.textView5);
        ssidListView = (ListView) findViewById(R.id.ls_SSID);
        waveProgress = (WaterWaveProgress) findViewById(R.id.prg_show);
        SSIDList = manager.getAllCustomSsid();

        waveProgress.setRingWidth((float)0.01);
        waveProgress.setWaveSpeed((float) 0.03);
        currentUser.setText(username);

        ssidListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return SSIDList.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View view, ViewGroup parent) {
                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_ssid, null);
                TextView text = (TextView) view.findViewById(R.id.item_ssid);
                text.setText(SSIDList.get(position));

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String ssid = ((TextView) v.findViewById(R.id.item_ssid)).getText().toString();
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(String.format(getString(R.string.ssid_delete_confirm), ssid))
                                .setPositiveButton(getString(R.string.confirm_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        manager.deleteSsid(ssid);
                                        SSIDList.remove(ssid);
                                        ((BaseAdapter) ssidListView.getAdapter()).notifyDataSetChanged();
                                        Toast.makeText(getApplicationContext(), R.string.ssid_delete_toast_success, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(R.string.confirm_wait, null)
                                .show();
                        return false;
                    }
                });
                return view;
            }
        });

        filterMenuLayout = (FilterMenuLayout) findViewById(R.id.filter_menu);
        new FilterMenu.Builder(this)
                .addItem(R.drawable.ic_action_add)//添加SSID
                .addItem(R.drawable.ic_action_wifi)//登录
                .addItem(R.drawable.ic_action_io)//注销
                .addItem(R.drawable.ic_action_info)//设置
                .attach(filterMenuLayout)
                .withListener(new FilterMenu.OnMenuChangeListener() {
                    @Override
                    public void onMenuItemClick(View view, int i) {
                        switch (i) {
                            case 0://添加SSID
                                final CustomEditText edit = new CustomEditText(MainActivity.this);
                                WifiInfo wifiInfo = ((WifiManager) getSystemService(WIFI_SERVICE)).getConnectionInfo();
                                String ssid = wifiInfo.getSSID();
//                                if(ssid == null || ssid.equals("<unknown ssid>") || ssid.equals(""))
//                                    ssid = "";
//                                else if(ssid.startsWith("\"") && ssid.endsWith("\""))
//                                    ssid = ssid.substring(1, ssid.length() - 1);
                                edit.setText(SharedPreferencesManager.trimSsid(ssid));
                                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                                        .setView(edit)
                                        .setPositiveButton(R.string.ssid_add_submit, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String newSSID = edit.getText().toString();
                                                if(newSSID.length() > 0 && !newSSID.equals("<unknown ssid>")) {
                                                    if (newSSID.startsWith("\"") && newSSID.endsWith("\"")) {
                                                        Toast.makeText(getApplicationContext(), R.string.ssid_add_quotes_not_supported, Toast.LENGTH_SHORT).show();
                                                    } else if (manager.addCustomSsid(newSSID)) {
                                                        SSIDList.add(newSSID);
                                                    } else {
                                                        Toast.makeText(getApplicationContext(), R.string.ssid_add_failure, Toast.LENGTH_SHORT).show();
                                                    }
                                                    ((BaseAdapter) ssidListView.getAdapter()).notifyDataSetChanged();
                                                    LoginHelper.asyncLogin();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), R.string.ssid_add_illegal_input, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .setNegativeButton(R.string.ssid_add_cancel, null)
                                        .setTitle(R.string.ssid_add_title);
                                dialog.show();
                                break;
                            case 1://登录
                                if (LoginService.getStatus() == NetworkState.OFFLINE) {
                                    LoginHelper.asyncLogin();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), R.string.toast_logged, Toast.LENGTH_SHORT).show();
                                break;
                            case 2://注销
                                LoginHelper.asyncForceLogout();
                                break;
                            case 3://设置
                                Intent setting = new Intent();
                                setting.setClass(MainActivity.this, SettingsActivity.class);
                                startActivity(setting);
                                break;
                        }
                    }

                    @Override
                    public void onMenuCollapse() {

                    }

                    @Override
                    public void onMenuExpand() {

                    }
                })
                .build();
    }

    @Override
    protected void onResume() {
        setProgress();
        MiStatInterface.recordPageStart(this, TAG);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(stateChangeReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        MiStatInterface.recordPageEnd();
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                // translucent status bar
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                // translucent navigation bar
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
    }

    protected void setProgress(){
        Log.d(TAG, "Set progress is called to set balance");
        if(LoginService.getStatus() == NetworkState.OFFLINE){
            waveProgress.setProgress(50);
            waveProgress.setProgressTxt(getString(R.string.status_unlogged));
            waveProgress.setCrestCount((float)0.5);
            waveProgress.setWaveSpeed((float)0.02);
            status.setText("");
        } else {
            float fBalance = LoginService.getmBalance();
            waveProgress.setCrestCount((float)1.5);
            if(fBalance < Global.INF)
            {
                waveProgress.setProgress(0);
                waveProgress.setProgressTxt(getString(R.string.status_unknown));
            } else {
                String balance = fBalance + "";
                balance = balance.substring(0, (balance.length() > 4 ? 4 : balance.length()));
                waveProgress.setProgress((int) ((fBalance > 30 ? 30 : fBalance) / 30 * 100));
                waveProgress.setProgressTxt(balance + " G");
            }
            status.setText(R.string.status_logged);
        }
    }

}
