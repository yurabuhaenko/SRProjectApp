package denver.srprojectapp;


import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends NavigationDrawerActivity {

    public static Context contextOfApplication;
    TextView textView;
    RelativeLayout rl_rel;
    SRProjectApplication srProjectApplication;
    ListView listViewProjects;
    List<Project> projectList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        srProjectApplication = ((SRProjectApplication) getApplicationContext());
        postCreate(savedInstanceState,R.layout.activity_main);

        //contextOfApplication = getApplicationContext();


        listViewProjects = (ListView)findViewById(R.id.listViewProjects);

        projectList = new ArrayList<>();
        if(srProjectApplication.isExistProjectList()){
            projectList = srProjectApplication.getProjectList();
        }
        ProjectListAdapter projectListAdapter = new ProjectListAdapter(this, projectList);

        if(srProjectApplication.isExistProjectList())
        {
            listViewProjects.setAdapter(projectListAdapter);
        }else
        {
            listViewProjects.setAdapter(null);
        }

        listViewProjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                /*int idProject = projectList.get(position).getId();
                Intent intent = new Intent(MainActivity.this, EditReminderActivity.class);
                intent.putExtra("id", idReminder);
                startActivity(intent);*/

            }
        });


    }

    public void onResume(){
        super.onResume();
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
