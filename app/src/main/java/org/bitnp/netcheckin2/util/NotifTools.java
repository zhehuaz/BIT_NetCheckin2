package org.bitnp.netcheckin2.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.bitnp.netcheckin2.R;
import org.bitnp.netcheckin2.ui.MainActivity;

/**
 * Created by ental_000 on 2015/3/18.
 */
public class NotifTools {



    public static void sendNotification(Context context, String title, String content){
        NotificationManager mNotificationManager;

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pd = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setTicker(title)
                .setContentIntent(pd)
                //.setWhen(System.currentTimeMillis())
                //.setPriority(Notification.PRIORITY_DEFAULT)
                .setSmallIcon(R.mipmap.ic_launcher);
                //.setContentIntent(PendingIntent.getActivity(context,1,new Intent(context, MainActivity.class),Intent.));
        mNotificationManager.notify(0, mBuilder.build());
    }
}
