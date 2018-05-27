package prosentation.example.com.prosentation.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import prosentation.example.com.prosentation.DynamoDB.DynamoDBManager;
import prosentation.example.com.prosentation.Entity.Presentation;
import prosentation.example.com.prosentation.Entity.Video;
import prosentation.example.com.prosentation.Fragments.TabFace;
import prosentation.example.com.prosentation.Fragments.TabGaze;
import prosentation.example.com.prosentation.Fragments.TabOverall;
import prosentation.example.com.prosentation.Fragments.TabPose;
import prosentation.example.com.prosentation.Fragments.TabVoice;
import prosentation.example.com.prosentation.Fragments.ViewPagerAdapter;
import prosentation.example.com.prosentation.R;
import prosentation.example.com.prosentation.StatusAdapter;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //user details
    private String username;
    private String email;
    private String password;
    private TextView usernameHeader;
    private TextView emailHeader;

    //Toolbar
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ProgressDialog progressDialog;
    private TextView emailTextView;
    private static DynamoDBManager managerClass = DynamoDBManager.getInstance();
    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private NavigationView navigationView;
    private TextView youHaveXVideos;
    private ArrayList<Presentation> myPresentations;
//    private StatusAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.activity_user_profile);
        tabLayout = (TabLayout) findViewById(R.id.tablayout_id);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager_id);
        drawerLayout = (DrawerLayout)findViewById(R.id.video_layout);
        username = getIntent().getStringExtra("USERNAME");
        email = getIntent().getStringExtra("EMAIL");
        password = getIntent().getStringExtra("PASSWORD");



        myPresentations = new ArrayList<Presentation>();
//        managerClass.getMyVideos(UserProfileActivity.this, username, myPresentations);
//        managerClass.getAllPresentationScores(getApplicationContext(),"Oğul",myPresentations);
//        adapter = new StatusAdapter(myPresentations);
        preparePresentations();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new TabOverall(), "Avg");
        adapter.AddFragment(new TabGaze(), "Gaze");
        adapter.AddFragment(new TabFace(), "Face");
        adapter.AddFragment(new TabPose(), "Pose");
        adapter.AddFragment(new TabVoice(), "Voice");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);setSupportActionBar(toolbar);
        //toolbar.setBackgroundColor(getResources().getColor(R.color.userprofile));

        //Bu iki line'ı starting activityi tekrardan mainactivity yaptığında uncommentle
        //SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        //username = prefs.getString("username", "UNKNOWN");
        //şimdilik hep aynı username kullan


        emailTextView = (TextView)findViewById(R.id.text_userprofile_email);
        emailTextView.setText(email);
        youHaveXVideos = (TextView)findViewById(R.id.text_preamble_userprofile);
        youHaveXVideos.setText("You have "+ myPresentations.size()+ " presentations.");
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
    class MyPresentationsGetterBackground extends AsyncTask<Void, Void, Void> {
        ArrayList<Presentation> myPres;
        public MyPresentationsGetterBackground(ArrayList<Presentation> myPres){
            this.myPres = myPres;
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.d("DB operations", "DB operations");
            managerClass.getAllPresentationScores(UserProfileActivity.this, username, myPres);
            Log.d("asycn myVideos.size()", myPres.size() + "");
            return null;
        }
    }
    private void preparePresentations() {
        ArrayList<Presentation> myPres = new ArrayList<Presentation>();
        try {
            new UserProfileActivity.MyPresentationsGetterBackground(myPres).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.d("myVideos.size()", myPres.size() + "");
        for(int i = 0; i < myPres.size(); i++){
            Log.d("here", "here1231231231");
            Presentation test = new Presentation(myPres.get(i).getOverall(), myPres.get(i).getFace(), myPres.get(i).getGaze(), myPres.get(i).getPose(), myPres.get(i).getVoice());
            myPresentations.add(test);
        }
//        adapter.notifyDataSetChanged();
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
            Intent helpIntent = new Intent(UserProfileActivity.this, HelpActivity.class);
            helpIntent.putExtra("USERNAME", username);
            startActivity(helpIntent);
            return true;
        }
        if(id == R.id.action_account){
            Toast.makeText(this, "Account clicked", Toast.LENGTH_SHORT).show();
            Intent accountIntent = new Intent(UserProfileActivity.this, AccountActivity.class);
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
                Intent intent = new Intent(UserProfileActivity.this, PendingVideosActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                UserProfileActivity.this.startActivity(intent);
                break;
            }

            case R.id.profile: {
                Intent intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                UserProfileActivity.this.startActivity(intent);
                break;
            }

            case R.id.pre_recorded: {
                Intent intent = new Intent(UserProfileActivity.this, MyVideosActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                UserProfileActivity.this.startActivity(intent);
                break;
            }

            case R.id.videos_sample: {
                Intent intent = new Intent(UserProfileActivity.this, PublishedVideosActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                UserProfileActivity.this.startActivity(intent);
                break;
            }
            case R.id.help: {
                Intent intent = new Intent(UserProfileActivity.this, HelpActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                UserProfileActivity.this.startActivity(intent);
                break;
            }
        }
        item.setChecked(true);
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
