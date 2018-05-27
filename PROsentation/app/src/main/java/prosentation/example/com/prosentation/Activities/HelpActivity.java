package prosentation.example.com.prosentation.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import prosentation.example.com.prosentation.R;

public class HelpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    TextView txt1, txt2, txt3, txt4, txt5;

    ExpandableTextView expandableTextView, expandableTextView2, expandableTextView3, expandableTextView4, expandableTextView5;
    String longText = "\n" +
            "How important is the way you give your presentations to be successful in both academic and professional communities? Today’s competitive and fast-moving world is looking for individuals who have the ability to present not only to express but also to impress. Hence, remarkable presentation skills will help you shine out in your career. PROsentation, which is an Android application, offers a feedback mechanism for users’ presentation videos based on four different behavioral modalities, such as facial expression, gaze, hand gesture and voice. Users can view their scores for each modality separately and all together through interactive graphs. Briefly, PROsentation is the blueprint for a career full of success. ";
    String longText2 = "\n" +
            "Open the app, then either record your presentation from record section or select one of your videos from your phone gallery. Upload it, wait for processing and see your result details from My Videos section. You can publish your videos as others are able to see them. And, you can check your profile to see your overall scores from all your presentations based on face, gaze, pose and voice. ";
    String longText3 = "\n" +
            "Effective presentations are a mixture of a variety of elements. You have to know what your audience wants. You need to prepare good, interesting, engaging content. You must be confident in presenting the material, you have to know how to manage your environment successfully, and you need to make sure that your message has maximum impact.";
    String longText4 = "\n" +
            "We are senior year students and this is our senior project!" +"\n" +
            "Developers; İlteber Ayvacı, Afra Dömeke, Ömer Mesud Toker, Ilgın Çamoğlu, Erkan Önal"+"\n" +
            "Thank you for using PROsentation!";

    private String username;
    private String email;
    private String password;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView usernameHeader;
    private TextView emailHeader;

    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        toolbar = (Toolbar) findViewById(R.id.toolbar);setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout)findViewById(R.id.nav_bar);

        username = getIntent().getStringExtra("USERNAME");
        email = getIntent().getStringExtra("EMAIL");
        password = getIntent().getStringExtra("PASSWORD");

        expandableTextView = (ExpandableTextView)findViewById(R.id.expandable_text_view);
        expandableTextView.setText(longText);

        expandableTextView3 = (ExpandableTextView)findViewById(R.id.expandable_text_view3);
        expandableTextView3.setText(longText2);

        expandableTextView4 = (ExpandableTextView)findViewById(R.id.expandable_text_view4);
        expandableTextView4.setText(longText3);

        expandableTextView5 = (ExpandableTextView)findViewById(R.id.expandable_text_view5);
        expandableTextView5.setText(longText4);


        txt1 = (TextView)findViewById(R.id.text_id);
        // txt2 = (TextView)findViewById(R.id.text_id2);
        txt3 = (TextView)findViewById(R.id.text_id3);
        txt4 = (TextView)findViewById(R.id.text_id4);
        txt5 = (TextView)findViewById(R.id.text_id5);

        navigationView =(NavigationView) findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        usernameHeader = (TextView)headerView.findViewById(R.id.name);
        emailHeader = (TextView)headerView.findViewById(R.id.email);

        usernameHeader.setText(username);
        emailHeader.setText(email);
        actionBarDrawerToggle.syncState();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.pendingVideos: {
                //do something
                Toast.makeText(this, "Pending Videos clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HelpActivity.this, PendingVideosActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                HelpActivity.this.startActivity(intent);
                break;
            }

            case R.id.profile: {
                Intent intent = new Intent(HelpActivity.this, UserProfileActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                HelpActivity.this.startActivity(intent);
                break;
            }

            case R.id.pre_recorded: {
                Intent intent = new Intent(HelpActivity.this, MyVideosActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                HelpActivity.this.startActivity(intent);
                break;
            }

            case R.id.videos_sample: {
                Intent intent = new Intent(HelpActivity.this, PublishedVideosActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                HelpActivity.this.startActivity(intent);
                break;
            }
            case R.id.help: {
                Intent intent = new Intent(HelpActivity.this, HelpActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                HelpActivity.this.startActivity(intent);
                break;
            }
        }
        item.setChecked(true);
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
