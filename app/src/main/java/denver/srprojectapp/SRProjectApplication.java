package denver.srprojectapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denver on 11.07.2015.
 */
public class SRProjectApplication extends Application {

    private User user;
    private List<ProjectTask> userTasks;
    private List<Project> projectList;
    private List<GeneralUser> generalUserList;
    private SharedPreferences sPref;


    @Override
    public void onCreate() {
        super.onCreate();

        if (checkIsSavedUser() == true){
            user = loadUser();
        }
        if (checkIsSavedUserProjectTasks() == true){
            userTasks = loadUserProjectTasks();
        }

        projectList = new ArrayList<>();
        generalUserList = new ArrayList<>();
        userTasks = new ArrayList<>();


    }


    public void setUser(User user){this.user = user;}
    public boolean checkIsLoginedUser(){
        if(checkIsSavedUser() == false){return false;}
        else if (user.getUserApiKey() != "" && user.getUserApiKey() != null){return true;}
        return false;
    }
    public String getUserRights(){return user.getUserRights();}
    public User getUser(){return user;}


    public void setProjectList(List<Project> projectList){this.projectList = projectList;}
    public List<Project> getProjectList(){
            return projectList;
    }
    public boolean isExistProjectList(){
        if (projectList != null){
            if(projectList.size() > 0){
                return true;
            }
        }
        return false;
    }


    public void setGeneralUserList(List<GeneralUser> generalUserList){
        this.generalUserList = generalUserList;
    }
    public boolean isGeneralUserList(){
        if (generalUserList != null){
            if(generalUserList.size() > 0){
                return true;
            }
        }
        return false;
    }
    public List<GeneralUser> getGeneralUserList(){return generalUserList;}



///////////////////////////////////
///////////////preferences saver///
///////////////////////////////////

    private static final String PREFERENCES_USER = "userPref";
    private static final String PREFERENCES_USER_TASK_PROJECT = "userProjectTaskPref";

    private static final String NUMBER_SAVED_PROJECT_TASKS = "numbProjectTasks";

    private static final String SAVED_USER_ID = "userId";
    private static final String SAVED_USER_EMAIL = "userEmail";
    private static final String SAVED_USER_API_KEY = "userApiKey";
    private static final String SAVED_USER_NAME = "userName";
    private static final String SAVED_USER_RIGHTS = "userIdRights";

    private static final String SAVED_TASK_ID = "taskId";
    private static final String SAVED_TASK_TEXT = "taskText";
    private static final String SAVED_TASK_STATUS = "taskStatus";
    private static final String SAVED_TASK_PROJECT_ID = "taskProjectId";
    private static final String SAVED_PROJECT_TITLE = "projectId";


    public void saveUser() {
        sPref = getSharedPreferences(PREFERENCES_USER, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_USER_ID, Integer.toString(user.getUserId()));
        ed.putString(SAVED_USER_EMAIL, user.getUserEmail());
        ed.putString(SAVED_USER_API_KEY, user.getUserApiKey());
        ed.putString(SAVED_USER_NAME, user.getUserName());
        ed.putString(SAVED_USER_RIGHTS, user.getUserRights());

        ed.commit();
    }

    public User loadUser() {
        sPref = getSharedPreferences(PREFERENCES_USER, MODE_PRIVATE);
        int id = Integer.parseInt(sPref.getString(SAVED_USER_ID, ""));
        String email = sPref.getString(SAVED_USER_EMAIL, "");
        String apiKey = sPref.getString(SAVED_USER_API_KEY, "");
        String name = sPref.getString(SAVED_USER_NAME, "");
        String rights = sPref.getString(SAVED_USER_RIGHTS, "");
        User user = new User(id, email, apiKey, name, rights);
        return user;
    }

    public boolean checkIsSavedUser() {
        sPref = getSharedPreferences(PREFERENCES_USER, MODE_PRIVATE);
        String chk = sPref.getString(SAVED_USER_ID, "");
        if (chk != null && chk != "") {
            return true;
        } else {
            return false;
        }
    }

    private void deleteUserFromSaved(){
        sPref = getSharedPreferences(PREFERENCES_USER, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_USER_ID, "");
        ed.putString(SAVED_USER_EMAIL, "");
        ed.putString(SAVED_USER_API_KEY, "");
        ed.putString(SAVED_USER_NAME, "");
        ed.putString(SAVED_USER_RIGHTS, "");
        ed.commit();
    }

    public void deleteUser(){
        user = null;
        deleteUserFromSaved();;
    }

    public void saveUserProjectTasks(List<ProjectTask> prTasks) {
        sPref = getSharedPreferences(PREFERENCES_USER_TASK_PROJECT, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();

        ed.putString(NUMBER_SAVED_PROJECT_TASKS, Integer.toString(prTasks.size()));

        for (int i = 1; i <= prTasks.size(); ++i) {
            ed.putString(SAVED_TASK_ID + Integer.toString(i), Integer.toString(prTasks.get(i).getId()));
            ed.putString(SAVED_TASK_TEXT + Integer.toString(i), prTasks.get(i).getText());
            ed.putString(SAVED_TASK_STATUS + Integer.toString(i), Integer.toString(prTasks.get(i).getStatus()));
            ed.putString(SAVED_TASK_PROJECT_ID + Integer.toString(i), Integer.toString(prTasks.get(i).getProjectId()));
            ed.putString(SAVED_PROJECT_TITLE + Integer.toString(i), prTasks.get(i).getProjectTitle());
        }

        ed.commit();
    }

    public List<ProjectTask> loadUserProjectTasks() {
        sPref = getSharedPreferences(PREFERENCES_USER_TASK_PROJECT, MODE_PRIVATE);

        List<ProjectTask> prTasks = new ArrayList<ProjectTask>();

        int numOfSavedTasks = Integer.parseInt(sPref.getString(NUMBER_SAVED_PROJECT_TASKS, ""));
        for (int i = 1; i <= numOfSavedTasks; ++i) {
            int id = Integer.parseInt(sPref.getString(SAVED_TASK_ID + Integer.toString(i), ""));
            String text = sPref.getString(SAVED_TASK_TEXT + Integer.toString(i), "");
            int status = Integer.parseInt(sPref.getString(SAVED_TASK_STATUS + Integer.toString(i), ""));
            int projectId = Integer.parseInt(sPref.getString(SAVED_TASK_PROJECT_ID + Integer.toString(i), ""));
            String title = sPref.getString(SAVED_PROJECT_TITLE + Integer.toString(i), "");

            ProjectTask t = new ProjectTask(id, text, status, 0, projectId, title);
            prTasks.add(t);
        }

        return prTasks;
    }

    public boolean checkIsSavedUserProjectTasks() {
        sPref = getSharedPreferences(PREFERENCES_USER_TASK_PROJECT, MODE_PRIVATE);
        String chk = sPref.getString(NUMBER_SAVED_PROJECT_TASKS, "");
        if (chk != null && chk != "") {
            if (Integer.parseInt(chk) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
