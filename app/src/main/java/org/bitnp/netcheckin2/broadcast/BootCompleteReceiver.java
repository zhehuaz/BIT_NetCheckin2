package org.bitnp.netcheckin2.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.bitnp.netcheckin2.service.LoginService;

/**
 * Created by langley on 4/2/15.
 */
public class BootCompleteReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, LoginService.class);
        String action = LoginService.COMMAND_START_LISTEN;
        service.putExtra("command", action);
        context.startService(service);
    }
}
