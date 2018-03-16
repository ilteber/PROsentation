package prosentation.example.com.prosentation;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;

public class VideoTakeActivity extends AppCompatActivity{
    private static final String TAG = "VideoTakeActivity";
    private DynamoDBManager managerClass = new DynamoDBManager();
    private FTPManager ftpManager = new FTPManager(VideoTakeActivity.this);
    private int newId = -1;
    private ProgressBar progressBar;
    private TextView textView;
    private String username;
    String videoPath;
    StringBuilder builderFilename = new StringBuilder();
    private File curVideoFile;

    private static final int VIDEO_REQUEST_CODE = 1;
    public static final int REQUEST_PICK_VIDEO = 2;
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private View mLayout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //Bu iki line'ı starting activityi tekrardan mainactivity yaptığında uncommentle
        //SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        //username = prefs.getString("username", "UNKNOWN");
        //şimdilik hep aynı username kullan
        username = "husnu";

        Log.d("erkan", "erkan");
        setContentView(R.layout.activity_videotake);

        drawerLayout = (DrawerLayout)findViewById(R.id.video_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.progressBar1);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //textView = (TextView)findViewById(R.id.textView1);
        //textView.setText(username);

        Log.d("Environment", Environment.getExternalStorageDirectory().toString());
        File dir = getExternalFilesDir(null);
        Log.d(" dir.getAbsolutePath()",  dir.getAbsolutePath());
        /////////////////////
        //deleteSomething();
        //setNewVideoId();

        //new showProcessStatus().execute(3);
        //new showProcessStatus().execute();
        //displayNotification();
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
                managerClass.deleteEntryInDB(VideoTakeActivity.this, 5, 0);
                managerClass.deleteEntryInDB(VideoTakeActivity.this, 5, 1);
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

    class InsertIntoTable extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Log.d("DB operations", "DB operations");
            //int k = managerClass.getProcessStatus(2, VideoTakeActivity.this);

            if(newId != -1){
                managerClass.insertVideoToDB(VideoTakeActivity.this, newId, 0, Integer.parseInt(params[2]), params[3], Integer.parseInt(params[4]), params[5]);
                managerClass.insertVideoToDB(VideoTakeActivity.this, newId, 1, Integer.parseInt(params[2]), params[3], Integer.parseInt(params[4]), params[5]);

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
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CAMERA);
        }
    }

    public void selectVideo(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            Intent pickVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickVideoIntent.setType("video/*");
            startActivityForResult(pickVideoIntent, REQUEST_PICK_VIDEO);
        }
        else {
            Toast.makeText(this, "Not granted!", Toast.LENGTH_LONG).show();
            requestPermission();
        }
    }

    public void captureVideo(View view){
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

            String URL = "http://139.179.225.40:80/";
            Log.d("before send","before send");

            // create a buffer of maximum size
            //AWS IP: 172.31.46.154/ec2-13-59-72-180.us-east-2.compute.amazonaws.com/13.58.144.50
            //For upload 3 lines

            //setNewVideoId();
            String filename = "unprocessed/" + newId + ".mp4";
            Log.d("File len 2:", " " + curVideoFile.length());
            ftpManager.FTPUpload("ec2-13-59-72-180.us-east-2.compute.amazonaws.com", 1025, filename, (int)curVideoFile.length(), builderFilename, progressBar);
            //try {
                Log.d("after send","after send");
                //new InsertIntoTable().execute("" + newId, "0", "200", "erkan.mp4", "0", username).get();
                if(newId != -1){
                    managerClass.insertVideoToDB(VideoTakeActivity.this, newId, 0, 100, "test.mp4", 0, "husnu");
                    managerClass.insertVideoToDB(VideoTakeActivity.this, newId, 1, 100, "test.mp4", 0, "husnu");
                }

                Log.d("afsend","after send");
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

            //Process became %100, it means that I can now download the processed file(whose type is 1) with FTPDownload
            //For Download
            //ftpManager.FTPDownload("ec2-13-59-72-180.us-east-2.compute.amazonaws.com", 1025, "video_downloaded.mp4", (int)curVideoFile.length(), builderFilename, progressBar);
            return null;
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
            } else if (requestCode == REQUEST_PICK_VIDEO) {
                if (intent != null) {
                    Log.i("pick video", "Video content URI: " + intent.getData());
                    Toast.makeText(this, "Video content URI: " + intent.getDataString(),
                            Toast.LENGTH_LONG).show();

//                    //videoPath = intent.getDataString();
//                    // SDK < API11
//                    if (Build.VERSION.SDK_INT < 11)
//                        videoPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, intent.getData());
//
//                        // SDK >= 11 && SDK < 19
//                    else if (Build.VERSION.SDK_INT < 19)
//                        videoPath = RealPathUtil.getRealPathFromURI_API11to18(this, intent.getData());
//
//                        // SDK > 19 (Android 4.4)
//                    else
//                        videoPath = RealPathUtil.getRealPathFromURI_API19(this, intent.getData());

                    videoPath = RealPathUtil.getRealPathFromURI(this, intent.getData());

                    Toast.makeText(this, "Video content URI: " + videoPath,
                            Toast.LENGTH_LONG).show();

                    String a = "hey";
                    BackgroundTask b = new BackgroundTask();
                    b.execute(a);
                }
            }
        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show();
        }
    }

    public void startMyVideosActivity(View view){
        Intent intent = new Intent(VideoTakeActivity.this, MyVideosActivity.class);
        intent.putExtra("erkan", 123); //Extra parametre koyablrsn
        VideoTakeActivity.this.startActivity(intent);
    }
}