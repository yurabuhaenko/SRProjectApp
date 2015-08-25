package denver.srprojectapp.activitys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import denver.srprojectapp.R;
import denver.srprojectapp.service.SRProjectApplication;
import denver.srprojectapp.service.UserRights;



    /*protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
    }*/



public class NavigationDrawerActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBar actionBar;
    TextView textView;
    FrameLayout frameLayout;
    RelativeLayout fullLayout;
    NavigationView navigationView;

    SRProjectApplication srProjectApplication;

    TextView textViewUserName;
    TextView textViewUserEmail;


    public void postCreate(Bundle savedInstanceState, int ID) {
        super.onPostCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        srProjectApplication = (SRProjectApplication)getApplicationContext();
        textViewUserName = (TextView) findViewById(R.id.text_view_header_user_name);
        textViewUserEmail = (TextView) findViewById(R.id.text_view_header_user_email);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("DDIT_Results");
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ViewGroup inclusionViewGroup = (ViewGroup)findViewById(R.id.inclusionlayout);
        View child1 = LayoutInflater.from(this).inflate(
                ID, null);
        inclusionViewGroup.addView(child1);

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        setHeaderUser();
        setMenuForUserRights();
        //(R.id.item_navigation_drawer_custom);
        //navigationView.getMenu().setGroupVisible(R.id.group_full_menu, false);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }

        setupNavigationDrawerContent(navigationView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setMenuForUserRights();
        setHeaderUser();
    }

    public void setHeaderUser(){
        if (srProjectApplication.checkIsLoginedUser()==true){
            textViewUserName.setText(srProjectApplication.getUser().getUserName());
            textViewUserEmail.setText(srProjectApplication.getUser().getUserEmail());
        }
        else{
            textViewUserName.setText("You need to sign in!");
            textViewUserEmail.setText("");
        }
    }

    public void setMenuForUserRights(){
        SRProjectApplication app = ((SRProjectApplication) getApplicationContext());
        if(app.checkIsLoginedUser() == true){
            if(app.getUserRights().equals(UserRights.FULL_RIGHTS)){
                navigationView.getMenu().setGroupVisible(R.id.group_base_menu, true);
                navigationView.getMenu().setGroupVisible(R.id.group_full_menu, true);
                navigationView.getMenu().setGroupVisible(R.id.group_login, false);
                navigationView.getMenu().setGroupVisible(R.id.group_app_menu, true);
                return;
            }

            if(app.getUserRights().equals(UserRights.BASE_RIGHTS)){
                navigationView.getMenu().setGroupVisible(R.id.group_base_menu, true);
                navigationView.getMenu().setGroupVisible(R.id.group_full_menu, false);
                navigationView.getMenu().setGroupVisible(R.id.group_login, false);
                navigationView.getMenu().setGroupVisible(R.id.group_app_menu, true);
                return;
            }
            if(app.getUserRights().equals(UserRights.VIEW_RIGHTS)){
                navigationView.getMenu().setGroupVisible(R.id.group_base_menu, true);
                navigationView.getMenu().setGroupVisible(R.id.group_full_menu, false);
                navigationView.getMenu().setGroupVisible(R.id.group_login, false);
                navigationView.getMenu().setGroupVisible(R.id.group_app_menu, true);
                return;
            }

        }else{
            navigationView.getMenu().setGroupVisible(R.id.group_base_menu, false);
            navigationView.getMenu().setGroupVisible(R.id.group_full_menu, false);
            navigationView.getMenu().setGroupVisible(R.id.group_login, true);
            navigationView.getMenu().setGroupVisible(R.id.group_app_menu, true);
            return;
        }

    }

    public void onClickSignOut(){
        AlertDialog.Builder ad;
        Context context;
        context = NavigationDrawerActivity.this;
        ad = new AlertDialog.Builder(context);
        ad.setTitle("Sign Out");
        ad.setMessage("You want to sign out?");
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                srProjectApplication.deleteUser();
                setMenuForUserRights();
                setHeaderUser();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                return;
            }
        });

        ad.setCancelable(true);
        ad.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



    public void setMenuItemChecked(int id){
        for(int i = 0; i < navigationView.getMenu().size(); ++i) {
            if(navigationView.getMenu().getItem(i).getItemId()==id){
                navigationView.getMenu().getItem(i).setChecked(true);
            }else{
                navigationView.getMenu().getItem(i).setChecked(false);
            }
        }
    }


    private void setupNavigationDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                       // textView = (TextView) findViewById(R.id.textView);
                        Intent intent;
                        switch (menuItem.getItemId()) {

                            case R.id.item_navigation_drawer_inbox_user_tasks:
                                intent = new Intent(NavigationDrawerActivity.this, UserTaskActivity.class);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                startActivity(intent);
                                return true;


                            case R.id.item_navigation_drawer_projects:
                                //setMenuItemChecked(menuItem.getItemId());
                                intent = new Intent(NavigationDrawerActivity.this, MainActivity.class);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                return true;

           ///////create project
                            case R.id.item_navigation_drawer_create_project:
                                //setMenuItemChecked(menuItem.getItemId());
                                intent = new Intent(NavigationDrawerActivity.this, CreateProjectActivity.class);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                startActivity(intent);
                                return true;

           ///////login
                            case R.id.item_navigation_drawer_login:
                                //setMenuItemChecked(menuItem.getItemId());
                                intent = new Intent(NavigationDrawerActivity.this, LoginActivity.class);
                                drawerLayout.closeDrawer(GravityCompat.START);

                                startActivity(intent);
                                return true;

           //////create user
                            case R.id.item_navigation_drawer_create_user:
                                ///setMenuItemChecked(menuItem.getItemId());
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent intent1 = new Intent(NavigationDrawerActivity.this, RegistrationActivity.class);
                                startActivity(intent1);
                                return true;


             //////settings - SIGN OUT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            case R.id.item_navigation_drawer_settings:
                                //setMenuItemChecked(menuItem.getItemId());
                                drawerLayout.closeDrawer(GravityCompat.START);
                                onClickSignOut();
                                return true;
/*
                            case R.id.item_navigation_drawer_help_and_feedback:
                                menuItem.setChecked(true);
                               // Toast.makeText(MainActivity.this, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;*/
                        }
                        return true;
                    }
                });
    }
}