package denver.srprojectapp.service;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import denver.srprojectapp.objects.GeneralUser;
import denver.srprojectapp.objects.Project;
import denver.srprojectapp.objects.ProjectTask;
import denver.srprojectapp.objects.User;

/**
 * Created by Denver on 11.07.2015.
 */
public class SRProjectApplication extends Application {

    ///////////////////////////
    /// APP SETTINGS FIELDS
    ///////////////////////////
    private int intervalToResyncTask;
    private int intervalToCheckUpdateTask;


    private int versionOfUsersTasks;

    ///////////////////////////
    ///// USER FIELDS
    ///////////////////////////
    private User user;
    private List<ProjectTask> userTasks;
    private List<Project> projectList;
    private List<GeneralUser> generalUserList;
    private SharedPreferences sPref;


    @Override
    public void onCreate() {
        super.onCreate();

        loadIntervalToResyncTask();
        loadIntervalToCheckUpdateTask();

        projectList = new ArrayList<>();
        generalUserList = new ArrayList<>();
        userTasks = new ArrayList<>();

        if (checkIsSavedUser() == true){
            user = loadUser();
        }
        if (checkIsSavedUserProjectTasks() == true){
            loadUserProjectTasks();
        }

    }


    public void setIntervalToResyncTask(int intervalToResyncTask){this.intervalToResyncTask = intervalToResyncTask;}
    public void setIntervalToCheckUpdateTask(int intervalToCheckUpdateTask){this.intervalToCheckUpdateTask = intervalToCheckUpdateTask;}
    public int getIntervalToResyncTask(){return intervalToResyncTask;}
    public int getIntervalToCheckUpdateTask(){return intervalToCheckUpdateTask;}


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
    public Project getProjectById(int project_id){
        Project project = new Project(0,"",0,"","",0);
        for(int i = 0; i < projectList.size(); ++i) {
            if (projectList.get(i).getId() == project_id) {
                project = projectList.get(i);
            }
        }
        return project;
    }

    public void rewriteProjectById(Project project){
        for(int i = 0; i < projectList.size(); ++i){
            if(projectList.get(i).getId() == project.getId()){
                projectList.set(i,project);
            }
        }
    }

    public int getVersionOfUsersTasks(){return  versionOfUsersTasks;}
    public void setVersionOfUsersTasks(int version){this.versionOfUsersTasks = version;}


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
    public GeneralUser getGeneralUserById(int id){
        GeneralUser generalUser = new GeneralUser(0,"");
        for(int i = 0; i < generalUserList.size(); ++i){
            if (id == generalUserList.get(i).getUserId()){generalUser = generalUserList.get(i);}
        }
        return generalUser;
    }

    public void setUserTasks(List<ProjectTask> userTasks){
        this.userTasks = userTasks;
    }
    public List<ProjectTask> getUserTasks(){return userTasks;}
    public boolean isUserTaskList(){
        if (userTasks != null){
            if(userTasks.size() > 0){
                return true;
            }
        }
        return false;
    }

////////////////////////////////////////
///////////////USER preferences saver///
////////////////////////////////////////

    private static final String PREFERENCES_USER = "userPref";
    private static final String PREFERENCES_USER_TASK_PROJECT = "userProjectTaskPref";
    private static final String NUMBER_SAVED_PROJECT_TASKS = "numbProjectTasks";
    private static final String SAVED_VERSION_USER_TASK = "versionUserTask";

    private static final String SAVED_USER_ID = "userId";
    private static final String SAVED_USER_EMAIL = "userEmail";
    private static final String SAVED_USER_API_KEY = "userApiKey";
    private static final String SAVED_USER_NAME = "userName";
    private static final String SAVED_USER_RIGHTS = "userIdRights";

    private static final String SAVED_TASK_ID = "taskId";
    private static final String SAVED_TASK_TEXT = "taskText";
    private static final String SAVED_TASK_STATUS = "taskStatus";
    private static final String SAVED_TASK_PROJECT_ID = "taskProjectId";
    private static final String SAVED_PROJECT_TITLE = "projectTitle";



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
        Log.d("onSRApplicationDenv", "start saving");

