package org.bitnp.netcheckin2.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cengalabs.flatui.FlatUI;
import com.linroid.filtermenu.library.FilterMenu;
import com.linroid.filtermenu.library.FilterMenuLayout;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.network.LoginHelper;
import org.bitnp.netcheckin2.network.LoginStateListener;
import org.bitnp.netcheckin2.service.LoginService;
import org.bitnp.netcheckin2.service.NetworkState;
import org.bitnp.netcheckin2.ui.wave_progress.WaterWaveProgress;
import org.bitnp.netcheckin2.util.Global;
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity{

    private static final String TAG = "MainActivity";


    SharedPreferencesManager manager = new SharedPreferencesManager(MainActivity.this);
    String username;

    TextView status, currentUser;
    ListView SSIDListView;
    ArrayList<String> SSIDList = new ArrayList<String>();
    StateChangeReceiver stateChangeReceiver;
    FilterMenuLayout filterMenuLayout;
    WaterWaveProgress waveProgress;

    Intent intent;
    LoginService loginService;


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
        username = manager.getUsername();
        if(username.length() == 0){

            /** first login
            *  show login activity and add default settings */
            FlatUI.initDefaultValues(this);
            FlatUI.setDefaultTheme(FlatUI.SAND);
            manager.addCustomSSID("BIT");
            manager.addCustomSSID("BeijingLG");
            manager.setIsAutoLogin(true);
            manager.setIsAutoCheck(true);

            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        stateChangeReceiver = new StateChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginService.BROADCAST_ACTION);

        /** Prepare to receive messages from LoginService*/
        registerReceiver(stateChangeReceiver, intentFilter);

        initUI();

        /** start service*/
        intent = new Intent(MainActivity.this, LoginService.class);
        intent.putExtra("command", LoginService.COMMAND_DO_TEST);
        //Nothing to do with service object now...
        startService(intent);

        /** Send a broadcast to WifiChangedReceiver*/
        intent = new Intent(GlobalConstant.ACTION_BROADCAST_FROM_MAIN);
        sendBroadcast(intent);

        setProgress();
    }

    private void initUI() {
        status = (TextView) findViewById(R.id.textView6);
        currentUser = (TextView) findViewById(R.id.textView5);
        SSIDListView = (ListView) findViewById(R.id.ls_SSID);
        waveProgress = (WaterWaveProgress) findViewById(R.id.prg_show);
        SSIDList = manager.getAllCustomSSID();

        /*// MiUI v6 immersive, official sample
        Window window = getWindow();

        Class clazz = window.getClass();
        try {
            int tranceFlag = 0;
            int darkModeFlag = 0;
            Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");

            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_TRANSPARENT");
            tranceFlag = field.getInt(layoutParams);

            field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);

            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            //只需要状态栏透明
            extraFlagField.invoke(window, tranceFlag, tranceFlag);
            //状态栏透明且黑色字体 extraFlagField.invoke(window, tranceFlag | darkModeFlag, tranceFlag | darkModeFlag);
            // 清除黑色字体
            extraFlagField.invoke(window, 0, darkModeFlag); }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
        catch (NoSuchFieldException e) { e.printStackTrace(); }
        catch (IllegalAccessException e) { e.printStackTrace(); }
        catch (IllegalArgumentException e) { e.printStackTrace(); }
        catch (InvocationTargetException e) { e.printStackTrace(); }

        //~MI UI v6*/


        currentUser.setText(username);
        waveProgress.setRingWidth((float)0.01);

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
                .addItem(R.drawable.ic_action_wifi)//登录
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
                                                    Toast.makeText(getApplicationContext(), "此SSID已存在或列表已满", Toast.LENGTH_SHORT).show();
                                                ((BaseAdapter) SSIDListView.getAdapter()).notifyDataSetChanged();
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .setTitle("自定义SSID");
                                dialog.show();
                                break;
                            case 1://登录
                                if (LoginService.getStatus() == NetworkState.OFFLINE) {
                                    LoginHelper.asyncLogin();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "已登录", Toast.LENGTH_SHORT).show();
                                break;
                            case 2://注销
                                //setProgress(true);
                                /*  FIXME login fail always
                                if (LoginService.getStatus() == NetworkState.OFFLINE) {
                                    LoginHelper.asyncForceLogout();
                                } else {
                                    LoginHelper.asyncLogout();
                                }*/
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
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(stateChangeReceiver);
        super.onDestroy();

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
            waveProgress.setProgress(100);
            waveProgress.setProgressTxt("未登录");
            status.setText("");
        } else {
            float fBalance = LoginService.getmBalance();
            if(fBalance < Global.INF)
            {
                waveProgress.setProgress(0);
                waveProgress.setProgressTxt("未知");
            }
            String balance = fBalance + "";
            balance = balance.substring(0, (balance.length() > 4 ? 4 : balance.length()));
            waveProgress.setProgress((int) ((fBalance > 30 ? 30 : fBalance) / 30 * 100));
            waveProgress.setProgressTxt(balance + " G");
            status.setText("已登录");
        }
    }

}
