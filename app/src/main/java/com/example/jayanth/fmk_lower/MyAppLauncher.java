package com.example.jayanth.fmk_lower;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by jayanth on 4/11/2017.
 */
public class MyAppLauncher extends BroadcastReceiver
{
    private static final String TAG = null;
    private PackageManager packageManager;
    private ComponentName componentName;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
        {
            String phoneNumber = intent.getExtras().getString(Intent.EXTRA_PHONE_NUMBER);
            Log.i(TAG, "Received Outgoing Call");
            if (phoneNumber.equals("#*7676*#"))
            {
                packageManager = context.getPackageManager();
                componentName = new ComponentName(context, AndroidAlarmSMS.class);
                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            }
        }
    }
}
