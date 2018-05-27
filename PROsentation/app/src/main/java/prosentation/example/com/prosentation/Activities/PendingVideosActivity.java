package prosentation.example.com.prosentation.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import prosentation.example.com.prosentation.DynamoDB.DynamoDBManager;
import prosentation.example.com.prosentation.Entity.Status;
import prosentation.example.com.prosentation.R;
import prosentation.example.com.prosentation.StatusAdapter;

public class PendingVideosActivity extends AppCompatActivity {
    private static DynamoDBManager dynamoDBManager = DynamoDBManager.getInstance();
    private int faceStatus;
    private int gazeStatus;
    private int poseStatus;
    private int voiceStatus;

    private List<Status> statusList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;;
    private StatusAdapter statusAdapter;

    private ArcProgress arcProgress;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_videos);
        toolbar = (Toolbar) findViewById(R.id.toolbar);setSupportActionBar(toolbar);
        //arcProgress = (ArcProgress) findViewById(R.id.arc_progress_face);
        //arcProgress.setProgress(20);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        statusAdapter = new StatusAdapter(statusList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(statusAdapter);

        try {
            prepareStatusData();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            Intent helpIntent = new Intent(PendingVideosActivity.this, HelpActivity.class);
            //helpIntent.putExtra("USERNAME", username);
            startActivity(helpIntent);
            return true;
        }
        if(id == R.id.action_account){
            Toast.makeText(this, "Account clicked", Toast.LENGTH_SHORT).show();
            Intent accountIntent = new Intent(PendingVideosActivity.this, AccountActivity.class);
            /*accountIntent.putExtra("USERNAME", username);
            accountIntent.putExtra("PASSWORD", password);
            accountIntent.putExtra("EMAIL", email);*/
            startActivity(accountIntent);
            return true;
        }
        if(id == R.id.action_logout){
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareStatusData() throws ExecutionException, InterruptedException {
        Status status1 = new Status(25, 35, 85, 55);
        statusList.add(status1);

        Status status2 = new Status(45, 20, 75, 15);
        statusList.add(status2);
        //new MyStatusesGetterBackground(statusList).execute().get();
        statusAdapter.notifyDataSetChanged();
    }

    class MyStatusesGetterBackground extends AsyncTask<Void, Void, Void> {
        List<prosentation.example.com.prosentation.Entity.Status> myStatuses;
        public MyStatusesGetterBackground(List<prosentation.example.com.prosentation.Entity.Status> myStatuses){
            this.myStatuses = myStatuses;
        }
        @Override
        protected Void doInBackground(Void... params) {
            dynamoDBManager.getMyStatuses(getApplicationContext(), "Oğul", myStatuses);
            return null;
        }
    }

    public void refreshContent(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                statusAdapter = new StatusAdapter(getNewStatuses());
                recyclerView.setAdapter(statusAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 400);
    }

    public List<Status> getNewStatuses() {
        List<Status> statuses = new ArrayList<Status>();
        Status status1 = new Status(25, 32, 83, 75);
        statuses.add(status1);

        Status status2 = new Status(15, 20, 75, 85);
        statuses.add(status2);

        Status status3= new Status(45, 10, 175, 35);
        statuses.add(status3);

        return statuses;
    }
}
