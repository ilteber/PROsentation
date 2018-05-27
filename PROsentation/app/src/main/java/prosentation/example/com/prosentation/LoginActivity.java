package prosentation.example.com.prosentation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.CheckBox;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import prosentation.example.com.prosentation.Activities.VideoTakeActivity;
import prosentation.example.com.prosentation.DynamoDB.DynamoDBManager;
import prosentation.example.com.prosentation.Entity.User;

/**
 * Created by lenovo on 6.2.2018.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static DynamoDBManager managerClass = DynamoDBManager.getInstance();
    boolean success;
    SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    String password, username, email;

    @BindView(R.id.input_username)
    EditText _usernameText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;
    @BindView(R.id.remember_me) CheckBox _rememberme;
    RelativeLayout rellay1, rellay2;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);

        handler.postDelayed(runnable, 0000);

        ButterKnife.bind(this);
        prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        prefsEditor = prefs.edit();
        if(prefs.getBoolean("remember",false)){
            username = prefs.getString("username",null);
            password = prefs.getString("password",null);
            _rememberme.setChecked(true);
            onLoginSuccess();
        }else {
            _rememberme.setChecked(false);
            _loginButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    login();
                }
            });
        }

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    class LoginToSystem extends AsyncTask<String, Void, Void> {
        //        private ProgressDialog dialog;

        @Override
        protected Void doInBackground(String... params) {
            Log.d("DB operations", "DB operations");

            success = managerClass.loginToSystem(LoginActivity.this, params[0], params[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(!success){
                onLoginFailed();
            }
        }
    }


    class getMyMail extends AsyncTask<String, Void, Void> {
        //        private ProgressDialog dialog;
        User user;
        public getMyMail(User user){
            this.user = user;
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.d("DB operations", "DB operations");

            success = managerClass.getMyEmail(LoginActivity.this, params[0], params[1], user);
            return null;
        }

    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        username = _usernameText.getText().toString();
        password = _passwordText.getText().toString();
        boolean remember = _rememberme.isChecked();
        if(remember){
            prefsEditor.putBoolean("remember", true);
            prefsEditor.putString("username", username);
            prefsEditor.putString("password", password);
            prefsEditor.commit();
        }else {
            prefsEditor.clear();
            prefsEditor.commit();
        }

        // TODO: Implement your own authentication logic here.
        new LoginToSystem().execute(username, password);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(success) {
                            // On complete call either onLoginSuccess or onLoginFailed
                            onLoginSuccess();
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the VideoTakeActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        prefs.edit().putString("username", username).commit();
        finish();


        User user = new User();
        try {
            new getMyMail(user).execute(username, password).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, VideoTakeActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("PASSWORD", password);
        intent.putExtra("EMAIL", user.getEmail());
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError("enter a valid username address");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
