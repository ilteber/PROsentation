package prosentation.example.com.prosentation.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import prosentation.example.com.prosentation.DynamoDB.DynamoDBManager;
import prosentation.example.com.prosentation.R;

public class AccountActivity extends AppCompatActivity {

    private static DynamoDBManager managerClass = DynamoDBManager.getInstance();

    private String oldUsername;
    private String oldEmail;
    private String oldPassword;

    private String newUsername;
    private String newEmail;
    private String newPassworD;

    private TextView text_username;
    private TextView text_password;
    private TextView text_email;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        oldUsername = getIntent().getStringExtra("USERNAME");
        oldPassword = getIntent().getStringExtra("PASSWORD");
        oldEmail = getIntent().getStringExtra("EMAIL");

        text_username = (TextView)findViewById(R.id.input_username);
        text_password = (TextView)findViewById(R.id.input_password);
        text_email = (TextView)findViewById(R.id.input_email);
        text_email.setFocusable(false);

        text_username.setText(oldUsername);
        text_password.setText(oldPassword);
        text_email.setText(oldEmail);

    }

    class ChangesSaver extends AsyncTask<String, Void, Void>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(AccountActivity.this);
            progressDialog.setMessage("Changes are being saved...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            managerClass.changeCredentials(AccountActivity.this, params[0], params[1], params[2], params[3]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

    public void saveChanges(View view){
        newUsername = text_username.getText().toString();
        newPassworD = text_password.getText().toString();
        newEmail = text_email.getText().toString();

        new ChangesSaver().execute(oldUsername, newUsername, newPassworD, newEmail);
        Log.d("Changes are saved", "Changes are saved");
        Toast.makeText(this, "Your changes has been saved", Toast.LENGTH_SHORT).show();
    }
}
