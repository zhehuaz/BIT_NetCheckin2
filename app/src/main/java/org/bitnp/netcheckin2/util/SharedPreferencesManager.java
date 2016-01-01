package org.bitnp.netcheckin2.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by ental_000 on 2015/3/17.
 */
public class SharedPreferencesManager {

    public final static String KEY_AUTO_LOGOUT = "auto_logout";
    public final static String KEY_SILENCE = "silence mode";
    public final static String KEY_RELOG_INTERVAL = "relog_interval";

    public void setListener(PreferenceChangedListener listener) {
        this.listener = listener;
    }

    private void updatePreference(PreferenceChangedListener.PreferenceKey key){
        if(listener != null)
            listener.onPreferenceChanged(key);
    }

    PreferenceChangedListener listener;
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
        editor.apply();
        updatePreference(PreferenceChangedListener.PreferenceKey.USERNAME);
    }

    public String getPassword(){
        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        return sp.getString("password", "");
    }

    public void setPassword(String password){
        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("password", password);
        editor.apply();
        updatePreference(PreferenceChangedListener.PreferenceKey.PASSWORD);
    }

    public void setUID(String uid){
        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        sp.edit()
            .putString("uid", uid)
            .apply();
        updatePreference(PreferenceChangedListener.PreferenceKey.UID);
    }

    public String getUID(){
        SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        return sp.getString("uid", "");
    }

    public boolean getIsAutoLogin(){
        SharedPreferences sp = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
        return sp.getBoolean("autologin", false);
    }

    public void setIsAutoLogin(boolean value){
        SharedPreferences sp = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("autologin", value);
        editor.apply();
        updatePreference(PreferenceChangedListener.PreferenceKey.IS_AUTO_LOGIN);
    }

    public boolean getIsAutoCheck(){
        SharedPreferences sp = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
        return true;//sp.getBoolean("autocheck", true);
    }

    public void setIsAutoCheck(boolean value){
        SharedPreferences sp = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("autocheck", value);
        editor.apply();
        updatePreference(PreferenceChangedListener.PreferenceKey.IS_KEEPALIVE);
    }

    public long getAutoCheckTime(){
        SharedPreferences sp = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
        return sp.getLong("autochecktime_millis", 20 * 1000);
    }

    public void setAutoCheckTime(long value){
        SharedPreferences sp = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("autochecktime_millis", value);
        editor.apply();
        updatePreference(PreferenceChangedListener.PreferenceKey.INTERVAL);
    }

    public ArrayList<String> getAllCustomSsid(){
        SharedPreferences sp = context.getSharedPreferences("autoLogin_SSID", Context.MODE_PRIVATE);

        Set<String> set = new HashSet<String>();

        set = sp.getStringSet("autoLogin_SSID", set);

        ArrayList<String> res = new ArrayList<String>();

        for(String i:set){
            res.add(i);
        }

        return res;
    }

    public void setAllCustomSsid(ArrayList<String> arr){
        SharedPreferences sp = context.getSharedPreferences("autoLogin_SSID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        HashSet<String> set = new HashSet<String>();
        for(String i:arr){
            set.add(i);
        }
        editor.putStringSet("autoLogin_SSD", set);
        editor.apply();
    }

    /**
     * @return if this ssid already exists, true means ssid is new
     * */
    public boolean addCustomSsid(String ssid){
        boolean newFlag = true;
        SharedPreferences sp;
        sp = context.getSharedPreferences("autoLogin_SSID", Context.MODE_PRIVATE);
        Set<String> set ;
        set = sp.getStringSet("autoLogin_SSID", new HashSet<String>());
        if(set.size() >= 10)
            return false;
        Set<String> cpSet = new HashSet<String>();
        if(set != null) {
            for(String i : set){
                if(i.equals(ssid))
                    newFlag = false;
                else
                    cpSet.add(i);
            }
        }
        cpSet.add(ssid);
        sp.edit().putStringSet("autoLogin_SSID", cpSet)
                .apply();
        return newFlag;
    }

    public static String trimSsid(String ssid) {
        if(ssid != null && ssid.length() > 0 && !ssid.equals("<unknown ssid>")) {
            if(ssid.length() > 2 && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                return ssid.substring(1, ssid.length() - 1);
            } else {
                return ssid;
            }
        }
        return "";
    }

    public void deleteSsid(String ssid){
        SharedPreferences sp = context.getSharedPreferences("autoLogin_SSID", Context.MODE_PRIVATE);
        Set<String> set;
        set = sp.getStringSet("autoLogin_SSID", new HashSet<String>());
        Set<String> cpSet = new HashSet<>();
        if(set != null){
            for(String i : set)
                if(!i.equals(ssid))
                    cpSet.add(i);
        }
        sp.edit().putStringSet("autoLogin_SSID", cpSet).apply();
    }

    public boolean isAutoLogin(String ssid){
        SharedPreferences sp = context.getSharedPreferences("autoLogin_SSID", Context.MODE_PRIVATE);
        String trimedSSID = trimSsid(ssid);
        //sp = context.getSharedPreferences("autoLogin_SSID", Context.MODE_PRIVATE);
        Set<String> set = sp.getStringSet("autoLogin_SSID", new HashSet<String>());

        return set.contains(trimedSSID);
    }

    public boolean getIsAutoLogout(){
        SharedPreferences sp = context.getSharedPreferences(KEY_AUTO_LOGOUT, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_AUTO_LOGOUT, false);
    }

    public void setIsAutoLogout(boolean value){
        SharedPreferences sp = context.getSharedPreferences(KEY_AUTO_LOGOUT, Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY_AUTO_LOGOUT, value).apply();
        updatePreference(PreferenceChangedListener.PreferenceKey.IS_AUTO_LOGOUT);
    }

    public void setIsSilent(boolean value){
        SharedPreferences sp = context.getSharedPreferences(KEY_SILENCE, Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY_SILENCE, value).apply();
        updatePreference(PreferenceChangedListener.PreferenceKey.IS_SLIENT);
    }

    public boolean getIsSilent(){
        SharedPreferences sp = context.getSharedPreferences(KEY_SILENCE, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_SILENCE, false);
    }

    public void setRelogInterval(long interval){
        SharedPreferences sp = context.getSharedPreferences(KEY_RELOG_INTERVAL, Context.MODE_PRIVATE);
        sp.edit().putLong(KEY_RELOG_INTERVAL, interval).apply();
        updatePreference(PreferenceChangedListener.PreferenceKey.RELOG_INTERVAL);
    }

    public long getRelogInterval(){
        SharedPreferences sp = context.getSharedPreferences(KEY_RELOG_INTERVAL, Context.MODE_PRIVATE);
        return sp.getLong(KEY_RELOG_INTERVAL, 10000);

    }
}
