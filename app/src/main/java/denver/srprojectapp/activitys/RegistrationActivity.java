package denver.srprojectapp.activitys;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import denver.srprojectapp.service.InternetConnectionChecker;
import denver.srprojectapp.R;
import denver.srprojectapp.service.SRProjectApplication;
import denver.srprojectapp.service.ServiceServerHandler;
import denver.srprojectapp.service.UrlHolder;
import denver.srprojectapp.objects.User;
import denver.srprojectapp.service.UserRights;


public class RegistrationActivity extends NavigationDrawerActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private EditText mNameView;
    private Spinner mSpinnerUserRights;

    SRProjectApplication srProjectApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        postCreate(savedInstanceState,R.layout.activity_registration);

        srProjectApplication = (SRProjectApplication) getApplicationContext();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.registration_email);
        populateAutoComplete();

        mNameView = (EditText) findViewById(R.id.registration_name);

        mPasswordView = (EditText) findViewById(R.id.registration_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mEmailSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.registration_form);
        mProgressView = findViewById(R.id.registration_progress);

        String[] dataSpinnerRights = {UserRights.BASE_RIGHTS, UserRights.FULL_RIGHTS, UserRights.VIEW_RIGHTS};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dataSpinnerRights);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinnerUserRights = (Spinner) findViewById(R.id.spinnerSetUserRights);
        mSpinnerUserRights.setAdapter(adapter);
        mSpinnerUserRights.setPrompt(getString(R.string.spinner_title));
        mSpinnerUserRights.setSelection(0);

        if (srProjectApplication.checkIsLoginedUser() == false){
            mSpinnerUserRights.setVisibility(View.GONE);
        }

        mSpinnerUserRights.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mNameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();

        String rights;
        if (srProjectApplication.checkIsLoginedUser() == false){
            rights = UserRights.BASE_RIGHTS;
        }else {
            rights = mSpinnerUserRights.getSelectedItem().toString();
        }

        boolean cancel = false;
        View focusView = null;


        if (!TextUtils.isEmpty(name) && !isNameValid(name)) {
            mNameView.setError(getString(R.string.error_invalid_name));
            focusView = mNameView;
            cancel = true;
        }

        if (!TextUtils.isEmpty(rights) && !UserRights.isValidStrRights(rights) ) {
            Toast.makeText(getBaseContext(), getString(R.string.error_invalid_user_rights) , Toast.LENGTH_LONG).show();
            focusView = mSpinnerUserRights;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if(InternetConnectionChecker.isNetworkConnected(RegistrationActivity.this)) {
                showProgress(true);
                mAuthTask = new UserLoginTask(email, password, name ,rights);
                mAuthTask.execute((Void) null);
            }else{
                Toast toast = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.no_internet_connection_error), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isNameValid(String name) {
        //TODO: Replace this with your own logic
        return name.length() > 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(RegistrationActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }






    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */




    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mName;
        private final String mRights;
        private String mError;
        private String name;

        UserLoginTask(String email, String password,String name, String rights) {
            mEmail = email;
            mPassword = password;
            mName = name;
            mRights = rights;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            ServiceServerHandler sh = new ServiceServerHandler();

            // Making a request to url and getting response
            List<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("name", mName));
            pairs.add(new BasicNameValuePair("email", mEmail));
            pairs.add(new BasicNameValuePair("password", mPassword));
            pairs.add(new BasicNameValuePair("rights", mRights));
            String jsonStr = sh.makeServiceCall(UrlHolder.getRegistrationUrl(), ServiceServerHandler.POST, pairs);

            if (jsonStr != null) {

                JSONObject jsonObj = null;
                String error = "";
                try {
                    jsonObj = new JSONObject(jsonStr);
                    error = jsonObj.getString(ServiceServerHandler.TAG_ERROR);


                    if (error == "false") {
                        ServiceServerHandler shLogin = new ServiceServerHandler();

                        // Making a request to url and getting response
                        pairs = new ArrayList<NameValuePair>();
                        pairs.add(new BasicNameValuePair("email", mEmail));
                        pairs.add(new BasicNameValuePair("password", mPassword));
                        jsonStr = shLogin.makeServiceCall(UrlHolder.getLoginUrl(), ServiceServerHandler.POST, pairs);

                        if (jsonStr != null) {

                            jsonObj = null;
                            error = "";
                            try {
                                jsonObj = new JSONObject(jsonStr);
                                error = jsonObj.getString(ServiceServerHandler.TAG_ERROR);

                                if (error == "false") {
                                    SRProjectApplication srApp = ((SRProjectApplication) getApplicationContext());
                                    name = jsonObj.getString("name");
                                    String email = jsonObj.getString("email");
                                    String apiKey = jsonObj.getString("apiKey");
                                    String rights = jsonObj.getString("rights");
                                    int id = Integer.parseInt(jsonObj.getString("id"));
                                    srApp.setUser(new User(id, email, apiKey, name, rights));
                                    srApp.saveUser();
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


                    } else {
                        mError = jsonObj.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return false;

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent newIntent = new Intent(RegistrationActivity.this,MainActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
            } else {
                /////////////make errors
                mEmailView.setError(mError);
                mEmailView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

