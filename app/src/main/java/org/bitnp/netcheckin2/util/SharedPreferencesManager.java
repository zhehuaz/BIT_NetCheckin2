package org.bitnp.netcheckin2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

    public ArrayList<String> getAllCustomSSID(){
        SharedPreferences sp = context.getSharedPreferences("autoLogin_SSID", Context.MODE_PRIVATE);

        Set<String> set = new HashSet<String>();

        set = sp.getStringSet("autoLogin_SSID", set);

        ArrayList<String> res = new ArrayList<String>();

        for(String i:set){
            res.add(i);
        }

        return res;
    }

    public void setAllCustomSSID(ArrayList<String> arr){
        SharedPreferences sp = context.getSharedPreferences("autoLogin_SSID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        HashSet<String> set = new HashSet<String>();
        for(String i:arr){
            set.add(i);
        }
        editor.putStringSet("autoLogin_SSD", set);
        editor.commit();
    }

    public void addCustomSSID(String ssid){
        SharedPreferences sp;
        sp = context.getSharedPreferences("autoLogin_SSID", Context.MODE_PRIVATE);
        Set<String> set ;
        set = sp.getStringSet("autoLogin_SSID", new HashSet<String>());
        Set<String> cpSet = new HashSet<String>();
        if(set != null) {
            for(String i : set){
                cpSet.add(i);
            }
        }
        cpSet.add(ssid);
        sp.edit().putStringSet("autoLogin_SSID", cpSet)
                .apply();
    }

    public boolean isAutoLogin(String SSID){
        SharedPreferences sp;
        String trimedSSID = SSID.substring(1, SSID.length() - 1);
        sp = context.getSharedPreferences("autoLogin_SSID", Context.MODE_PRIVATE);
        Set<String> set = sp.getStringSet("autoLogin_SSID", new HashSet<String>());
        if(set != null){
            for(String i : set){
                if(i.equals(trimedSSID))
                    return true;
            }
        }
        return false;


    }
}
