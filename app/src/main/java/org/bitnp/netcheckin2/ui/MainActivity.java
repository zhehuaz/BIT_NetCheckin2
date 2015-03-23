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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.network.LoginStateListener;
import org.bitnp.netcheckin2.service.LoginService;
import org.bitnp.netcheckin2.service.NetworkState;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements LoginStateListener{

    private static final String TAG = "MainActivity";

    private static final String ACTION_MAINACTIVITY_START = "org.bitnp.netcheckin2.ui.MAIN_START";

    SharedPreferencesManager manager = new SharedPreferencesManager(MainActivity.this);
    String username;

    //CircleProgressBar progressBar;
    TextView status, currentUser;
    ListView SSIDListView;
    ArrayList<String> SSIDList = new ArrayList<String>();
    StateChangeReceiver stateChangeReceiver;
    FilterMenuLayout filterMenuLayout;

    Intent intent;
    LoginService loginService;

    @Override
    public void onLoginStateChanged(String message, int state) {
        //setProgress(false);
    }

    public class StateChangeReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "network state change received");
            if(intent != null) {
                String command = intent.getStringExtra("command");
                if(command.equals(LoginService.ACTION_STATE_CHANGE))
                    if(LoginService.getStatus() == NetworkState.ONLINE) {
                        status.setText("已登录");
                        setProgress(false);
                    }
                    else {
                        status.setText("未登录");
                        setProgress(false);
                    }
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
            manager.addCustomSSID("BIT");
            manager.addCustomSSID("BeijingLG");
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
        }

        stateChangeReceiver = new StateChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginService.BROADCAST_ACTION);
        registerReceiver(stateChangeReceiver, intentFilter);

        LoginHelper.registerListener(this);

        initUI();

        intent = new Intent(MainActivity.this, LoginService.class);
        //intent.setAction(LoginService.ACTION_DO_TEST);
        //Nothing to do with service object now...
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        intent = new Intent(ACTION_MAINACTIVITY_START);
        sendBroadcast(intent);
    }

    private void initUI() {
        //progressBar = (CircleProgressBar) findViewById(R.id.progressBar);
        status = (TextView) findViewById(R.id.textView6);
        currentUser = (TextView) findViewById(R.id.textView5);
        SSIDListView = (ListView) findViewById(R.id.ls_SSID);
        SSIDList = manager.getAllCustomSSID();
        //progressBar.setCircleBackgroundEnabled(false);
        //progressBar.setColorSchemeColors(R.color.common_signin_btn_default_background);
        //progressBar.setVisibility(View.GONE);


        currentUser.setText(username);


        SSIDListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return SSIDList.size() ;
            }

            @Override
            public Object getItem(int position) {return null;}

            @Override
            public long getItemId(int position) {return 0;}

            @Override
            public View getView(int position, View view, ViewGroup parent) {


                view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_ssid, null);
                TextView text = (TextView) view.findViewById(R.id.item_ssid);
                text.setText(SSIDList.get(position));

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String ssid = ((TextView)v.findViewById(R.id.item_ssid)).getText().toString();
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("确定要删除\"" + ssid + "\"?")
                                .setPositiveButton("嗯", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        manager.deleteSSID(ssid);
                                        SSIDList.remove(ssid);
                                        ((BaseAdapter) SSIDListView.getAdapter()).notifyDataSetChanged();
                                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("等等", null)
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
                .addItem(R.drawable.ic_conn_l)//登录
                .addItem(R.drawable.ic_action_io)//注销
                .addItem(R.drawable.ic_action_info)//设置
                .attach(filterMenuLayout)
                .withListener(new FilterMenu.OnMenuChangeListener() {
                    @Override
                    public void onMenuItemClick(View view, int i) {
                        switch (i) {
                            case 0://添加SSID
                                final EditText edit = new EditText(MainActivity.this);

                                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this)
                                        .setView(edit)
                                        .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String newSSID = edit.getText().toString();
                                                if(manager.addCustomSSID(newSSID) == true)
                                                    SSIDList.add(newSSID);
                                                else
                                                    Toast.makeText(getApplicationContext(), "此SSID已存在", Toast.LENGTH_SHORT).show();
                                                ((BaseAdapter) SSIDListView.getAdapter()).notifyDataSetChanged();
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .setTitle("自定义SSID");
                                dialog.show();
                                break;
                            case 1://登录
                                if (LoginService.getStatus() == NetworkState.OFFLINE) {
                                    setProgress(true);
                                    LoginHelper.asyncLogin();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "已登录", Toast.LENGTH_SHORT).show();
                                break;
                            case 2://注销
                                setProgress(true);
                                if (LoginService.getStatus() == NetworkState.OFFLINE) {
                                    LoginHelper.asyncForceLogout();
                                } else {
                                    LoginHelper.asyncLogout();
                                }
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
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        unregisterReceiver(stateChangeReceiver);
        LoginHelper.unRegisterLisener(this);
        super.onDestroy();

    }

    void setProgress(boolean show){
        if(show) {
            status.setVisibility(View.INVISIBLE);
            //progressBar.setVisibility(View.VISIBLE);
            //buttonLogin.setClickable(false);
            //buttonLogout.setClickable(false);
        } else {
            status.setVisibility(View.VISIBLE);
            //progressBar.setVisibility(View.INVISIBLE);
            //buttonLogin.setClickable(true);
            //sbuttonLogout.setClickable(true);
        }
    }

}
