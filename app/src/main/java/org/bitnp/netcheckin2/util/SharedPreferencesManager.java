package org.bitnp.netcheckin2.util;

import android.content.Context;
import android.content.SharedPreferences;

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
}
