package com.example.jayanth.fmk_lower;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidAlarmSMS extends Activity
{

    String smsNumber;
    private static String time_intr;
    TextView stat;
    EditText time;

    private Context context;
    PackageManager packageManager;
    ComponentName componentName;
    private PendingIntent pendingIntent;
    private static final int REPEAT_TIME_IN_SECONDS = 60;
    static  int time_space=0;

    /*protected void check_entry()
    {
        if("".equals(time))
        {
            Toast.makeText(AndroidAlarmSMS.this,"Enter all the details...!!",Toast.LENGTH_LONG).show();
            check_entry();
        }
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_android_alarm_sms);
        final EditText edittextSmsNumber = (EditText) findViewById(R.id.smsnumber);
        stat = (TextView) findViewById(R.id.status);
        Button buttonStart = (Button) findViewById(R.id.startalarm);
        Button buttonCancel = (Button) findViewById(R.id.cancelalarm);
        Button hide_app = (Button) findViewById(R.id.hide);

        time = (EditText) findViewById(R.id.time_in);

        hide_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packageManager = context.getPackageManager();
                componentName = new ComponentName(context, AndroidAlarmSMS.class);
                packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
                Toast.makeText(AndroidAlarmSMS.this, "I'm Going to hide", Toast.LENGTH_LONG).show();

                onPause();
            }
        });

        buttonStart.setOnClickListener(new Button.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                smsNumber = edittextSmsNumber.getText().toString();

                Intent myIntent = new Intent(AndroidAlarmSMS.this, MyService2.class);
                Bundle bundle = new Bundle();
                bundle.putCharSequence("extraSmsNumber", smsNumber);
                myIntent.putExtras(bundle);

                if("".equals(time))
                {
                    Log.d("invalid time ", String.valueOf(time));
                }
                else
                {
                    time_intr = String.valueOf(time.getText());
                    time_space = Integer.parseInt(time_intr);
                }

                pendingIntent = PendingIntent.getService(AndroidAlarmSMS.this, 0, myIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),time_space * REPEAT_TIME_IN_SECONDS * 1000, pendingIntent);

                Toast.makeText(AndroidAlarmSMS.this, "Service started", Toast.LENGTH_LONG).show();
                stat.setText("Service Running");
                stat.setTextColor(Color.WHITE);
                stat.setBackgroundColor(Color.rgb(30, 150, 72));

            }
        });

        buttonCancel.setOnClickListener(new Button.OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                stopService(new Intent(AndroidAlarmSMS.this, MyService2.class));
                Toast.makeText(AndroidAlarmSMS.this, "Service Canceled!", Toast.LENGTH_LONG).show();
                stat.setText("Service Stopped");
                stat.setTextColor(Color.WHITE);
                stat.setBackgroundColor(Color.rgb(209, 20, 67));
            }
        });
    }
}