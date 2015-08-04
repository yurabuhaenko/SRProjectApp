package denver.srprojectapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MonitorChangesOnTasksIntentService extends IntentService {

    private static MonitorChangesOnTasksIntentService instance = null;

    SRProjectApplication srProjectApplication;
    Context context;
    private Timer mTimer;
    private GetUserTaskFromServerTimerTask mMyTimerTask;

    public MonitorChangesOnTasksIntentService() {
        super("MonitorChangesOnTasksIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d("onHandleIntent", "onCreate");
    }

    @Override
    public void onDestroy()
    {
        instance = null;
        Log.d("onHandleIntent", "onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("onHandleIntent", "started");
        context = getApplicationContext();

        srProjectApplication = ((SRProjectApplication) getApplicationContext());

        if(srProjectApplication.checkIsLoginedUser()){
            if(srProjectApplication.isUserTaskList()){
                //////if exist saved user task list
                Log.d("onHandleIntent", "user task list is on cashe");
            }
            else{
                Log.d("onHandleIntent", "setUserTaskFromServerTimer");
                setUserTaskFromServerTimer();
            }
        }

    }


//////////////////////////////////////////////////////////
///////////// MONITOR CHANGES ON TASKS///////////////////
/////////////////////////////////////////////////////////





/////////////////////////////////////////////////////////////////////
///////////// LOADING ALL USER TASKS IF NOT EXIST////////////////////
/////////////////////////////////////////////////////////////////////

    public void setUserTaskFromServerTimer() {

        if (mTimer != null) {
            mTimer.cancel();
        }

        mTimer = new Timer();
        mMyTimerTask = new GetUserTaskFromServerTimerTask();

        mTimer.schedule(mMyTimerTask, 60000, 60000);
        Log.d("onHandleIntent", "schedule");
    }

    public void cancelGetUserTaskFromServerTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    class GetUserTaskFromServerTimerTask extends TimerTask {

        boolean isSuccess = false;

        @Override
        public void run() {
            Log.d("onHandleIntent", "isRunning");
            if (InternetConnectionChecker.isNetworkConnected(context)){
                Log.d("onHandleIntent", "NetworkIsConnected");
                srProjectApplication.setUserTasks(getAllUserTasksFromServer());
                if(isSuccess){
                    Log.d("onHandleIntent", "successfully loaded on app");
                    makeNotificationAllTasksIsLoaded();
                    cancelGetUserTaskFromServerTimer();
                }

            }
        }

        private void makeNotificationAllTasksIsLoaded(){
            final int NOTIFY_ID = 1488;
            Intent notificationIntent = new Intent(context, UserTaskActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_action_toggle_star)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_toggle_star))
                    .setTicker(getResources().getString(R.string.notify))
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle(getResources().getString(R.string.notify))
                    .setContentText(getResources().getString(R.string.notifyTaskSuccess));

            // Notification notification = builder.getNotification(); // до API 16
            Notification notification = builder.build();

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFY_ID, notification);
            Log.d("onHandleIntent", "notification created");
        }

        private List<ProjectTask> getAllUserTasksFromServer(){
            List<ProjectTask> userTasks = new ArrayList<>();

            ServiceServerHandler sh = new ServiceServerHandler();

            String jsonStr = sh.makeServiceCall(UrlHolder.getTaskByUserUrl(), ServiceServerHandler.GET, null, srProjectApplication.getUser().getUserApiKey() );

            if(jsonStr != null){
                JSONObject jsonObjUserTask = null;
                String error = "";

                try {
                    jsonObjUserTask = new JSONObject(jsonStr);
                    error = jsonObjUserTask.getString(ServiceServerHandler.TAG_ERROR);

                    if (error == "false") {

                        JSONArray jsonArrayTasks = jsonObjUserTask.getJSONArray("tasks");

                        for(int i = 0; i < jsonArrayTasks.length(); ++i){
                            JSONObject jsonUserTask = jsonArrayTasks.getJSONObject(i);

                            int id = jsonUserTask.getInt("id");
                            String text = jsonUserTask.getString("text");
                            int status = Integer.parseInt(jsonUserTask.getString("status"));
                            int createdById = Integer.parseInt(jsonUserTask.getString("created_by_id"));
                            int projectId = Integer.parseInt(jsonUserTask.getString("project_id"));
                            String projectTitle = jsonUserTask.getString("project_title");

                            userTasks.add(new ProjectTask(id,text,status,createdById,projectId,projectTitle));
                        }

                        isSuccess = true;
                        Log.d("onHandleIntent", "serv_load_success");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return userTasks;
        }


    }


}
