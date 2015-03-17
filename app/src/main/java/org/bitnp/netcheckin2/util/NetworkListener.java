package org.bitnp.netcheckin2.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkListener extends BroadcastReceiver {
    public NetworkListener() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(intent.getAction());

    }
}
