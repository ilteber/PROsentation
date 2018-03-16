package prosentation.example.com.prosentation;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ERKAN-PC on 24.02.2018.
 */

public class FTPManager {

    //variable to hold the context
    private Context context;

    //save the context recievied via constructor in a local variable
    public FTPManager(Context context){
        this.context = context;
    }
    public void FTPUpload(String URL, int PORT, String Filename, int curFileLength,
                          StringBuilder curBuilderFilename, ProgressBar curProgressBar){
        final int myFileLength = curFileLength;
        final StringBuilder myBuilderFilename = curBuilderFilename;
        final ProgressBar myProgressBar = curProgressBar;

        FileInputStream fileInputStream = null;
        //https://stackoverflow.com/questions/15309591/ftp-apache-commons-progress-bar-in-java/15309975
        CopyStreamAdapter streamListener = new CopyStreamAdapter() {

            @Override
            public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                //this method will be called everytime some bytes are transferred

                int percent = (int)(totalBytesTransferred*100/myFileLength);
                Log.d("total percent is: "+percent+"%","total percent is: " + percent +"%");
                // update your progress bar with this percentage
                myProgressBar.setProgress(percent);
            }

        };

        try {
            //fileInputStream = new FileInputStream(videoPath);
            File direc = this.context.getExternalFilesDir(null);
            String absoPath = direc.getAbsolutePath();
            fileInputStream = new FileInputStream(absoPath + "/" + myBuilderFilename.toString());
            myBuilderFilename.setLength(0);

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

    public void FTPDownload(String URL, int PORT, String Filename, int curFileLength,
                            StringBuilder curBuilderFilename, ProgressBar curProgressBar){

        final int myFileLength = curFileLength;
        //final StringBuilder myBuilderFilename = curBuilderFilename;
        final ProgressBar myProgressBar = curProgressBar;

        FileOutputStream fileOutputStream = null;

        try {
            File direc = this.context.getExternalFilesDir(null);
            String absoPath = direc.getAbsolutePath();
            fileOutputStream = new FileOutputStream(absoPath +"/video_downloaded.mp4");
            Log.d("Abso Path: ", absoPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FTPClient client = new FTPClient();
        CopyStreamAdapter streamListener = new CopyStreamAdapter() {

            @Override
            public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {
                //this method will be called everytime some bytes are transferred
                int percent = (int)(totalBytesTransferred*100/myFileLength);
                Log.d("total percent is: "+percent+"%","total percent is: " + percent +"%");
                // update your progress bar with this percentage
                myProgressBar.setProgress(percent);
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
}
