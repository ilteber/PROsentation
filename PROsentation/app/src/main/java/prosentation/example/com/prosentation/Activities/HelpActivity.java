package prosentation.example.com.prosentation.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import prosentation.example.com.prosentation.R;

public class HelpActivity extends AppCompatActivity {

    TextView txt1, txt2, txt3, txt4, txt5;

    ExpandableTextView expandableTextView, expandableTextView2, expandableTextView3, expandableTextView4, expandableTextView5;
    String longText = "\n" +
            "Help Page could be reached from every page of the application by pressing the Help button at the top of the current page. It will include information about what is PROsentation, why users should use it, how to use the application and how to be a good presenter. There will also be a ‘Give us feedback’ and ‘About us’ section on Help Page. ";

    private String username;
    private String email;
    private String password;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        toolbar = (Toolbar) findViewById(R.id.toolbar);setSupportActionBar(toolbar);

        username = getIntent().getStringExtra("USERNAME");
        email = getIntent().getStringExtra("EMAIL");
        password = getIntent().getStringExtra("PASSWORD");

        expandableTextView = (ExpandableTextView)findViewById(R.id.expandable_text_view);
        expandableTextView.setText(longText);

        expandableTextView2 = (ExpandableTextView)findViewById(R.id.expandable_text_view2);
        expandableTextView2.setText(longText);

        expandableTextView3 = (ExpandableTextView)findViewById(R.id.expandable_text_view3);
        expandableTextView3.setText(longText);

        expandableTextView4 = (ExpandableTextView)findViewById(R.id.expandable_text_view4);
        expandableTextView4.setText(longText);

        expandableTextView5 = (ExpandableTextView)findViewById(R.id.expandable_text_view5);
        expandableTextView5.setText(longText);


        txt1 = (TextView)findViewById(R.id.text_id);
        txt1.setBackgroundColor(Color.parseColor("#F50057"));

        txt2 = (TextView)findViewById(R.id.text_id2);
        txt2.setBackgroundColor(Color.parseColor("#BA68C8"));

        txt3 = (TextView)findViewById(R.id.text_id3);
        txt3.setBackgroundColor(Color.parseColor("#9C27B0"));

        txt4 = (TextView)findViewById(R.id.text_id4);
        txt4.setBackgroundColor(Color.parseColor("#6A1B9A"));

        txt5 = (TextView)findViewById(R.id.text_id5);
        txt5.setBackgroundColor(Color.parseColor("#7c4dff"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            Toast.makeText(this, "Help clicked", Toast.LENGTH_SHORT).show();
            Intent helpIntent = new Intent(HelpActivity.this, HelpActivity.class);
            helpIntent.putExtra("USERNAME", username);
            startActivity(helpIntent);
            return true;
        }
        if(id == R.id.action_account){
            Toast.makeText(this, "Account clicked", Toast.LENGTH_SHORT).show();
            Intent accountIntent = new Intent(HelpActivity.this, AccountActivity.class);
            accountIntent.putExtra("USERNAME", username);
            accountIntent.putExtra("PASSWORD", password);
            accountIntent.putExtra("EMAIL", email);
            startActivity(accountIntent);
            return true;
        }
        if(id == R.id.action_logout){
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
