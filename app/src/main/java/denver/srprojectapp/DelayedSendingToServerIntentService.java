package denver.srprojectapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class DelayedSendingToServerIntentService extends IntentService {

    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    SRProjectApplication srProjectApplication;
    Context context;


    private static DelayedSendingToServerIntentService instance = null;

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    public void onCreate() {
        super.onCreate();
        instance = this;

    }

    @Override
    public void onDestroy()
    {
        instance = null;
    }

    public DelayedSendingToServerIntentService() {
        super("DelayedSendingToServerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("onHandleIntent", "started");

        context = getApplicationContext();
        srProjectApplication = ((SRProjectApplication) getApplicationContext());
        setTimer();
    }


    public void setTimer() {

        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();

        mTimer.schedule(mMyTimerTask, context.getResources().getInteger(R.integer.delayToFirstTimerLaunch),
                srProjectApplication.getIntervalToResyncTask());

    }

    public void cancelTimer() {
         if (mTimer != null) {
             mTimer.cancel();
             mTimer = null;
         }
    }


    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if(InternetConnectionChecker.isNetworkConnected(context)) {
//////////////////////////////////////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                ///!!!!!!!!!!!!!!!!!!!!!!!!!!!!//////MAKELOGIK TO CONNECT SERV
//////////////////////////////////////////////////////////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                Log.d("myTimerRun", "wqewqe");

////LOGIK HERE
//////////////


                //cancelTimer();
            }
        }
    }


}
