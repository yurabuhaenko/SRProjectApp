package denver.srprojectapp.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import denver.srprojectapp.service.SRProjectApplication;

public class StarterBroadcastReceiver extends BroadcastReceiver {
    public StarterBroadcastReceiver() {
    }

    SRProjectApplication srProjectApplication;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onStarterBroadcastDenv", "onReceive");
        srProjectApplication = ((SRProjectApplication) context.getApplicationContext());


        if(srProjectApplication.checkIsLoginedUser()){
             if (!srProjectApplication.checkIsSavedUserProjectTasks()) {
                Intent myIntent = new Intent(context,
                StartUploadAllUserTasksBroadcastReceiver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 10);

                Log.d("onStarterBroadcastDenv", "" +
                "create alarm");
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), 60000, pendingIntent);
             }////////////////відловити назад відміну.

            Intent myIntentMonitorChanges = new Intent(context,
                    StartMonitorChangesOnTaskServiceBroadcastReceiver.class);

            PendingIntent pendingIntentMonitorChanges = PendingIntent.getBroadcast(context, 0, myIntentMonitorChanges, 0);

            AlarmManager alarmManagerMonitorChanges = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 10);

            Log.d("onStarterBroadcastDenv", "" +
                    "create alarm MonitorChanges");
            alarmManagerMonitorChanges.setRepeating(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), srProjectApplication.getIntervalToCheckUpdateTask(), pendingIntentMonitorChanges);

        }else{
            Log.d("onStarterBroadcastDenv", "no user logined");
        }


    }

}
      /*      if (!srProjectApplication.checkIsSavedUserProjectTasks()) {
        Intent myIntent = new Intent(context,
        StartUploadAllUserTasksBroadcastReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);

        Log.d("onStarterBroadcastDenv", "" +
        "create alarm");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
        calendar.getTimeInMillis(), 60000, pendingIntent);
        }////////////////відловити назад відміну.
        */