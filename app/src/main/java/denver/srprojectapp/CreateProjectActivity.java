package denver.srprojectapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CreateProjectActivity extends NavigationDrawerActivity {

    private Date datetime;
    Button buttonDateTimeCreate;
    EditText textSelectDateTimeCreate;
    SimpleDateFormat formatter;


    private List<GeneralUser> allGeneralUsers;

    boolean[] mCheckedItemsArr;
    List<boolean[]> allCheckedUsersOnTasksList;


    List<TaskWithUsersSetted> taskWithUsersSettedList;


    private LinearLayout myList;
    private MyAdapter myAdapter;

    private View mProgressSaveProjectView;
    private View mCreateProjectView;


    private EditText editTextEnterTitleCreate;
    private EditText editTextEnterDescriptionCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        srProjectApplication = ((SRProjectApplication) getApplicationContext());
        postCreate(savedInstanceState,R.layout.activity_create_project);
        /////////////

        taskWithUsersSettedList = new ArrayList<>();
       // taskUsersSetedList.add(new TaskUsersSeted());

        myList = (LinearLayout) findViewById(R.id.listViewTaskCreate);
       // myList.setItemsCanFocus(true);
        setAdapterForListView();
        allGeneralUsers = new ArrayList<>();

        editTextEnterTitleCreate = (EditText) findViewById(R.id.editTextTitleCreate);
        editTextEnterDescriptionCreate = (EditText) findViewById(R.id.editTextDescriptionCreate);

/////////test
/*
        allGeneralUsers.add(new GeneralUser(0,"00"));
        allGeneralUsers.add(new GeneralUser(1,"11"));
        allGeneralUsers.add(new GeneralUser(2,"22"));
        allGeneralUsers.add(new GeneralUser(3,"33"));
        allGeneralUsers.add(new GeneralUser(4,"44"));*/
        ///////////
        mProgressSaveProjectView = findViewById(R.id.save_project_progress);
        mCreateProjectView = findViewById(R.id.scrollViewCreateProject);

        datetime = new Date();
        buttonDateTimeCreate = (Button)findViewById(R.id.buttonDateTimeCreate);
        textSelectDateTimeCreate = (EditText)findViewById(R.id.textSelectDateTimeCreate);

        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        datetime = new Date();
        setDatetimeFromString("0001-01-01 00:00:00");


        if(srProjectApplication.isGeneralUserList() == true) {
            allGeneralUsers = srProjectApplication.getGeneralUserList();
        }
        //textSelectDateTimeCreate.setText(folderName);


        allCheckedUsersOnTasksList = new ArrayList<>();
        mCheckedItemsArr = new boolean[allGeneralUsers.size()];
        for(int i = 0; i < mCheckedItemsArr.length; i++){
            mCheckedItemsArr[i] = false;
        }
        allCheckedUsersOnTasksList.add(mCheckedItemsArr.clone());

    }

    private void setDatetimeFromString(String dt){
        try {
            this.datetime = formatter.parse(dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private String getDatetimeInString(){
        String str = formatter.format(datetime);
        return str;
    }
    private void setDatetimeOnView(){
        if (isSetDatetime() == true){
            textSelectDateTimeCreate.setText(getDatetimeInString());
            buttonDateTimeCreate.setText(getString(R.string.button_datetime_date_delete));
        }
        else{
            textSelectDateTimeCreate.setText("");
            buttonDateTimeCreate.setText(getString(R.string.button_datetime_date_add));
        }
    }
    private boolean isSetDatetime() {
        if (getDatetimeInString().equals("0001-01-01 00:00:00") == false) {
            return true;
        } else{
            return false;
        }
    }

    public void onClickButtonDateTimeCreate(View view){
        if(isSetDatetime() == false){
            makeDateTimeDialog();

        }else{
            setDatetimeFromString("0001-01-01 00:00:00");
            setDatetimeOnView();
        }

    }





    ///////////task list



    public void setAdapterForListView(){
        myAdapter = new MyAdapter(taskWithUsersSettedList);
        myList.removeAllViews();
        for (int i = 0; i < taskWithUsersSettedList.size(); i++) {
            View view = myAdapter.getView(i, null, myList);
            myList.addView(view);
        }

    }



    public void onClickDeleteTask(int position){
       //taskUsersSetedList.remove(number);

        taskWithUsersSettedList.remove(position);
        allCheckedUsersOnTasksList.remove(position);
        setAdapterForListView();
    }

    public void onClickButtonAddTask(View view){
        taskWithUsersSettedList.add(new TaskWithUsersSetted());
        allCheckedUsersOnTasksList.add(mCheckedItemsArr.clone());
        setAdapterForListView();

    }

    final List<GeneralUser> tmpForItemChosenGeneralUsers = new ArrayList<>();

    public void onClickItemAddUserForTask(final int position){


        final boolean[] mCheckedItems = allCheckedUsersOnTasksList.get(position).clone();

        String[] checkAllGeneralUsersName = new String[allGeneralUsers.size()];
        for(int i = 0; i < checkAllGeneralUsersName.length; i++){
            checkAllGeneralUsersName[i] = allGeneralUsers.get(i).getUserName();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(CreateProjectActivity.this);
        builder.setTitle("Set users")
                .setCancelable(false)

                .setMultiChoiceItems(checkAllGeneralUsersName, mCheckedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which, boolean isChecked) {
                                mCheckedItems[which] = isChecked;
                            }
                        })

                        // Добавляем кнопки
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                for (int i = 0; i < allGeneralUsers.size(); ++i) {
                                    if (mCheckedItems[i] == true) {
                                        tmpForItemChosenGeneralUsers.add(new GeneralUser(allGeneralUsers.get(i)));
                                    }
                                }
                                //allCheckedUsersOnTasksList.set(position, mCheckedItems);
                                allCheckedUsersOnTasksList.set(position, mCheckedItems.clone());
                                taskWithUsersSettedList.get(position).setListUsers(tmpForItemChosenGeneralUsers);
                            }
                        })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();

                            }
                        });
        builder.create();
        builder.show();

    }



