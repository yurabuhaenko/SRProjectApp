package denver.srprojectapp.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import denver.srprojectapp.service.InternetConnectionChecker;
import denver.srprojectapp.objects.ProjectTask;
import denver.srprojectapp.R;
import denver.srprojectapp.service.SRProjectApplication;
import denver.srprojectapp.service.ServiceServerHandler;
import denver.srprojectapp.service.UrlHolder;
import denver.srprojectapp.activitys.UserTaskActivity;

//////////////////////////////////////////////////////////
///////////// MONITOR CHANGES ON TASKS///////////////////
/////////////////////////////////////////////////////////


public class MonitorChangesOnTasksIntentService extends IntentService {

    private static MonitorChangesOnTasksIntentService instance = null;

    SRProjectApplication srProjectApplication;
    Context context;
    boolean isSuccess = false;

    public MonitorChangesOnTasksIntentService() {
        super("MonitorChangesOnTasksIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d("onUploadingVersUTDenv", "onCreate");
    }

    @Override
    public void onDestroy()
    {
        instance = null;
        Log.d("onUploadingVersUTDenv", "onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("onUploadingVersUTDenv", "started");
        context = getApplicationContext();

        srProjectApplication = ((SRProjectApplication) getApplicationContext());

        if (InternetConnectionChecker.isNetworkConnected(context)){
            int version = getCurrentVersionOfUserTaskFromServer();
            if (version > srProjectApplication.getVersionOfUsersTasks()){
                List<ProjectTask> projectTaskList = getAllUserTasksFromServer();
                if (isSuccess) {
                    srProjectApplication.setVersionOfUsersTasks(version);
                    srProjectApplication.setUserTasks(projectTaskList);

                    Log.d("onUploadingVersUTDenv", "user task size = " + Integer.toString(srProjectApplication.getUserTasks().size()));
                    srProjectApplication.saveUserProjectTasks(projectTaskList);
                    Log.d("onUploadingVersUTDenv", "successfully loaded on app");
                    makeNotificationAllTasksIsLoaded();

                } else {
                    Log.d("onUploadingVersUTDenv", "do not loaded on app");
                }
            }
        }

    }

    private int getCurrentVersionOfUserTaskFromServer(){
        int version = 0;
        ServiceServerHandler sh = new ServiceServerHandler();
        Log.d("onUploadingVersUTDenv", "serv_load_success");
        String jsonStr = sh.makeServiceCall(UrlHolder.getUserTaskVersionUrl(), ServiceServerHandler.GET, null, srProjectApplication.getUser().getUserApiKey() );
        Log.d("onUploadingVersUTDenv", jsonStr);
        if(jsonStr != null){
            JSONObject jsonObjUserTask = null;
            String error = "";
            try {
                jsonObjUserTask = new JSONObject(jsonStr);
                error = jsonObjUserTask.getString(ServiceServerHandler.TAG_ERROR);
                if (error == "false") {
                    version = Integer.parseInt(jsonObjUserTask.getString("version"));
                    Log.d("onUploadingVersUTDenv", "serv_load_version_success");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return version;
    }

    private List<ProjectTask> getAllUserTasksFromServer(){
        List<ProjectTask> userTasks = new ArrayList<>();

        ServiceServerHandler sh = new ServiceServerHandler();
        Log.d("onUploadingVersUTDenv", "serv_load_success");
        String jsonStr = sh.makeServiceCall(UrlHolder.getTaskByUserUrl(), ServiceServerHandler.GET, null, srProjectApplication.getUser().getUserApiKey() );
        Log.d("onUploadingVersUTDenv", jsonStr);
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
                        Log.d("onUploadingVersUTDenv", Integer.toString(userTasks.get(i).getId()));
                    }

                    isSuccess = true;
                    Log.d("onUploadingVersUTDenv", "serv_load_success");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return userTasks;
    }

    private void makeNotificationAllTasksIsLoaded(){
        final int NOTIFY_ID = 1666;
        Log.d("onUploadingVersUTDenv", "notification started");
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
                .setContentText(getResources().getString(R.string.notifySuccessNewTasks));

        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
        Log.d("onUploadingVersUTDenv", "notification created");
    }





}
