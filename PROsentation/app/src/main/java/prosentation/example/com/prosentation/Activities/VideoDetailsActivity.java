package prosentation.example.com.prosentation.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import prosentation.example.com.prosentation.FormatRelated.CategoryAxisValueFormatter;
import prosentation.example.com.prosentation.Fragments.InstantFragment;
import prosentation.example.com.prosentation.Fragments.SummaryFragment;
import prosentation.example.com.prosentation.ListViewItems.ChartItem;
import prosentation.example.com.prosentation.LoginActivity;
import prosentation.example.com.prosentation.R;
import prosentation.example.com.prosentation.Utils.DataGenerator;

//afra
import android.widget.ImageView;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

public class VideoDetailsActivity extends AppCompatActivity {
    private int videoID;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;

    private String username;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);

        username = getIntent().getStringExtra("USERNAME");
        email = getIntent().getStringExtra("EMAIL");
        password = getIntent().getStringExtra("PASSWORD");

        videoID = Integer.parseInt(getIntent().getStringExtra("VIDEO_ID"));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
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
            SharedPreferences preferences = getSharedPreferences("MyApp", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == android.R.id.home){
            this.finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void setupTabIcons() {

        TextView tabSummary = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabSummary.setText("Summary");
        tabSummary.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.summary_icon, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabSummary);

        TextView tabInstant = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabInstant.setText("Instant");
        tabInstant.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.instant_icon, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabInstant);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new SummaryFragment(), "Summary");
        adapter.addFrag(new InstantFragment(), "Instant");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
