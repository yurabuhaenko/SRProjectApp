package denver.srprojectapp;

/**
 * Created by Denver on 14.07.2015.
 */
public class UrlHolder {
    public static final String url = "http://192.168.1.132:1488";

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

    public static String getTasksWithUserTaskUrl(){
        return url + "/sr_app/v1/tasks/withusertask";
    }

    public static String getTasksUrl(){
        return url + "/sr_app/v1/tasks";
    }

    public static String getTasksUrlById(int userId){
        return url + "/sr_app/v1/tasks/"+ Integer.toString(userId);
    }

}
