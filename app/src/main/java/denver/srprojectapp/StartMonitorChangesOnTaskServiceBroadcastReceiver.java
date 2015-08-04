package denver.srprojectapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartMonitorChangesOnTaskServiceBroadcastReceiver extends BroadcastReceiver {
    public StartMonitorChangesOnTaskServiceBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intnt) {
        Intent intent = new Intent(context, MonitorChangesOnTasksIntentService.class);
        context.startService(intent);
        Log.i("AutostartMonitorChanges", "started");
    }
}
