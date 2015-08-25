package denver.srprojectapp.service;

import android.content.Intent;

/**
 * Created by Denver on 14.07.2015.
 */
public class UrlHolder {
    public static final String url = "http://192.168.1.3:1488";

    public static String getRegistrationUrl(){
        return url + "/sr_app/v1/register";
    }

    public static String getLoginUrl(){
        return url + "/sr_app/v1/login";
    }


    ///////////
    public static String getProjectUrl(){
        return url + "/sr_app/v1/projects";
    }
    public static String getProjectUrlById(int projectId){
        return url + "/sr_app/v1/projects/"+ Integer.toString(projectId);
    }
public static String getProjectWithTaskAndUserTaskUrl(){return url + "/sr_app/v1/projectWithTaskAndUserTask";}



    public static String getTasksWithUserTaskUrl(){
        return url + "/sr_app/v1/tasks/withusertask";
    }
    public static String getTasksWithUserTaskUrlById(int taskId){
        return url + "/sr_app/v1/tasks/withusertask"+Integer.toString(taskId);
    }



    public static String getTasksUrl(){
        return url + "/sr_app/v1/tasks";
    }
    public static String getTasksUrlById(int Id){
        return url + "/sr_app/v1/tasks/"+ Integer.toString(Id);
    }

    public static String getUserTaskVersionUrl(){
        return url + "/user_task_version";
    }



    public static String getTasksStatusByIdUrl(int taskId){
        return url + "/sr_app/v1/tasks/status/"+ Integer.toString(taskId);
    }

    public static String getTaskByUserUrl(){ return url + "/sr_app/v1/tasks/user"; }
    public static String getAllBaseUserUrl(){return url + "/sr_app/v1/allBaseUsers";}
}
