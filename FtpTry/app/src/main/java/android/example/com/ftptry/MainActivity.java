package android.example.com.ftptry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.example.com.ftptry.R;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private int progressStatus = 0;

    private int VIDEO_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Log.d("erkan", "erkan");
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar1);



        Log.d("Environment", Environment.getExternalStorageDirectory().toString());
        File dir = getExternalFilesDir(null);
        Log.d(" dir.getAbsolutePath()",  dir.getAbsolutePath());
        /////////////////////
//        String a = "hey";
//        BackgroundTask b = new BackgroundTask();
//        b.execute(a);
    }

    public void captureVideo(View view){
        Intent camera_intent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
        File video_file = getFilePath();

        Uri video_uri;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            video_uri = FileProvider.getUriForFile(MainActivity.this,
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
            File direc = getExternalFilesDir(null);
            String absoPath = direc.getAbsolutePath();
            fileInputStream = new FileInputStream(absoPath +"/sample_video.mp4");

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
//            FTPUpload("ec2-13-59-72-180.us-east-2.compute.amazonaws.com", 1025, "sample_video.mp4");
            FTPDownload("ec2-13-59-72-180.us-east-2.compute.amazonaws.com", 1025, "video_downloaded.mp4");
            return null;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("here1", "hocaa11: ");
        if (requestCode == VIDEO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Video Successfully Recorded", Toast.LENGTH_LONG).show();
                //Log.d("here", "hocaa: ");
                String a = "hey";
                BackgroundTask b = new BackgroundTask();
                b.execute(a);
            } else {
                Toast.makeText(getApplicationContext(), "Video Capture Failed...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
