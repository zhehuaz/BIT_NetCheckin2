package org.bitnp.netcheckin2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by ental_000 on 2015/3/17.
 */
public class SharedPreferencesManager {

    Context context;

    public SharedPreferencesManager(Context context) {
        this.context = context;
    }

    public String getUsername(){
        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        return sp.getString("username", "");
    }

    public void setUsername(String username){
        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.commit();
    }

    public String getPassword(){
        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        return sp.getString("password", "");
    }

    public void setPassword(String password){
        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("password", password);
        editor.commit();
    }

    public boolean getIsAutoLogin(){
        SharedPreferences sp = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
        return sp.getBoolean("autologin", false);
    }

    public void setIsAutoLogin(boolean value){
        SharedPreferences sp = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("autologin", value);
        editor.commit();
    }

    public boolean getIsAutoCheck(){
        SharedPreferences sp = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
        return sp.getBoolean("autocheck", false);
    }

    public void setIsAutoCheck(boolean value){
        SharedPreferences sp = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("autocheck", value);
        editor.commit();
    }

    public long getAutoCheckTime(){
        SharedPreferences sp = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
        return sp.getLong("autochecktime_millis", 900000);
    }

    public void setAutoCheckTime(long value){
        SharedPreferences sp = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("autochecktime_millis", value);
        editor.commit();
    }

    public ArrayList<String> getAllSSID(){
        SharedPreferences sp = context.getSharedPreferences("autoLogin_SSID", Context.MODE_PRIVATE);
        ArrayList<String> res = new ArrayList<String>();
        for(int i = 0; i < 5; i++){
            String s = sp.getString("ssid_" + i, "");
            if(!s.isEmpty())
                res.add(s);
        }
        return res;
    }

    public void setAllSSID(ArrayList<String> arr){
        SharedPreferences sp = context.getSharedPreferences("autoLogin_SSID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for(int i = 0; i < 5; i++){
            editor.putString("ssid_" + i, arr.get(i));
        }
        editor.commit();
    }
}
