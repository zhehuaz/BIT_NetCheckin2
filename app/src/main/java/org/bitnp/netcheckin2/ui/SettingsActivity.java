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
import org.bitnp.netcheckin2.util.SharedPreferencesManager;

import java.util.ArrayList;

public class SettingsActivity extends ActionBarActivity {

    CheckBox autoLogin, autoCheck;
    EditText autoCheckTime;
    ListView listView;

    ArrayList<String> ssidList;

    SharedPreferencesManager manager;

    public void confirmTime(View v){
        String s = autoCheckTime.getText().toString();
        long val = 0;
        try{
            val = Long.parseLong(s);
            manager.setAutoCheckTime(val);
        } catch (Exception e) {
            Toast.makeText(SettingsActivity.this, "Invalid format", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        manager = new SharedPreferencesManager(SettingsActivity.this);

        autoLogin = (CheckBox) findViewById(R.id.checkBox2);
        autoCheck = (CheckBox) findViewById(R.id.checkBox3);
        autoCheckTime = (EditText) findViewById(R.id.editText3);
        listView = (ListView) findViewById(R.id.listView);

        ssidList = manager.getAllCustomSSID();

        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                manager.setIsAutoLogin(isChecked);
                if(isChecked)
                    autoCheck.setClickable(true);
                else
                    autoCheck.setClickable(false);
            }
        });

        autoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                manager.setIsAutoCheck(isChecked);
                autoCheckTime.setEnabled(isChecked);
                autoCheckTime.setText(manager.getAutoCheckTime() + "");
            }
        });



        autoLogin.setChecked(manager.getIsAutoLogin());

        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return ssidList.size() >= 5 ? 5 : ssidList.size() + 1;
            }

            @Override
            public Object getItem(int position) {return null;}

            @Override
            public long getItemId(int position) {return 0;}

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(position >= ssidList.size()){
                    Button b = new Button(SettingsActivity.this);
                    b.setText("+");
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final EditText edit = new EditText(SettingsActivity.this);

                            AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this)
                                    .setView(edit)
                                    .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String newSSID = edit.getText().toString();
                                            ssidList.add(newSSID);
                                            ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                                            manager.addCustomSSID(newSSID);
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .setTitle("自定义SSID");
                            dialog.show();

                        }
                    });
                    return b;
                } else {
                    TextView text = new TextView(SettingsActivity.this);
                    text.setText(ssidList.get(position));
                    return text;
                }
            }
        });

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