        sPref = getSharedPreferences(PREFERENCES_USER_TASK_PROJECT, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(NUMBER_SAVED_PROJECT_TASKS, Integer.toString(prTasks.size()));
        ed.putString(SAVED_VERSION_USER_TASK, Integer.toString(versionOfUsersTasks));

        for (int i = 0; i < prTasks.size(); ++i) {
            ed.putString(SAVED_TASK_ID + Integer.toString(i), Integer.toString(prTasks.get(i).getId()));
            ed.putString(SAVED_TASK_TEXT + Integer.toString(i), prTasks.get(i).getText());
            ed.putString(SAVED_TASK_STATUS + Integer.toString(i), Integer.toString(prTasks.get(i).getStatus()));
            ed.putString(SAVED_TASK_PROJECT_ID + Integer.toString(i), Integer.toString(prTasks.get(i).getProjectId()));
            ed.putString(SAVED_PROJECT_TITLE + Integer.toString(i), prTasks.get(i).getProjectTitle());
        }
        Log.d("onSRApplicationDenv", "successfully saved");
        ed.commit();
    }


    public void loadUserProjectTasks() {
        sPref = getSharedPreferences(PREFERENCES_USER_TASK_PROJECT, MODE_PRIVATE);

        int numOfSavedTasks = Integer.parseInt(sPref.getString(NUMBER_SAVED_PROJECT_TASKS, ""));
        this.versionOfUsersTasks = Integer.parseInt(sPref.getString(SAVED_VERSION_USER_TASK, "0"));

        for (int i = 0; i < numOfSavedTasks; ++i) {
            int id = Integer.parseInt(sPref.getString(SAVED_TASK_ID + Integer.toString(i), ""));
            String text = sPref.getString(SAVED_TASK_TEXT + Integer.toString(i), "");
            int status = Integer.parseInt(sPref.getString(SAVED_TASK_STATUS + Integer.toString(i), ""));
            int projectId = Integer.parseInt(sPref.getString(SAVED_TASK_PROJECT_ID + Integer.toString(i), ""));
            String title = sPref.getString(SAVED_PROJECT_TITLE + Integer.toString(i), "");

            ProjectTask t = new ProjectTask(id, text, status, 0, projectId, title);
            userTasks.add(t);
        }


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

    public void deleteUserProjectTasks(){
        sPref = getSharedPreferences(PREFERENCES_USER_TASK_PROJECT, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(NUMBER_SAVED_PROJECT_TASKS, Integer.toString(0));
        ed.putString(SAVED_VERSION_USER_TASK, Integer.toString(0));

        int numOfSavedTasks = Integer.parseInt(sPref.getString(NUMBER_SAVED_PROJECT_TASKS, ""));
        for (int i = 0; i < numOfSavedTasks; ++i) {
            ed.putString(SAVED_TASK_ID + Integer.toString(i), "");
            ed.putString(SAVED_TASK_TEXT + Integer.toString(i), "");
            ed.putString(SAVED_TASK_STATUS + Integer.toString(i), "");
            ed.putString(SAVED_TASK_PROJECT_ID + Integer.toString(i), "");
            ed.putString(SAVED_PROJECT_TITLE + Integer.toString(i),"");
        }
        Log.d("onSRApplicationDenv", "successfully deleted");
        ed.commit();
    }

////////////////////////////////////////
///////////////SYSTEM preferences saver/
////////////////////////////////////////
    private static final String PREFERENCES_SYSTEM = "systemPref";
    private static final String SAVED_INTERVAL_TO_RESYNC_TASK = "systemIntervalResync";
    private static final String SAVED_INTERVAL_TO_CHECK_UPDATE_TASK = "systemIntervalToCheckUpd";

    public void saveIntervalToResyncTask() {
        sPref = getSharedPreferences(PREFERENCES_SYSTEM, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_INTERVAL_TO_RESYNC_TASK, Integer.toString(this.getIntervalToResyncTask()));
        ed.commit();
    }

    public void saveIntervalToCheckUpdateTask() {
        sPref = getSharedPreferences(PREFERENCES_SYSTEM, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_INTERVAL_TO_CHECK_UPDATE_TASK, Integer.toString(this.getIntervalToCheckUpdateTask()));
        ed.commit();
    }

    public void loadIntervalToResyncTask() {
        sPref = getSharedPreferences(PREFERENCES_SYSTEM, MODE_PRIVATE);
        this.intervalToResyncTask = Integer.parseInt(sPref.getString(SAVED_INTERVAL_TO_RESYNC_TASK, "600000"));
    }

    public void loadIntervalToCheckUpdateTask() {
        sPref = getSharedPreferences(PREFERENCES_SYSTEM, MODE_PRIVATE);
        this.intervalToCheckUpdateTask = Integer.parseInt(sPref.getString(SAVED_INTERVAL_TO_CHECK_UPDATE_TASK, "600000"));
    }


}
