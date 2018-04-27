package prosentation.example.com.prosentation.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.universalvideoview.UniversalMediaController;
import com.universalvideoview.UniversalVideoView;


import prosentation.example.com.prosentation.Fragments.VideoDetailsActivityFragments.FaceFragment;
import prosentation.example.com.prosentation.Fragments.VideoDetailsActivityFragments.GazeFragment;
import prosentation.example.com.prosentation.Fragments.VideoDetailsActivityFragments.GestureFragment;
import prosentation.example.com.prosentation.Fragments.VideoDetailsActivityFragments.TotalFragment;
import prosentation.example.com.prosentation.Fragments.VideoDetailsActivityFragments.VoiceFragment;
import prosentation.example.com.prosentation.R;

public class VideoDetailsActivity extends AppCompatActivity {
    private Button clk;;
    private VideoView videov;
    private MediaController mediaController;
    private ProgressDialog progressDialog;
    private Fragment fragment = null;
    private String videoURL;
    private int videoID;

    private String username;
    private String email;
    private String password;

    //Fragments related items
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    //private ActionBarDrawerToggle actionBarDrawerToggle;

    boolean isFullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);setSupportActionBar(toolbar);
        // Show the Up button in the action bar.
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/

        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        username = getIntent().getStringExtra("USERNAME");
        email = getIntent().getStringExtra("EMAIL");
        password = getIntent().getStringExtra("PASSWORD");

        videoID = Integer.parseInt(getIntent().getStringExtra("VIDEO_ID"));
        videoURL = getIntent().getStringExtra("STREAM_VIDEO_URL");
        Log.d("here", "set thumbnail");

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
            Intent helpIntent = new Intent(VideoDetailsActivity.this, HelpActivity.class);
            helpIntent.putExtra("USERNAME", username);
            startActivity(helpIntent);
            return true;
        }
        if(id == R.id.action_account){
            Toast.makeText(this, "Account clicked", Toast.LENGTH_SHORT).show();
            Intent accountIntent = new Intent(VideoDetailsActivity.this, AccountActivity.class);
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

        /*if(id == R.id.home){
            Toast.makeText(this, "Back clicked", Toast.LENGTH_SHORT).show();
            this.finish();
            NavUtils.navigateUpTo(this, new Intent(this, MyVideosActivity.class));
            return true;
        }*/

        /*if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    TotalFragment totalFragment = new TotalFragment();
                    Bundle args = new Bundle();
                    args.putString("VIDEO_ID","" + videoID);
                    args.putString("STREAM_VIDEO_URL",videoURL);
                    totalFragment.setArguments(args);
                    return totalFragment;
                case 1:
                    FaceFragment faceFragment = new FaceFragment();
                    return faceFragment;
                case 2:
                    GazeFragment gazeFragment = new GazeFragment();
                    return gazeFragment;
                case 3:
                    GestureFragment gestureFragment = new GestureFragment();
                    return gestureFragment;
                case 4:
                    VoiceFragment voiceFragment = new VoiceFragment();
                    return voiceFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
