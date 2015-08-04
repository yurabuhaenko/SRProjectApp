package denver.srprojectapp;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends NavigationDrawerActivity {
    private View mProgressGetProjectsMainView;
    private View mGetProjectsMainView;
    ProjectListAdapter projectListAdapter;
    public static Context contextOfApplication;
    TextView textView;
    RelativeLayout rl_rel;
    SRProjectApplication srProjectApplication;
    ListView listViewProjects;
    //List<Project> projectList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        srProjectApplication = ((SRProjectApplication) getApplicationContext());
        postCreate(savedInstanceState,R.layout.activity_main);

        //showProgress(true);
        if(!srProjectApplication.isExistProjectList()) {
            if (InternetConnectionChecker.isNetworkConnected(MainActivity.this)) {
                GetProjectsAndBaseUsersFromServer mAuthTask = new GetProjectsAndBaseUsersFromServer(MainActivity.this);
                mAuthTask.execute((Void) null);
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.no_internet_connection_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        mGetProjectsMainView = findViewById(R.id.rel_lay_main);
        mProgressGetProjectsMainView = findViewById(R.id.get_projects_progress_on_main);

        //contextOfApplication = getApplicationContext();


        listViewProjects = (ListView)findViewById(R.id.listViewProjects);



        listViewProjects.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                int idProject = srProjectApplication.getProjectList().get(position).getId();
                Intent intent = new Intent(MainActivity.this, EditProjectActivity.class);
                intent.putExtra("project_id", idProject);
                startActivity(intent);
                return true;
            }
        });




    }

    public void onResume(){
        super.onResume();
        setProjectListOnView(MainActivity.this);

    }

    private void setProjectListOnView(Context context){
        projectListAdapter = new ProjectListAdapter(context, srProjectApplication.getProjectList());

        if(srProjectApplication.isExistProjectList())
        {
            listViewProjects.setAdapter(projectListAdapter);
        }else
        {
            listViewProjects.setAdapter(null);
        }
    }


    public class GetProjectsAndBaseUsersFromServer extends AsyncTask<Void, Void, Boolean> {

        List<Project> mProjectList;
        Context context;
        ///
        private String mError;
        ///

        GetProjectsAndBaseUsersFromServer(Context context) {
            this.context = context;
            mProjectList = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            SRProjectApplication srProjectApplication = (SRProjectApplication)getApplicationContext();
            //////////////get base users
            ServiceServerHandler sh = new ServiceServerHandler();

            String jsonStr = sh.makeServiceCall(UrlHolder.getAllBaseUserUrl(), ServiceServerHandler.GET, null, srProjectApplication.getUser().getUserApiKey() );
            if(jsonStr != null){
                JSONObject jsonObjUsers = null;
                String error = "";
                try {
                    jsonObjUsers = new JSONObject(jsonStr);
                    error = jsonObjUsers.getString(ServiceServerHandler.TAG_ERROR);

                    if (error == "false") {
                        List<GeneralUser> generalUserList = new ArrayList<>();
                        JSONArray jsonArrayUsers = jsonObjUsers.getJSONArray("users");

                        for(int i = 0; i < jsonArrayUsers.length(); ++i){
                            JSONObject jsonUser = jsonArrayUsers.getJSONObject(i);
                            int id = jsonUser.getInt("id");
                            String name = jsonUser.getString("name");
                            generalUserList.add(new GeneralUser(id,name));
                        }

                        srProjectApplication.setGeneralUserList(generalUserList);

                    } else {
                        mError = jsonObjUsers.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


            ///////////get projects
            sh = new ServiceServerHandler();
            jsonStr = null;
            jsonStr = sh.makeServiceCall(UrlHolder.getProjectWithTaskAndUserTaskUrl(), ServiceServerHandler.GET, null, srProjectApplication.getUser().getUserApiKey() );


            if (jsonStr != null) {

                JSONObject jsonObj = null;
                String error = "";
                try {
                    jsonObj = new JSONObject(jsonStr);
                    error = jsonObj.getString(ServiceServerHandler.TAG_ERROR);

                    if (error == "false") {
                        List<Project> projectsList = new ArrayList<>();
                        JSONArray jsonArrayProjects = jsonObj.getJSONArray("projects");

                        for(int i = 0; i < jsonArrayProjects.length(); ++i){
                            JSONObject jsonProject = jsonArrayProjects.getJSONObject(i);
                            JSONArray jsonArrayTasks = jsonProject.getJSONArray("tasks");
                            List<TaskWithUsersSetted> tasksWithUsers = new ArrayList<>();

                            if(jsonArrayTasks != null){

                                for (int j = 0; j < jsonArrayTasks.length(); ++j){
                                    JSONObject jsonTask = jsonArrayTasks.getJSONObject(j);
                                    JSONArray jsonArrayUserTasks = jsonTask.getJSONArray("users_tasks");

                                    List<GeneralUser> listUsers = new ArrayList<>();
                                    Task task = new Task(jsonTask.getInt("id"),jsonTask.getString("text"),jsonTask.getInt("status"),jsonTask.getInt("created_by_id"),jsonTask.getInt("project_id"));

                                    if(jsonArrayUserTasks != null){
                                        for(int k = 0; k < jsonArrayUserTasks.length(); ++k){
                                            JSONObject jsonUserTask = jsonArrayUserTasks.getJSONObject(k);
                                            listUsers.add(new GeneralUser(jsonUserTask.getInt("user_id"), srProjectApplication.getGeneralUserById(jsonUserTask.getInt("user_id")).getUserName()));
                                        }
                                    }
                                    tasksWithUsers.add(new TaskWithUsersSetted(task,listUsers));
                                }
                            }

                            Project project = new Project(jsonProject.getInt("project_id"),jsonProject.getString("title"),jsonProject.getInt("status"), jsonProject.getString("description"), jsonProject.getString("datetime"),jsonProject.getInt("created_by_id"), tasksWithUsers);
                            projectsList.add(project);
                        }

                        ////save project to application
                        srProjectApplication.setProjectList(projectsList);

                        return true;

                    } else {
                        mError = jsonObj.getString("message");
                        /*Toast toast = Toast.makeText(getApplicationContext(),
                                jsonObj.getString(ServerApplication.TAG_ERROR), Toast.LENGTH_LONG);
                        toast.show();*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//
                return false;

            }
            return false;
        }


        @Override
        protected void onPostExecute ( final Boolean success){

            showProgress(false);

            if (success) {
                setProjectListOnView(context);
            }else{
                // mPasswordView.setError(getString(R.string.error_incorrect_password));
                Toast toast = Toast.makeText(getApplicationContext(),
                        mError, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }


    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mGetProjectsMainView.setVisibility(show ? View.GONE : View.VISIBLE);
            mGetProjectsMainView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mGetProjectsMainView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressGetProjectsMainView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressGetProjectsMainView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressGetProjectsMainView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressGetProjectsMainView.setVisibility(show ? View.VISIBLE : View.GONE);
            mGetProjectsMainView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public void onClickAddProjectFAB(View view){
        Intent intent = new Intent(MainActivity.this, CreateProjectActivity.class);
        startActivity(intent);
    }

    public void setContextOfApplication(){contextOfApplication = getApplicationContext();}

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }


}
