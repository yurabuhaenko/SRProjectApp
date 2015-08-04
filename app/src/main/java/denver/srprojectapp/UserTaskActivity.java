package denver.srprojectapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UserTaskActivity extends NavigationDrawerActivity {


    private View mProgressSaveProjectView;
    private View mCreateProjectView;

    private LinearLayout myList;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_task);
        srProjectApplication = ((SRProjectApplication) getApplicationContext());
        postCreate(savedInstanceState,R.layout.activity_user_task);


        mProgressSaveProjectView = findViewById(R.id.change_task_status_progress);
        mCreateProjectView = findViewById(R.id.scrollViewActivityUserTasks);

////////////////////
        myList = (LinearLayout) findViewById(R.id.linearLayoutShowUserTasks);
        setAdapterForListView();

    }


///////////////////////////////////////////////////////////////////
//////////// ADAPTER FOR USER TASKS////////////////////////////////
    //////////////////////////////////////////////////////////////
//String utf8String= new String(cp1251String.getBytes("Cp1251"), "UTF-8");

    public void setAdapterForListView(){
        if(srProjectApplication.isUserTaskList()) {
            myAdapter = new MyAdapter(srProjectApplication.getUserTasks());
        }else{
            myAdapter = new MyAdapter(new ArrayList<ProjectTask>());
        }
        myList.removeAllViews();
        for (int i = 0; i < srProjectApplication.getUserTasks().size(); i++) {
            View view = myAdapter.getView(i, null, myList);
            myList.addView(view);
        }
    }


    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        List<ProjectTask> userTasks;

        public MyAdapter(List<ProjectTask> userTasks) {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            notifyDataSetChanged();
        }

        public List<ProjectTask> getUserTasks() {
            return userTasks;
        }

        public int getCount() {
            return userTasks.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final TextView textViewTaskText;
            final TextView textViewProjectTitle;
            CheckBox checkBoxTaskStatus;

            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.item_user_task_show, null);

                textViewTaskText = (TextView) convertView.findViewById(R.id.textViewTaskTextOnItem);
                textViewProjectTitle = (TextView) convertView.findViewById(R.id.textViewProjectTitleTextOnItem);
                checkBoxTaskStatus = (CheckBox) convertView.findViewById(R.id.checkBoxItemUserTaskShow);

                convertView.setTag(textViewTaskText);

                textViewTaskText.setText(userTasks.get(position).getText());
                textViewProjectTitle.setText(userTasks.get(position).getProjectTitle());
                checkBoxTaskStatus.setChecked(userTasks.get(position).getStatus() == 1);

//////////////////////////////////////////////////////////////////////
/////////////////////////on checkbox click///////////////////////////
//////////////////////////////////////////////////////////////////////
                checkBoxTaskStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            onCheckBoxClick(1, position);
                        }else{
                            onCheckBoxClick(0, position);
                        }
                    }
                });
            }
            return convertView;
        }

        private void onCheckBoxClick(int clickValue, int position){
            if(InternetConnectionChecker.isNetworkConnected(UserTaskActivity.this)) {
                showProgress(true);
                ChangeStatusOfTask changeStatusOfTask = new ChangeStatusOfTask(clickValue, userTasks.get(position).getId());
                changeStatusOfTask.execute((Void) null);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.no_internet_connection_error)+" "+
                                getResources().getString(R.string.unable_to_save_changes_on_server_error), Toast.LENGTH_SHORT);
                toast.show();
                if(!DelayedSendingToServerIntentService.isInstanceCreated()) {
                    startService(new Intent(UserTaskActivity.this, DelayedSendingToServerIntentService.class));
                }
            }
            srProjectApplication.getUserTasks().get(position).setStatus(clickValue);
            srProjectApplication.saveUserProjectTasks(srProjectApplication.getUserTasks());
        }

    }




    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCreateProjectView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCreateProjectView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCreateProjectView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressSaveProjectView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressSaveProjectView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressSaveProjectView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressSaveProjectView.setVisibility(show ? View.VISIBLE : View.GONE);
            mCreateProjectView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class ChangeStatusOfTask extends AsyncTask<Void, Void, Boolean> {

        private final int mStatus;
        private final int mTaskId;
        ///
        private String mError;
        ///

        ChangeStatusOfTask(int mStatus, int mTaskId) {
            this.mTaskId = mTaskId;
            this.mStatus = mStatus;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            SRProjectApplication srProjectApplication = (SRProjectApplication)getApplicationContext();
            ServiceServerHandler sh = new ServiceServerHandler();

            // Making a request to url and getting response
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();

            pairs.add(new BasicNameValuePair("status", Integer.toString(mStatus)));


            String jsonStr = sh.makeServiceCall(UrlHolder.getTasksStatusByIdUrl(mTaskId), ServiceServerHandler.PUT, pairs, srProjectApplication.getUser().getUserApiKey() );

            if (jsonStr != null) {
                JSONObject jsonObj = null;
                String error = "";
                try {
                    jsonObj = new JSONObject(jsonStr);
                    error = jsonObj.getString(ServiceServerHandler.TAG_ERROR);

                    if (error == "false") {
                        return true;
                    } else {
                        mError = jsonObj.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
            return false;
        }


        @Override
        protected void onPostExecute ( final Boolean success){

            showProgress(false);

            if (success) {

            } else {
                // mPasswordView.setError(getString(R.string.error_incorrect_password));
                Toast toast = Toast.makeText(getApplicationContext(),
                        mError, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }




}
