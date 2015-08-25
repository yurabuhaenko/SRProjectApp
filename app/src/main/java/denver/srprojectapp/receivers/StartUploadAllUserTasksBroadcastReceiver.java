package denver.srprojectapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import denver.srprojectapp.services.UploadingAllUserTasksIntentService;

public class StartUploadAllUserTasksBroadcastReceiver extends BroadcastReceiver {
    public StartUploadAllUserTasksBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intnt) {
        Log.d("onUploadBroadcastDenv", "onReceive");
        Intent intent = new Intent(context, UploadingAllUserTasksIntentService.class);
        context.startService(intent);
    }
}