////////////////////adapter and class for content
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        //public List<Task> tasks = new ArrayList();
        List<TaskWithUsersSetted> taskUsersList = new ArrayList<>();
        boolean isFocusOnItem = false;

        public MyAdapter( List<TaskWithUsersSetted> taskUsersList) {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.taskUsersList = taskUsersList;
            notifyDataSetChanged();
        }

        public List<TaskWithUsersSetted> getTaskUsersList(){return taskUsersList;}

        public int getCount() {
            return taskUsersList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final EditText editText;
            Button setUserTask;
            Button deleteThisTask;
            final boolean[] wasItemDeletedInListView = {false};

            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.item_task_list_create, null);

                editText = (EditText) convertView
                        .findViewById(R.id.editTextSetTaskTextOnItem);
                setUserTask = (Button) convertView.findViewById(R.id.buttonSetUsersTaskOnItem);
                deleteThisTask = (Button) convertView.findViewById(R.id.buttonDelUserTaskItem);
                convertView.setTag(editText);


                setUserTask.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        onClickItemAddUserForTask(position);

                    }
                });

                deleteThisTask.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        wasItemDeletedInListView[0] = true;
                       // editText.clearFocus();
                        editText.requestFocus();
                        onClickDeleteTask(position);


                    }

                });
            } else {
                editText = (EditText) convertView.getTag();
            }


            //Fill EditText with the value you have in data source
            editText.setText(taskUsersList.get(position).getTaskText());
            editText.setId(position);
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    final int position = v.getId();
                    final EditText caption = (EditText) v;

                    if (!hasFocus) {

                        if (wasItemDeletedInListView[0] == false) {

                            taskWithUsersSettedList.get(position).setTaskText(caption.getText().toString());

                        }
                    }
                }
            });
            //we need to update adapter once we finish with editing


            return convertView;
        }
    }








   ////////////////date time pickers

    private int year;
    private int monthOfYear;
    private int dayOfMonth;
    private int hourOfDay;
    private int minute;

    private final int DIALOG_DATE = 1;
    private final int DIALOG_TIME = 2;


    private void makeDateTimeDialog(){
        showDialog(DIALOG_DATE);
        showDialog(DIALOG_TIME);
    }

    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            Calendar calendar = Calendar.getInstance();
            int  mYear = calendar.get(Calendar.YEAR);
            int mMonth = calendar.get(Calendar.MONTH);
            int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBackDate, mYear, mMonth, mDay);
            return tpd;
        }
        if (id == DIALOG_TIME) {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog tpd;
            int  mMinute = calendar.get(Calendar.MINUTE);
            int mHour = calendar.get(Calendar.HOUR_OF_DAY);
            tpd = new TimePickerDialog(this, myCallBackTime, mHour, mMinute, true);
            return tpd;
        }
        return super.onCreateDialog(id);
    }


    DatePickerDialog.OnDateSetListener myCallBackDate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            savePickedDate(year, monthOfYear, dayOfMonth);
            datetime = new Date(year-1900, monthOfYear, dayOfMonth, hourOfDay, minute);
            setDatetimeOnView();
        }
    };

    TimePickerDialog.OnTimeSetListener myCallBackTime = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            savePickedTime(hourOfDay, minute);

        }
    };

    public void savePickedTime(int hourOfDay, int minute){
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    public void savePickedDate(int year, int monthOfYear, int dayOfMonth){
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
    }



    ///////////////////////////////////////
    /////////save project part
    ///////////////////////////////////////


    public void onClickFABSaveProject(View view){
        textSelectDateTimeCreate.requestFocus();
        textSelectDateTimeCreate.clearFocus();


        boolean cancel = false;

        for(int i = 0; i < taskWithUsersSettedList.size(); ++i){
            if(taskWithUsersSettedList.get(i).getTaskText().length() < 5) {
                cancel = true;
                View layout = myList.getChildAt(i);
                if (layout.getTag() != null) {
                    EditText editText = (EditText) layout.findViewById(i);
                    editText.setError("Text of task can not be shorter than 5 symbols");
                    editText.requestFocus();
                }
            }
        }

        if(editTextEnterDescriptionCreate.getText().length() < 15){
            editTextEnterDescriptionCreate.setError("Description can not be shorter than 15 symbols");
            editTextEnterDescriptionCreate.requestFocus();
            cancel = true;
        }

        if(editTextEnterTitleCreate.getText().length() < 10){
            editTextEnterTitleCreate.setError("Title can not be shorter than 10 symbols");
            editTextEnterTitleCreate.requestFocus();
            cancel = true;
        }


        if (!cancel) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            showProgress(true);
            SaveProjectTask mAuthTask = new SaveProjectTask(editTextEnterTitleCreate.getText().toString(),
                    editTextEnterDescriptionCreate.getText().toString(), getDatetimeInString(),  taskWithUsersSettedList);
            mAuthTask.execute((Void) null);
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



    public class SaveProjectTask extends AsyncTask<Void, Void, Boolean> {

        private final String mTitle;
        private final String mDescription;
        private final String mDatetime;
        private final List<TaskWithUsersSetted> mTaskWithUsersSettedList;

        ///
        private String mError;
        ///

        SaveProjectTask(String title, String description, String datetime, List<TaskWithUsersSetted> taskUsersSetedList) {
            mTitle = title;
            mDescription = description;
            mDatetime =  datetime;
            mTaskWithUsersSettedList = taskUsersSetedList;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            SRProjectApplication srProjectApplication = (SRProjectApplication)getApplicationContext();
            ServiceServerHandler sh = new ServiceServerHandler();

            // Making a request to url and getting response
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("title", mTitle));
            pairs.add(new BasicNameValuePair("description", mDescription));
            pairs.add(new BasicNameValuePair("datetime", mTitle));
            /*
            JSONArray arr = new JSONArray();
            arr.put(new BasicNameValuePair("id","111"));
            arr.put(new BasicNameValuePair("id", "222"));
            String str = arr.toString();*/

            String jsonStr = sh.makeServiceCall(UrlHolder.getProjectUrl(), ServiceServerHandler.POST, pairs, srProjectApplication.getUser().getUserApiKey() );

            if (jsonStr != null) {

                JSONObject jsonObj = null;
                String error = "";
                try {
                    jsonObj = new JSONObject(jsonStr);
                    error = jsonObj.getString(ServiceServerHandler.TAG_ERROR);

                    if (error == "false") {
                        int project_id = Integer.parseInt(jsonObj.getString("project_id"));

                        for(int i = 0; i < mTaskWithUsersSettedList.size(); ++i) {
                            if (mTaskWithUsersSettedList.get(i).getListUsers().size() > 0) {
                                List<NameValuePair> pairsTasks = new ArrayList<NameValuePair>();
                                pairsTasks.add(new BasicNameValuePair("text", mTaskWithUsersSettedList.get(i).getTaskText()));
                                pairsTasks.add(new BasicNameValuePair("project_id", Integer.toString(project_id)));
                                JSONArray arr = new JSONArray();
                                for (int j = 0; j < mTaskWithUsersSettedList.get(i).getListUsers().size(); ++j){
                                    arr.put(new BasicNameValuePair("id", Integer.toString(mTaskWithUsersSettedList.get(i).getListUsers().get(j).getUserId())));
                                }
                                pairsTasks.add(new BasicNameValuePair("usersArr", arr.toString()));
                                jsonStr = null;
                                jsonStr = sh.makeServiceCall(UrlHolder.getTasksWithUserTaskUrl(), ServiceServerHandler.POST, pairsTasks, srProjectApplication.getUser().getUserApiKey() );
                                if(jsonStr != null){
                                    jsonObj = null;
                                    error = "";
                                    jsonObj = new JSONObject(jsonStr);
                                    error = jsonObj.getString(ServiceServerHandler.TAG_ERROR);

                                    if (error == "false") {
                                        int id = Integer.parseInt(jsonObj.getString("id"));
                                        mTaskWithUsersSettedList.get(i).setIdTask(id);

                                    } else {
                                        mError = jsonObj.getString("message");
                                    }
                                }
                            }else{
                                List<NameValuePair> pairsTasks = new ArrayList<NameValuePair>();
                                pairsTasks.add(new BasicNameValuePair("text", mTaskWithUsersSettedList.get(i).getTaskText()));
                                pairsTasks.add(new BasicNameValuePair("project_id", Integer.toString(project_id)));
                                jsonStr = null;
                                jsonStr = sh.makeServiceCall(UrlHolder.getTasksUrl(), ServiceServerHandler.POST, pairsTasks, srProjectApplication.getUser().getUserApiKey() );
                                if(jsonStr != null){
                                    jsonObj = null;
                                    error = "";
                                    jsonObj = new JSONObject(jsonStr);
                                    error = jsonObj.getString(ServiceServerHandler.TAG_ERROR);

                                    if (error == "false") {
                                        int id = Integer.parseInt(jsonObj.getString("id"));
                                        mTaskWithUsersSettedList.get(i).setIdTask(id);

                                    } else {
                                        mError = jsonObj.getString("message");
                                    }
                                }
                            }

                        }
                        ////save project to application
                        srProjectApplication.getProjectList().add(new Project(project_id, mTitle, 0 , mDescription, mDatetime,
                                srProjectApplication.getUser().getUserId(), mTaskWithUsersSettedList ));
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

                return false;

            }
            return false;
        }


        @Override
        protected void onPostExecute ( final Boolean success){

            showProgress(false);

            if (success) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Successes added!", Toast.LENGTH_SHORT);
                toast.show();
                Intent newIntent = new Intent(CreateProjectActivity.this, MainActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
            } else {
                // mPasswordView.setError(getString(R.string.error_incorrect_password));
                Toast toast = Toast.makeText(getApplicationContext(),
                        mError, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }





}
