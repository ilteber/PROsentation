package prosentation.example.com.prosentation;

import android.provider.MediaStore;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoDetails extends AppCompatActivity {
    Button clk;;
    VideoView videov;
    MediaController mediaController;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);

        clk = (Button)findViewById(R.id.button);
        videov = (VideoView)findViewById(R.id.videoView);
        mediaController = new MediaController(this);
    }

    public void videoplay(View v){
        String videoUrl="http://ec2-13-59-72-180.us-east-2.compute.amazonaws.com/unravel.MP4";
        videov.setVideoPath(videoUrl);
        videov.setMediaController(mediaController);
        mediaController.setAnchorView(videov);
        videov.start();

        progressDialog = ProgressDialog.show(this, "Please wait ...", "Retrieving data ...", true);
        videov.setOnPreparedListener(new OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
            }
        });

    }
}
