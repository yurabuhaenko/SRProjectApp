package denver.srprojectapp.activitys;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import denver.srprojectapp.objects.GeneralUser;
import denver.srprojectapp.service.InternetConnectionChecker;
import denver.srprojectapp.objects.Project;
import denver.srprojectapp.R;
import denver.srprojectapp.service.SRProjectApplication;
import denver.srprojectapp.service.ServiceServerHandler;
import denver.srprojectapp.objects.TaskWithUsersSetted;
import denver.srprojectapp.service.UrlHolder;


public class EditProjectActivity extends NavigationDrawerActivity {

    private Date datetime;
    private List<GeneralUser> allGeneralUsers;
    List<TaskWithUsersSetted> taskWithUsersSettedList;




    Button buttonDateTimeEdit;
    EditText textSelectDateTimeEdit;
    SimpleDateFormat formatter;


    boolean[] mCheckedItemsArr;
    List<boolean[]> allCheckedUsersOnTasksList;


    private LinearLayout myList;
    private MyAdapter myAdapter;

    private View mProgressSaveEditedProjectView;
    private View mEditProjectView;
    private Project thisEditebleProject;

    private EditText editTextEnterTitleEdit;
    private EditText editTextEnterDescriptionEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        srProjectApplication = ((SRProjectApplication) getApplicationContext());
        postCreate(savedInstanceState,R.layout.activity_edit_project);
        /////////////
        int project_id = getIntent().getIntExtra("project_id", 0 );



