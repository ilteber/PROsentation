package prosentation.example.com.prosentation.Activities;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import is.arontibo.library.ElasticDownloadView;
import prosentation.example.com.prosentation.BuildConfig;
import prosentation.example.com.prosentation.DynamoDB.DynamoDBManager;
import prosentation.example.com.prosentation.FTP.FTPManager;
import prosentation.example.com.prosentation.LoginActivity;
import prosentation.example.com.prosentation.R;
import prosentation.example.com.prosentation.Utils.RealPathUtil;

public class VideoTakeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "VideoTakeActivity";
    private static final String AWSIP = "ec2-52-17-110-82.eu-west-1.compute.amazonaws.com";
    private static DynamoDBManager managerClass = DynamoDBManager.getInstance();
    private FTPManager ftpManager = new FTPManager(VideoTakeActivity.this);
    private int newId = -1;
    private ProgressBar progressBar;
    private TextView textView;
    private TextView usernameHeader;
    private TextView emailHeader;
    private String username;
    private String email;
    private String password;
    String videoPath;
    StringBuilder builderFilename = new StringBuilder();
    private File curVideoFile;

    private static final int REQUEST_TAKE_GALLERY_VIDEO = 3;
    private static final int VIDEO_REQUEST_CODE = 1;
    public static final int REQUEST_PICK_VIDEO = 2;
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private View mLayout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FrameLayout frameLayout1;
    private FrameLayout frameLayout2;
    private AnimatedCircleLoadingView animatedCircleLoadingView;
    private ElasticDownloadView elasticDownloadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //Bu iki line'ı starting activityi tekrardan mainactivity yaptığında uncommentle
        //SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        //username = prefs.getString("username", "UNKNOWN");
        //şimdilik hep aynı username kullan
        /*username = "Oğul";
        email = "Oğul@gmail.com";
        password = "12345";*/
        /*username = getIntent().getStringExtra("USERNAME");
        password = getIntent().getStringExtra("PASSWORD");
        email = getIntent().getStringExtra("EMAIL");*/
        username = "Ogul";
        password = "12345";
        email = "Ogul@gmail.com";

        Log.d("username: ", username);
        Log.d("password: ", password);
        Log.d("email: ", email);


        Log.d("erkan", "erkan");
        setContentView(R.layout.activity_videotake);


        drawerLayout = (DrawerLayout)findViewById(R.id.video_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);setSupportActionBar(toolbar);
        //toolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
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
        //frameLayout1 = (FrameLayout) findViewById(R.id.frameLayout1);
        frameLayout2 = (FrameLayout) findViewById(R.id.frameLayout2);
        //animatedCircleLoadingView = (AnimatedCircleLoadingView)findViewById(R.id.circle_loading_view);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //textView = (TextView)findViewById(R.id.textView1);
        //textView.setText(username);

        Log.d("Environment", Environment.getExternalStorageDirectory().toString());
        File dir = getExternalFilesDir(null);
        Log.d(" dir.getAbsolutePath()",  dir.getAbsolutePath());
        //Find the directory for the SD Card using the API

        //*Don't* hardcode "/sdcard"

        /*File direc = getExternalFilesDir(null);
        String absoPath = direc.getAbsolutePath();

        //Get the text file
        File file = new File(absoPath + "/face_result.txt");

        //Read text from file
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        Log.d("text","text is :" + text.toString());*/
        ////////////////////

        //deleteSomething();
        //setNewVideoId();

        //new showProcessStatus().execute(3);
        //new showProcessStatus().execute();
        //displayNotification();

        //Intent intent = new Intent(VideoTakeActivity.this,prosentation.example.com.prosentation.Activities.AnimatedCircleActivity.class);
        //startActivity(intent);
    }


    /*class BackgroundTask2 extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... voids) {
            builderFilename.append("face_result.txt");
            ftpManager.FTPDownload(AWSIP, 1025, "face_result.txt", 1000, builderFilename, progressBar);
            return null;
        }
    }*/

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
            Intent helpIntent = new Intent(VideoTakeActivity.this, HelpActivity.class);
            helpIntent.putExtra("USERNAME", username);
            startActivity(helpIntent);
            return true;
        }
        if(id == R.id.action_account){
            Toast.makeText(this, "Account clicked", Toast.LENGTH_SHORT).show();
            Intent accountIntent = new Intent(VideoTakeActivity.this, AccountActivity.class);
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

        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showProgressSpin(ProgressDialog dialog){
        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void deleteSomething(){
        //Progress spinner bar
        ProgressDialog dialog = new ProgressDialog(this);
        showProgressSpin(dialog);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //managerClass.deleteEntryInDB(VideoTakeActivity.this, 1, 0);


                managerClass.insertVideoToDB(getApplicationContext(), 5, 1, 100,"test.mp4", 0,"Ogul",1);
                //managerClass.insertVideoToDB(getApplicationContext(), 2, 1, 100,"test.mp4", 0,"Oğul",0);
                //managerClass.insertVideoToDB(getApplicationContext(), 3, 1, 100,"test.mp4", 0,"Oğul",0);
                //managerClass.insertVideoToDB(getApplicationContext(), 4, 1, 100,"test.mp4", 0,"Oğul",0);
                //managerClass.insertVideoToDB(getApplicationContext(), 5, 1, 100,"test.mp4", 0,"Oğul",0);
                //managerClass.insertVideoToDB(getApplicationContext(), 6, 1, 100,"test.mp4", 0,"Oğul",0);

                //managerClass.insertVideoToDB(getApplicationContext(), 4, 1, 100,"test.mp4", 0,"Oğul",0);
                //managerClass.insertVideoToDB(getApplicationContext(), 5, 1, 100,"test.mp4", 0,"Oğul",0);
                //managerClass.deleteEntryInDB(VideoTakeActivity.this, 2, 0);
                //managerClass.insertVideoToDB(getApplicationContext(), 3, 0, 100,"blabla", 0,"erkan",0);
            }});

        t.start();

        try {
            t.join();  // wait for thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //dismiss the dialog
        dialog.dismiss();
    }

    class showProcessStatus extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            Log.d("DB operations", "DB operations");

            /*Intent emptyIntent = new Intent(VideoTakeActivity.this, VideoTakeActivity.class);
            emptyIntent.putExtra(getString(R.string.NOTIFICATION_ID_KEY), 1);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    VideoTakeActivity.this,
                    0,
                    emptyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                    );*/

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(VideoTakeActivity.this);
            final int notify_id = 1;
            final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            builder.setSmallIcon(R.drawable.notification_icon);
            builder.setOngoing(true);
            builder.setContentTitle("Processing Status");
            builder.setContentText("Processing in progress...");


            int icr;
            int status = managerClass.getProcessStatus(params[0], VideoTakeActivity.this);
            for(icr = 0; icr < 100; icr = status){
                //Log.d("k: ", " " + k);
                builder.setProgress(100, icr, false);
                notificationManager.notify(notify_id, builder.build());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                status = managerClass.getProcessStatus(params[0], VideoTakeActivity.this);
            }
            builder.setOngoing(false);
            builder.setProgress(0, 0, false);
            builder.setContentText("Processing Complete");

            //builder.addAction(R.drawable.icon_download, "Download", pendingIntent); // #0
            //builder.addAction(R.drawable.icon_download, "Cancel", pendingIntent);  // #1
            //builder.addAction(new NotificationCompat.Action(R.drawable.icon_download,"hey",
            //        PendingIntent.getActivity(VideoTakeActivity.this, 0, downloadIntent, PendingIntent.FLAG_UPDATE_CURRENT)));
            notificationManager.notify(notify_id, builder.build());
            return null;
        }
    }

    public void showMyNotification(int Id){
        Log.d("DB operations", "DB operations");

            /*Intent emptyIntent = new Intent(VideoTakeActivity.this, VideoTakeActivity.class);
            emptyIntent.putExtra(getString(R.string.NOTIFICATION_ID_KEY), 1);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    VideoTakeActivity.this,
                    0,
                    emptyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                    );*/

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(VideoTakeActivity.this);
        final int notify_id = 1;
        final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setOngoing(true);
        builder.setContentTitle("Processing Status");
        builder.setContentText("Processing in progress...");


        int icr;
        int status = managerClass.getProcessStatus(Id, VideoTakeActivity.this);
        for(icr = 0; icr < 100; icr = status){
            //Log.d("k: ", " " + k);
            builder.setProgress(100, icr, false);
            notificationManager.notify(notify_id, builder.build());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            status = managerClass.getProcessStatus(Id, VideoTakeActivity.this);
        }
        builder.setOngoing(false);
        builder.setProgress(0, 0, false);
        builder.setContentText("Processing Complete");
        notificationManager.notify(notify_id, builder.build());
    }
    class InsertIntoTable extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Log.d("DB operations", "DB operations");
            //int k = managerClass.getProcessStatus(2, VideoTakeActivity.this);

            if(newId != -1){
                managerClass.insertVideoToDB(VideoTakeActivity.this, newId, 0, Integer.parseInt(params[2]), params[3], Integer.parseInt(params[4]), params[5], 0);
                managerClass.insertVideoToDB(VideoTakeActivity.this, newId, 1, Integer.parseInt(params[2]), params[3], Integer.parseInt(params[4]), params[5], 0);

            }
            return null;
        }
    }

    public void setNewVideoId(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int tempId = managerClass.getNewVideoId(VideoTakeActivity.this);
                newId = tempId;
            }});

        t.start(); // spawn thread

        try {
            t.join();  // wait for thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void requestPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(mLayout, "Permission Needed",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(VideoTakeActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CAMERA);
                }
            }).show();

        } else {
            Snackbar.make(mLayout, "Permission is not available", Snackbar.LENGTH_SHORT).show();
            //Log.d("crashed","crashed");
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
        }
    }


    public void selectVideo(View view) {
        /*Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);*/


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            Intent pickVideoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickVideoIntent.setType("video/*");
            startActivityForResult(Intent.createChooser(pickVideoIntent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);
            //startActivityForResult(pickVideoIntent, REQUEST_PICK_VIDEO);
        }
        else {
            Toast.makeText(this, "Not granted!", Toast.LENGTH_LONG).show();
            //isStoragePermissionGranted();
            requestPermission();
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void captureVideo(View view){
        Log.d("captureVideo before","captureVideo");
        Intent camera_intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
        File video_file = getFilePath();

        Uri video_uri;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            video_uri = FileProvider.getUriForFile(VideoTakeActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    video_file);
        }
        else {
            video_uri = Uri.fromFile(video_file);
        }

        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, video_uri);
        camera_intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);  //1 for max quality, 0 for lowest quality

        Log.d("captureVideo after","captureVideo");
        startActivityForResult(camera_intent, VIDEO_REQUEST_CODE);
    }

    private File getFilePath() {
        File direc = getExternalFilesDir(null);
        String absoPath = direc.getAbsolutePath();

        Log.d("SD1", "File Path: ");
        File folder = new File(absoPath + "/");

        String dir = folder.getParent();
        Log.d("dir", dir);
        Log.d("SD2", "File Path after: ");
        if(!folder.exists()){
            folder.mkdir();
        }
        //SimpleDateFormat sdf8 = new SimpleDateFormat("yyyyMMdd");
        //String currentDateandTime = sdf8.format(new Date());     //20090630
        //set the video Id here to identfy the name of the file in phone and to use this Id in FTPUpload
        setNewVideoId();

        //builderFilename.append(username + "_" + currentDateandTime + "_" + newId + ".mp4");
        builderFilename.append(newId + "_unprocessed" +".mp4");
        File video_file = new File(folder, builderFilename.toString());
        curVideoFile = video_file;
        //return (dir == null ? "" : (dir.getAbsolutePath() + "/")) + System.currentTimeMillis() + ".mp4";
        return video_file;
    }


    class BackgroundTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... voids) {
            Log.d("before send","before send");

            // create a buffer of maximum size
            //AWS IP: 172.31.46.154/ec2-13-59-72-180.us-east-2.compute.amazonaws.com/13.58.144.50
            //For upload 3 lines

            //setNewVideoId();
            String filename = newId + ".mp4";
            Log.d("File len 2:", " " + curVideoFile.length());
            //ftpManager.FTPUpload("ec2-13-59-72-180.us-east-2.compute.amazonaws.com", 1025, filename, (int)curVideoFile.length(), builderFilename, progressBar);
            ftpManager.FTPUpload(AWSIP, 1025, filename, (int)curVideoFile.length(), builderFilename, progressBar);


            builderFilename.setLength(0);
            //try {
            Log.d("after send","after send");
            //new InsertIntoTable().execute("" + newId, "0", "200", "erkan.mp4", "0", username).get();
            if(newId != -1){
                managerClass.insertVideoToDB(VideoTakeActivity.this, newId, 0, 100, "test.mp4", 0, username, 0);
                managerClass.insertVideoToDB(VideoTakeActivity.this, newId, 1, 100, "test.mp4", 0, username, 0);
            }

            Log.d("before notification","before notification");
            /*} catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/


            //if upload successful, add corresponding row for processed video to DB (initially processed bar as 0)
            //and look for that processed video in database in 5 seconds intervals
            /*try {
                new showProcessStatus().execute(newId).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/

            showMyNotification(newId);
            //Process became %100, it means that I can now download the processed file(whose type is 1) with FTPDownload
            //For Download
            ftpManager.FTPDownload(AWSIP, 1025, "/results/" + newId + "_face.txt", 100, builderFilename, progressBar, newId + "_face.txt", newId);
            ftpManager.FTPDownload(AWSIP, 1025, "/results/" + newId + "_gaze.txt", 100, builderFilename, progressBar, newId + "_gaze.txt", newId);
            ftpManager.FTPDownload(AWSIP, 1025, "/results/" + newId + "_voice.txt", 100, builderFilename, progressBar, newId + "_voice.txt", newId);
            ftpManager.FTPDownload(AWSIP, 1025, "/results/" + newId + "_pose.txt", 100, builderFilename, progressBar, newId + "_pose.txt", newId);
            return null;
        }
    }
    // UPDATED!
    public String getMyPath(Uri uri) {
        String[] projection = { MediaStore.Video.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (requestCode == VIDEO_REQUEST_CODE) {

                Toast.makeText(getApplicationContext(), "Video Successfully Recorded", Toast.LENGTH_LONG).show();
                String a = "hey";
                BackgroundTask b = new BackgroundTask();
                b.execute(a);
            } else if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                Uri selectedImageUri = intent.getData();


                // OI FILE Manager
                String filemanagerstring = getRealPathFromUri(getApplicationContext(), selectedImageUri);
                /*Log.d("selected(): ", filemanagerstring);

                builderFilename.setLength(0);
                builderFilename.append(filemanagerstring);
                File video_file = new File(selectedImageUri.toString());
                curVideoFile = video_file;*/

                //curVideoFile
                // MEDIA GALLERY
                if (filemanagerstring != null) {

                    Toast.makeText(this, "Video content URI NOt null: " + filemanagerstring,
                            Toast.LENGTH_LONG).show();
                }
                Toast.makeText(this, "Video content URI null: "  + filemanagerstring,
                        Toast.LENGTH_LONG).show();
                String a = "hey";

                //BackgroundTask b = new BackgroundTask();
                //b.execute(a);
            }
        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.pendingVideos: {
                //do something
                Toast.makeText(this, "Pending Videos clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(VideoTakeActivity.this, PendingVideosActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                VideoTakeActivity.this.startActivity(intent);
                break;
            }

            case R.id.profile: {
                Intent intent = new Intent(VideoTakeActivity.this, UserProfileActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                VideoTakeActivity.this.startActivity(intent);
                break;
            }

            case R.id.pre_recorded: {
                Intent intent = new Intent(VideoTakeActivity.this, MyVideosActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                VideoTakeActivity.this.startActivity(intent);
                break;
            }

            case R.id.videos_sample: {
                Intent intent = new Intent(VideoTakeActivity.this, PublishedVideosActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                VideoTakeActivity.this.startActivity(intent);
                break;
            }
            case R.id.help: {
                Intent intent = new Intent(VideoTakeActivity.this, HelpActivity.class);
                intent.putExtra("USERNAME", username);
                intent.putExtra("EMAIL", email);
                intent.putExtra("PASSWORD", password);
                VideoTakeActivity.this.startActivity(intent);
                break;
            }
        }
        item.setChecked(true);
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    public void startMyVideosActivity(View view){
        Intent intent = new Intent(VideoTakeActivity.this, MyVideosActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("PASSWORD", password);
        intent.putExtra("EMAIL", email);
        VideoTakeActivity.this.startActivity(intent);
    }

    public void startUserProfileActivity(View view){
        Intent intent = new Intent(VideoTakeActivity.this, UserProfileActivity.class);
        intent.putExtra("USERNAME", username); //Extra parametre koyablrsn
        intent.putExtra("PASSWORD", password);
        intent.putExtra("EMAIL", email);
        VideoTakeActivity.this.startActivity(intent);
    }

    public void startHelpActivity(View view){
        Intent intent = new Intent(VideoTakeActivity.this, HelpActivity.class);
        intent.putExtra("USERNAME", username); //Extra parametre koyablrsn
        intent.putExtra("PASSWORD", password);
        intent.putExtra("EMAIL", email);
        VideoTakeActivity.this.startActivity(intent);
    }

    public void startPublishedVideosActivity(View view){
        Intent intent = new Intent(VideoTakeActivity.this, PublishedVideosActivity.class);
        intent.putExtra("USERNAME", username); //Extra parametre koyablrsn
        intent.putExtra("PASSWORD", password);
        intent.putExtra("EMAIL", email);
        VideoTakeActivity.this.startActivity(intent);
    }
}