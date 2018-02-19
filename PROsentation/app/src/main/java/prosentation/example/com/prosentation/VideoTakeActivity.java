package prosentation.example.com.prosentation;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamAdapter;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class VideoTakeActivity extends AppCompatActivity{
    private static final String TAG = "VideoTakeActivity";
    private DynamoDBManager managerClass = new DynamoDBManager();
    private int newId = -1;
    private ProgressBar progressBar;
    private TextView textView;
    private int progressStatus = 0;
    private String username;
    String videoPath;

    private static final int VIDEO_REQUEST_CODE = 1;
    public static final int REQUEST_PICK_VIDEO = 2;
    private static final int PERMISSION_REQUEST_CAMERA = 0;
    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        username = prefs.getString("username", "UNKNOWN");

        Log.d("erkan", "erkan");
        setContentView(R.layout.activity_videotake);

        mLayout = findViewById(R.id.video_layout);

        progressBar = findViewById(R.id.progressBar1);

        textView = (TextView)findViewById(R.id.textView1);
        textView.setText(username);

        Log.d("Environment", Environment.getExternalStorageDirectory().toString());
        File dir = getExternalFilesDir(null);
        Log.d(" dir.getAbsolutePath()",  dir.getAbsolutePath());
        /////////////////////

        /////////////////////
        //doSomething();

        //setNewVideoId();

        new showProcessStatus().execute(3);

        //new UpdateTable().execute();
        //new showProcessStatus().execute();
        //Log.d("I am here", "I am here");
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

    public void doSomething(){
        //Progress spinner bar
        ProgressDialog dialog = new ProgressDialog(this);
        showProgressSpin(dialog);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //managerClass.deleteEntryInDB(VideoTakeActivity.this, 1, 0, 100, "presentation.mp4", 98, "mesut");
            }});

        t.start(); // spawn thread

        try {
            t.join();  // wait for thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //dismiss the dialog
        dialog.dismiss();
    }

    public void deleteSomething(){
        //Progress spinner bar
        ProgressDialog dialog = new ProgressDialog(this);
        showProgressSpin(dialog);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                managerClass.deleteEntryInDB(VideoTakeActivity.this, 1, 1);
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

    /*public void displayNotification(){
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        final int notify_id = 1;
        final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setOngoing(true);
        builder.setContentTitle("Processing Status");
        builder.setContentText("Processing in progress...");


        //new Thread(new Runnable() {
        //    @Override
        //    public void run() {
            int icr;
                for(icr = 0; icr <= 100; icr+=20){
                    builder.setProgress(100, icr, false);
                    notificationManager.notify(notify_id, builder.build());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                builder.setOngoing(false);
                builder.setProgress(0, 0, false);
                builder.setContentText("Processing Complete");
                notificationManager.notify(notify_id, builder.build());
        //    }
        //}).start();


    }*/

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


            //process complete so now we should download the processed file
            //FTPDownload("ec2-13-59-72-180.us-east-2.compute.amazonaws.com", 1025, "video_downloaded.mp4");
            //
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    class InsertIntoTable extends AsyncTask<String, Void, Void> {
       // private ProgressDialog dialog;

//        public InsertIntoTable() {
//            dialog = new ProgressDialog(VideoTakeActivity.this);
//        }

//        @Override
//        protected void onPreExecute() {
//            dialog.setMessage("Inserting into DB, please wait.");
//            dialog.show();
//        }

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

//        @Override
//        protected void onPostExecute(Void aVoid) {
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
//        }
    }

    public void setNewVideoId(){
        /*ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();*/

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
        //dialog.dismiss();
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

        //File folder = new File(Environment.getExternalStorageDirectory().toString() + "/video_app");
        //String s;
        //final File dir = getExternalFilesDir(null);
        /*if(dir == null)
            s = "";
        else
            s = (dir.getAbsolutePath() + "/") + System.currentTimeMillis() + ".mp4";*/
        //dir == null ? "" : (dir.getAbsolutePath() + "/") + System.currentTimeMillis() + ".mp4";
        String dir = folder.getParent();
        Log.d("dir", dir);
        Log.d("SD2", "File Path after: ");
        if(!folder.exists()){
            folder.mkdir();
        }
        File video_file = new File(folder, "sample_video.mp4");
        //return (dir == null ? "" : (dir.getAbsolutePath() + "/")) + System.currentTimeMillis() + ".mp4";
        return video_file;
    }

    void FTPUpload(String URL, int PORT, String Filename){
        FileInputStream fileInputStream = null;
        //https://stackoverflow.com/questions/15309591/ftp-apache-commons-progress-bar-in-java/15309975
        CopyStreamAdapter streamListener = new CopyStreamAdapter() {

            @Override
            public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                //this method will be called everytime some bytes are transferred

                int percent = (int)(totalBytesTransferred*100/getFilePath().length());
                Log.d("total percent is: "+percent+"%","total percent is: " + percent +"%");
                // update your progress bar with this percentage
                progressBar.setProgress(percent);
            }

        };

        try {
                fileInputStream = new FileInputStream(videoPath);
//            File direc = getExternalFilesDir(null);
//            String absoPath = direc.getAbsolutePath();
//            fileInputStream = new FileInputStream(absoPath + "/sample_video.mp4");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FTPClient client = new FTPClient();
        client.setCopyStreamListener(streamListener);
        try {
            client.connect(URL, PORT);
            client.login("erkan", "12345");
            Log.d("Connected to upload", "Connected. Reply: " + client.getReplyString());
            client.setFileType(FTP.BINARY_FILE_TYPE);
            Log.d("Uploading", "Uploading");
            client.enterLocalPassiveMode();
            //client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            Log.d("before store", "before");

            client.storeFile(Filename, fileInputStream);

            //store successful so insert the uploaded video to videos table (wait until table is updated)
            //new showProcessStatus().execute();
            //newId
            //new InsertIntoTable().execute();
            //I am not user to update table here or after this method(FTPUpload)

            Log.d("after store", "after");
            fileInputStream.close();
            Log.d("Stream closed", "Stream closed");
            client.disconnect();
            Log.d("Client disconnect", "Client disconnect");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    long size = getFilePath().length();

    void FTPDownload(String URL, int PORT, String Filename){

        FileOutputStream fileOutputStream = null;

        try {
            File direc = getExternalFilesDir(null);
            String absoPath = direc.getAbsolutePath();
            fileOutputStream = new FileOutputStream(absoPath +"/video_downloaded.mp4");
            Log.d("Abso Path: ", absoPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FTPClient client = new FTPClient();
//        try {
//            FTPFile file = client.mlistFile("/sample_video.mp4");
//            size = file.getSize();
//            Log.d("File Size from server", "File size : " + size);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        CopyStreamAdapter streamListener = new CopyStreamAdapter() {

            @Override
            public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                //this method will be called everytime some bytes are transferred

                int percent = (int)(totalBytesTransferred*100/getFilePath().length());
                Log.d("total percent is: "+percent+"%","total percent is: " + percent +"%");
                // update your progress bar with this percentage
                progressBar.setProgress(percent);
            }

        };

        client.setCopyStreamListener(streamListener);
        try {
            client.connect(URL, PORT);
            client.enterLocalPassiveMode();
            client.login("erkan", "12345");
            Log.d("Connected to download", "Connected. Reply: " + client.getReplyString());
            client.enterLocalPassiveMode();
            client.setFileType(FTP.BINARY_FILE_TYPE);
            Log.d("Downloading", "Downloading");
            client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            FTPFile[] files = client.listFiles("/");
            Log.d("Files length:", Integer.toString(files.length));
            for (FTPFile file : files) {
                Log.d("Filename:", file.getName());
            }

            boolean success = client.retrieveFile("/sample_video.mp4", fileOutputStream);
            //Log.d("here", "false return from ftp.retrieveFile() - code " + client.getReplyCode());
            fileOutputStream.close();
            client.disconnect();
            if (success)
                Log.d("Download","File #1 has been downloaded successfully.");
            else
                Log.d("Download","Download failed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class BackgroundTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... voids) {

            String URL = "http://139.179.225.40:80/";
            Log.d("before send","before send");

            // create a buffer of maximum size
            //AWS IP: 172.31.46.154/ec2-13-59-72-180.us-east-2.compute.amazonaws.com/13.58.144.50
            //For upload 3 lines
            setNewVideoId();
            String filename = "unprocessed/" + newId + ".mp4";
            FTPUpload("ec2-13-59-72-180.us-east-2.compute.amazonaws.com", 1025, filename);
            try {
                Log.d("after send","after send");
                new InsertIntoTable().execute("" + newId, "0", "200", "erkan.mp4", "0", username).get();
                Log.d("afsend","after send");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


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
            //FTPDownload("ec2-13-59-72-180.us-east-2.compute.amazonaws.com", 1025, "video_downloaded.mp4");
            return null;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        Log.d("here1", "hocaa11: ");

        if (resultCode == RESULT_OK) {
            if (requestCode == VIDEO_REQUEST_CODE) {

                Toast.makeText(getApplicationContext(), "Video Successfully Recorded", Toast.LENGTH_LONG).show();
                //Log.d("here", "hocaa: ");
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
}

