package prosentation.example.com.prosentation.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import prosentation.example.com.prosentation.DynamoDB.DynamoDBManager;
import prosentation.example.com.prosentation.Entity.Video;
import prosentation.example.com.prosentation.OnItemClickListener;
import prosentation.example.com.prosentation.R;
import prosentation.example.com.prosentation.VideoAdapter;

public class PublishedVideosActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static DynamoDBManager managerClass = DynamoDBManager.getInstance();
    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private List<Video> videoList;
    Toolbar toolbar;

    private String username;
    private String email;
    private String password;

    private NavigationView navigationView;
    private TextView textPublished;
    private TextView usernameHeader;
    private TextView emailHeader;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_published_videos);

        username = getIntent().getStringExtra("USERNAME");

        email = getIntent().getStringExtra("EMAIL");
        password = getIntent().getStringExtra("PASSWORD");

        drawerLayout = (DrawerLayout)findViewById(R.id.main_content3);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        videoList = new ArrayList<>();
        adapter = new VideoAdapter(this, videoList, new OnItemClickListener() {
            @Override
            public void onItemClick(Video item) {
                Log.d("test tıklama", item.getThumbnailURL());
                Intent intent = new Intent(PublishedVideosActivity.this, VideoDetailsActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("PASSWORD", password);
                intent.putExtra("EMAIL", email);
                intent.putExtra("VIDEO_ID", "" + item.getId());
                intent.putExtra("STREAM_VIDEO_URL", item.getVideoURL());
                PublishedVideosActivity.this.startActivity(intent);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new PublishedVideosActivity.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareAlbums();

        textPublished = (TextView) findViewById(R.id.publishedText);
        textPublished.setText("You have "+ videoList.size() + " presentations");

        ImageView imageView;
        //imageView = (ImageView)findViewById(R.id.backdrop);
        //imageView.setImageDrawable((int)R.drawable.gradientbackground_published);
        /*try {
            Glide.with(this).load(R.drawable.souma).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

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


    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    class MyVideosGetterBackground extends AsyncTask<Void, Void, Void> {
        ArrayList<Video> myVideos;
        public MyVideosGetterBackground(ArrayList<Video> myVideos){
            this.myVideos = myVideos;
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.d("DB operations", "DB operations");
            managerClass.getPublishedVideos(PublishedVideosActivity.this, username, myVideos);
            Log.d("asycn myVideos.size()", myVideos.size() + "");
            return null;
        }
    }

    /**
     * Adding few albums for testing
     */
    private void prepareAlbums() {
        ArrayList<Video> myVideos = new ArrayList<Video>();
        try {
            new PublishedVideosActivity.MyVideosGetterBackground(myVideos).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.d("myVideos.size()", myVideos.size() + "");
        for(int i = 0; i < myVideos.size(); i++){
            Log.d("here", "here1231231231");
            Video test = new Video(myVideos.get(i).getId(), myVideos.get(i).getName(), myVideos.get(i).getVideoURL(), myVideos.get(i).getNumOfContents(), myVideos.get(i).getThumbnailURL());
            videoList.add(test);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.pendingVideos: {
                //do something
                Toast.makeText(this, "Pending Videos clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PublishedVideosActivity.this, PendingVideosActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                PublishedVideosActivity.this.startActivity(intent);
                break;
            }

            case R.id.profile: {
                Intent intent = new Intent(PublishedVideosActivity.this, UserProfileActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                PublishedVideosActivity.this.startActivity(intent);
                break;
            }

            case R.id.pre_recorded: {
                Intent intent = new Intent(PublishedVideosActivity.this, MyVideosActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                PublishedVideosActivity.this.startActivity(intent);
                break;
            }

            case R.id.videos_sample: {
                Intent intent = new Intent(PublishedVideosActivity.this, PublishedVideosActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                PublishedVideosActivity.this.startActivity(intent);
                break;
            }
            case R.id.help: {
                Intent intent = new Intent(PublishedVideosActivity.this, HelpActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                PublishedVideosActivity.this.startActivity(intent);
                break;
            }
        }
        item.setChecked(true);
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