        myList = (LinearLayout) findViewById(R.id.listViewTaskEdit);
        // myList.setItemsCanFocus(true);
        editTextEnterTitleEdit = (EditText) findViewById(R.id.editTextTitleEdit);
        editTextEnterDescriptionEdit = (EditText) findViewById(R.id.editTextDescriptionEdit);
        datetime = new Date();
        buttonDateTimeEdit = (Button)findViewById(R.id.buttonDateTimeEdit);
        textSelectDateTimeEdit = (EditText)findViewById(R.id.textSelectDateTimeEdit);
        mProgressSaveEditedProjectView = findViewById(R.id.save_project_progress_on_edit_project);
        mEditProjectView = findViewById(R.id.scrollViewEditProject);
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);

        thisEditebleProject = srProjectApplication.getProjectById(project_id);

        editTextEnterTitleEdit.setText(thisEditebleProject.getTitle());
        editTextEnterDescriptionEdit.setText(thisEditebleProject.getDescription());
        datetime = new Date();
        datetime = thisEditebleProject.getDatetimeInDate();
        setDatetimeOnView();
        
        taskWithUsersSettedList = new ArrayList<>();
        if(thisEditebleProject.getNumberOfTasks() != 0) {
            taskWithUsersSettedList = thisEditebleProject.getTaskWithUsersSettedList();
        }

        setAdapterForListView();
        allGeneralUsers = new ArrayList<>();



        if(srProjectApplication.isGeneralUserList() == true) {
            allGeneralUsers = srProjectApplication.getGeneralUserList();
        }
        //textSelectDateTimeCreate.setText(folderName);


        allCheckedUsersOnTasksList = new ArrayList<>();

        for(int j = 0; j < thisEditebleProject.getTaskWithUsersSettedList().size(); ++j) {

            mCheckedItemsArr = new boolean[allGeneralUsers.size()];

            for (int i = 0; i < mCheckedItemsArr.length; i++) {
                boolean flagIsChecked = false;
                for(int k = 0; k < thisEditebleProject.getTaskWithUsersSettedList().get(j).getListUsers().size(); ++k) {
                    if (allGeneralUsers.get(i).getUserId() == thisEditebleProject.getTaskWithUsersSettedList().get(j).getListUsers().get(k).getUserId()){
                        mCheckedItemsArr[i] = true;
                        flagIsChecked = true;
                        break;
                    }
                }
                if(flagIsChecked == false) {
                    mCheckedItemsArr[i] = false;
                }
            }
            allCheckedUsersOnTasksList.add(mCheckedItemsArr.clone());
            mCheckedItemsArr = new boolean[allGeneralUsers.size()];
            for(int i = 0; i < mCheckedItemsArr.length; i++){
                mCheckedItemsArr[i] = false;
            }
        }

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
            textSelectDateTimeEdit.setText(getDatetimeInString());
            buttonDateTimeEdit.setText(getString(R.string.button_datetime_date_delete));
        }
        else{
            textSelectDateTimeEdit.setText("");
            buttonDateTimeEdit.setText(getString(R.string.button_datetime_date_add));
        }
    }
    private boolean isSetDatetime() {
        if (getDatetimeInString().equals("0001-01-01 00:00:00") == false) {
            return true;
        } else{
            return false;
        }
    }

    public void onClickButtonDateTimeEdit(View view){
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
        if(taskWithUsersSettedList.get(position).getTask().getId() != 0){
            if(InternetConnectionChecker.isNetworkConnected(EditProjectActivity.this)){
                DeleteExistingProjectTask mAuthTask = new DeleteExistingProjectTask(taskWithUsersSettedList.get(position));
                mAuthTask.execute((Void) null);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.no_internet_connection_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        taskWithUsersSettedList.remove(position);
        allCheckedUsersOnTasksList.remove(position);
        setAdapterForListView();
    }

    public void onClickButtonAddTaskEdit(View view){
        taskWithUsersSettedList.add(new TaskWithUsersSetted());
        allCheckedUsersOnTasksList.add(mCheckedItemsArr.clone());
        setAdapterForListView();

    }






    public void onClickItemAddUserForTask(final int position){

        final List<GeneralUser> tmpForItemChosenGeneralUsers = new ArrayList<>();

        final boolean[] mCheckedItems = allCheckedUsersOnTasksList.get(position).clone();

        String[] checkAllGeneralUsersName = new String[allGeneralUsers.size()];
        for(int i = 0; i < checkAllGeneralUsersName.length; i++){
            checkAllGeneralUsersName[i] = allGeneralUsers.get(i).getUserName();
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(EditProjectActivity.this);
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


    public void onClickFABSaveEditedProject(View view){
        textSelectDateTimeEdit.requestFocus();
        textSelectDateTimeEdit.clearFocus();


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

        if(editTextEnterDescriptionEdit.getText().length() < 15){
            editTextEnterDescriptionEdit.setError("Description can not be shorter than 15 symbols");
            editTextEnterDescriptionEdit.requestFocus();
            cancel = true;
        }

        if(editTextEnterTitleEdit.getText().length() < 10){
            editTextEnterTitleEdit.setError("Title can not be shorter than 10 symbols");
            editTextEnterTitleEdit.requestFocus();
            cancel = true;
        }


        if (!cancel) {

            if(InternetConnectionChecker.isNetworkConnected(EditProjectActivity.this)) {
                showProgress(true);
                SaveProjectTask mAuthTask = new SaveProjectTask(editTextEnterTitleEdit.getText().toString(),
                        editTextEnterDescriptionEdit.getText().toString(), getDatetimeInString(), 0, taskWithUsersSettedList);
                mAuthTask.execute((Void) null);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.no_internet_connection_error), Toast.LENGTH_SHORT);
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

            mEditProjectView.setVisibility(show ? View.GONE : View.VISIBLE);
            mEditProjectView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mEditProjectView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressSaveEditedProjectView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressSaveEditedProjectView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressSaveEditedProjectView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressSaveEditedProjectView.setVisibility(show ? View.VISIBLE : View.GONE);
            mEditProjectView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }



    public class SaveProjectTask extends AsyncTask<Void, Void, Boolean> {

        private final String mTitle;
        private final String mDescription;
        private final String mDatetime;
        private final int mStatus;
        private final List<TaskWithUsersSetted> mTaskWithUsersSettedList;

        ///
        private String mError;
        ///

        SaveProjectTask(String title, String description, String datetime,int status,List<TaskWithUsersSetted> taskUsersSetedList) {
            mTitle = title;
            mDescription = description;
            mDatetime =  datetime;
            mStatus = status;
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
            pairs.add(new BasicNameValuePair("status", Integer.toString(mStatus)));
            pairs.add(new BasicNameValuePair("description", mDescription));
            pairs.add(new BasicNameValuePair("datetime", mDatetime));


            String jsonStr = sh.makeServiceCall(UrlHolder.getProjectUrlById(thisEditebleProject.getId()), ServiceServerHandler.PUT, pairs, srProjectApplication.getUser().getUserApiKey() );

            if (jsonStr != null) {

                JSONObject jsonObj = null;
                String error = "";
                try {
                    jsonObj = new JSONObject(jsonStr);
                    error = jsonObj.getString(ServiceServerHandler.TAG_ERROR);


                        //boolean[] isTaskFromExisting = new boolean[mTaskWithUsersSettedList.size()];
                        //isTaskFromExisting = srProjectApplication.compareTaskWithUserSettedWithExisting(mTaskWithUsersSettedList,thisEditebleProject.getId());

                        for(int i = 0; i < mTaskWithUsersSettedList.size(); ++i) {
                            if(mTaskWithUsersSettedList.get(i).getTask().getId() != 0) { ///// update task
                                if(mTaskWithUsersSettedList.get(i).getListUsers().size() > 0) {
                                    updateTaskOnServer( UrlHolder.getTasksWithUserTaskUrl() ,
                                            mTaskWithUsersSettedList.get(i));
                                }else{
                                    updateTaskOnServer( UrlHolder.getTasksUrl(), mTaskWithUsersSettedList.get(i));
                                }
                            }else{    ///////////////////create task
                                if(mTaskWithUsersSettedList.get(i).getListUsers().size() > 0) {
                                    createNewTaskOnServer(UrlHolder.getTasksWithUserTaskUrl(), mTaskWithUsersSettedList.get(i));
                                }else{
                                    createNewTaskOnServer(UrlHolder.getTasksUrl(), mTaskWithUsersSettedList.get(i));
                                }
                            }
                        }
                        ////save project to application
                        srProjectApplication.rewriteProjectById(new Project(thisEditebleProject.getId(), mTitle, mStatus , mDescription, mDatetime,
                                thisEditebleProject.getCreatedByID(), mTaskWithUsersSettedList ));

                        return true;


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
            return false;
        }

        private void createNewTaskOnServer(String url, TaskWithUsersSetted mTaskWithUsersSetted){
            try {
            List<NameValuePair> pairsTasks = new ArrayList<NameValuePair>();
            ServiceServerHandler sh = new ServiceServerHandler();
            pairsTasks.add(new BasicNameValuePair("text", mTaskWithUsersSetted.getTaskText()));
            pairsTasks.add(new BasicNameValuePair("project_id", Integer.toString(thisEditebleProject.getId())));

            if(mTaskWithUsersSetted.getListUsers().size() > 0){
                JSONArray arr = new JSONArray();
                for (int j = 0; j < mTaskWithUsersSetted.getListUsers().size(); ++j){
                    JSONObject objectUs = new JSONObject();
                    objectUs.put("id",Integer.toString(mTaskWithUsersSetted.getListUsers().get(j).getUserId()));
                    arr.put(objectUs);
                }
                JSONObject object = new JSONObject();
                object.put("arrUsersId",arr);

                pairsTasks.add(new BasicNameValuePair("users", object.toString()));
            }

            String jsonStr = null;
            jsonStr = sh.makeServiceCall(url, ServiceServerHandler.POST
                    , pairsTasks, srProjectApplication.getUser().getUserApiKey() );

            if(jsonStr != null){
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String error = null;

                    error = jsonObj.getString(ServiceServerHandler.TAG_ERROR);


                    if (error == "true") {
                        mError = jsonObj.getString("message");
                    }else{
                        int taskId = Integer.parseInt(jsonObj.getString("task_id"));
                        mTaskWithUsersSetted.setIdTask(taskId);
                    }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        }

        private void updateTaskOnServer(String url, TaskWithUsersSetted mTaskWithUsersSetted){
            try {
                List<NameValuePair> pairsTasks = new ArrayList<NameValuePair>();
                ServiceServerHandler sh = new ServiceServerHandler();
                pairsTasks.add(new BasicNameValuePair("text", mTaskWithUsersSetted.getTaskText()));
                pairsTasks.add(new BasicNameValuePair("status", Integer.toString(mStatus)));
                pairsTasks.add(new BasicNameValuePair("task_id", Integer.toString(mTaskWithUsersSetted.getTask().getId())));

                if(mTaskWithUsersSetted.getListUsers().size() > 0){
                    JSONArray arr = new JSONArray();
                    for (int j = 0; j < mTaskWithUsersSetted.getListUsers().size(); ++j){
                        JSONObject objectUs = new JSONObject();
                        objectUs.put("id",Integer.toString(mTaskWithUsersSetted.getListUsers().get(j).getUserId()));
                        arr.put(objectUs);
                    }
                    JSONObject object = new JSONObject();
                    object.put("arrUsersId",arr);

                    pairsTasks.add(new BasicNameValuePair("users", object.toString()));
                }

                String jsonStr = null;
                jsonStr = sh.makeServiceCall(url,ServiceServerHandler.PUT
                        , pairsTasks, srProjectApplication.getUser().getUserApiKey() );

                if(jsonStr != null){
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String error = null;

                    error = jsonObj.getString(ServiceServerHandler.TAG_ERROR);


                    if (error == "True") {
                        mError = jsonObj.getString("message");

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        @Override
        protected void onPostExecute ( final Boolean success){

            showProgress(false);

            if (success) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Successes edited!", Toast.LENGTH_SHORT);
                toast.show();
                Intent newIntent = new Intent(EditProjectActivity.this, MainActivity.class);
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

    public class DeleteExistingProjectTask extends AsyncTask<Void, Void, Boolean> {

        private final TaskWithUsersSetted mTaskWithUsersSetted;

        ///
        private String mError;
        ///

        DeleteExistingProjectTask(TaskWithUsersSetted taskWithUsersSetted) {
            mTaskWithUsersSetted = taskWithUsersSetted;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            SRProjectApplication srProjectApplication = (SRProjectApplication)getApplicationContext();
            ServiceServerHandler sh = new ServiceServerHandler();

            String jsonStr = sh.makeServiceCall(UrlHolder.getTasksUrlById(mTaskWithUsersSetted.getTask().getId()

            ), ServiceServerHandler.DELETE, null, srProjectApplication.getUser().getUserApiKey() );

            if (jsonStr != null) {

                JSONObject jsonObj = null;
                String error = "";
                try {
                    jsonObj = new JSONObject(jsonStr);
                    error = jsonObj.getString(ServiceServerHandler.TAG_ERROR);


                    return true;


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }


        @Override
        protected void onPostExecute ( final Boolean success){




        }
    }



}