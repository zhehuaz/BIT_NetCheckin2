package org.bitnp.netcheckin2.ui;

import android.app.Application;
import android.util.Log;

import com.xiaomi.mistatistic.sdk.MiStatInterface;

/**
 * Created by langley on 4/9/15.
 */
public class MainApp extends Application {

    /** Used for Xiaomi States service */
    private static final String appID = "2882303761517318026";
    private static final String appKey = "5261731875026";
    @Override
    public void onCreate() {
        /** Xiaomi States API*/
        MiStatInterface.initialize(this.getApplicationContext(), appID, appKey, "Mi");
        MiStatInterface.setUploadPolicy(MiStatInterface.UPLOAD_POLICY_WIFI_ONLY, 0);
        MiStatInterface.enableLog();
        MiStatInterface.enableExceptionCatcher(false);

        super.onCreate();
    }
}
