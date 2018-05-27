package prosentation.example.com.prosentation.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import prosentation.example.com.prosentation.Activities.VideoDetailsActivity;
import prosentation.example.com.prosentation.R;
import prosentation.example.com.prosentation.Utils.DataGenerator;


public class InstantFragment extends Fragment implements OnChartValueSelectedListener {
    //DataGenerator
    private DataGenerator dataGenerator;

    private Button button;;
    private VideoView videov;
    private MediaController mediaController;
    private Fragment fragment = null;
    private String videoURL;
    private int videoID;

    private Typeface mTfLight;

    //charts
    private LineChart realtimeChart;
    private LineChart averageChart;
    private LineChart faceChart;
    private LineChart gazeChart;
    private LineChart poseChart;
    private LineChart voiceChart;

    //AFRA
    private PieChart emotionChart;
    private ArrayList<Double> myOverallEmotionScores=new ArrayList<Double>();
    private ImageView img;
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private ImageView img4;
    private ImageView img5;
    private String mood = "";

    private ListView listView;
    private Thread thread;
    private int duration;
    private int fileSize;
    private int currentTime;

    private ArrayList<Double> myFaceDataPoints;
    private ArrayList<Double> myGazeDataPoints;
    private ArrayList<Double> myVoiceDataPoints;
    private ArrayList<Double> myPoseDataPoints;

    public InstantFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataGenerator = DataGenerator.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_instant, container, false);


        videov = (VideoView) fragmentView.findViewById(R.id.videoView);

        realtimeChart = (LineChart) fragmentView.findViewById(R.id.chart_realtime);
        averageChart = (LineChart) fragmentView.findViewById(R.id.chart_average);
        faceChart = (LineChart) fragmentView.findViewById(R.id.chart_face);
        gazeChart = (LineChart) fragmentView.findViewById(R.id.chart_gaze);
        poseChart = (LineChart) fragmentView.findViewById(R.id.chart_pose);
        voiceChart = (LineChart) fragmentView.findViewById(R.id.chart_voice);

        realtimeChart.setOnChartValueSelectedListener(this);
        averageChart.setOnChartValueSelectedListener(this);
        faceChart.setOnChartValueSelectedListener(this);
        gazeChart.setOnChartValueSelectedListener(this);
        poseChart.setOnChartValueSelectedListener(this);
        voiceChart.setOnChartValueSelectedListener(this);
        mediaController = new MediaController(getActivity());

        //button = (Button) fragmentView.findViewById(R.id.button);


        // Set up the ViewPager with the sections adapter.

        videoID = Integer.parseInt(getActivity().getIntent().getStringExtra("VIDEO_ID"));
        Log.d("videoIDDDDDDDDDDDDDD: ","videoID: " + videoID);
        videoURL = getActivity().getIntent().getStringExtra("STREAM_VIDEO_URL");
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(getFilePath(videoID + "_unprocessed.mp4"),
                MediaStore.Images.Thumbnails.MINI_KIND);

        videov.setVideoPath(getFilePath(videoID + "_unprocessed.mp4"));
        videov.setMediaController(mediaController);
        //For setting thumnail of the video in background
        Thread seekToThread = new Thread(){
            @Override
            public void run(){
                Log.d("seekto is started", "seekto is started");
                videov.seekTo(100);
            }
        };

        MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(getFilePath(videoID + "_unprocessed.mp4")));
        duration = mp.getDuration()/1000;

        seekToThread.start();
        mediaController.setAnchorView(videov);

        File direc = getActivity().getExternalFilesDir(null);
        String absoPath = direc.getAbsolutePath();

        String face_filename = absoPath + "/" + videoID + "_face.txt";
        String gaze_filename = absoPath + "/" + videoID + "_gaze.txt";
        String voice_filename = absoPath + "/" + videoID + "_voice.txt";
        String pose_filename = absoPath + "/" + videoID + "_pose.txt";

        Log.d("directorrry: ", face_filename);

        setDataPointsFromFile();

        dataGenerator.setRealtimeChart(realtimeChart, myFaceDataPoints, myGazeDataPoints, myPoseDataPoints, myVoiceDataPoints);
        dataGenerator.setOverallChart(averageChart, myFaceDataPoints, myGazeDataPoints, myPoseDataPoints, myVoiceDataPoints);
        dataGenerator.setCustomChart(faceChart, myFaceDataPoints, 0);
        dataGenerator.setCustomChart(gazeChart, myGazeDataPoints, 1);
        dataGenerator.setCustomChart(poseChart, myPoseDataPoints, 2);
        dataGenerator.setCustomChart(voiceChart, myVoiceDataPoints, 3);


        final Runnable highlightRunnable = new Runnable() {
            @Override
            public void run() {
                realtimeChart.highlightValue(currentTime,0);
                averageChart.highlightValue(currentTime,0);
                faceChart.highlightValue(currentTime,0);
                gazeChart.highlightValue(currentTime,0);
                poseChart.highlightValue(currentTime,0);
                voiceChart.highlightValue(currentTime,0);

            }
        };

        Thread thread2 = new Thread(new Runnable() {

            int tempCur;
            @Override
            public void run() {
                while(true){
                    currentTime = (videov.getCurrentPosition()/1000);
                    if(tempCur != currentTime) {
                        realtimeChart.centerViewTo(currentTime, 50, YAxis.AxisDependency.LEFT);
                        averageChart.centerViewTo(currentTime, 50, YAxis.AxisDependency.LEFT);
                        faceChart.centerViewTo(currentTime, 50, YAxis.AxisDependency.LEFT);
                        gazeChart.centerViewTo(currentTime, 50, YAxis.AxisDependency.LEFT);
                        poseChart.centerViewTo(currentTime, 50, YAxis.AxisDependency.LEFT);
                        voiceChart.centerViewTo(currentTime, 50, YAxis.AxisDependency.LEFT);
                        getActivity().runOnUiThread(highlightRunnable);
                        Log.d("tempCur: ", "" + tempCur);
                        tempCur = currentTime;
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread2.start();

        for(int i = 0; i < duration; i++){
              dataGenerator.addPresentationEntry(realtimeChart, myFaceDataPoints, myGazeDataPoints,
                    myPoseDataPoints,  myVoiceDataPoints, i);
            dataGenerator.addAverageEntry(averageChart, myFaceDataPoints, myGazeDataPoints,
                    myPoseDataPoints,  myVoiceDataPoints, i);
            dataGenerator.addCustomEntry(faceChart, myFaceDataPoints, 0, i);
            dataGenerator.addCustomEntry(gazeChart, myGazeDataPoints, 1, i);
            dataGenerator.addCustomEntry(poseChart, myPoseDataPoints, 2, i);
            dataGenerator.addCustomEntry(voiceChart, myVoiceDataPoints, 3, i);
        }
        return fragmentView;
    }

    private double getRandom() {
        Random mRand = new Random();
        return mRand.nextInt(25);
    }

    public void videoplay(View view){
        videov.start();
    }

    public void setDataPointsFromFile(){
        Log.d("Duration: ","" + duration);
        File direc = getActivity().getExternalFilesDir(null);
        String absoPath = direc.getAbsolutePath();
        String face_filename = absoPath + "/" + videoID + "_face.txt";
        String gaze_filename = absoPath + "/" + videoID + "_gaze.txt";
        String voice_filename = absoPath + "/" + videoID + "_voice.txt";
        String pose_filename = absoPath + "/" + videoID + "_pose.txt";

        myFaceDataPoints = getFaceDataPoints(face_filename);
        myGazeDataPoints = getGazeDataPoints(gaze_filename);
        myPoseDataPoints = getPoseDataPoints(voice_filename);
        myVoiceDataPoints = getVoiceDataPoints(pose_filename);

        int faceLen = myFaceDataPoints.size();
        int gazeLen = myGazeDataPoints.size();
        int poseLen = myPoseDataPoints.size();
        int voiceLen = myVoiceDataPoints.size();
        if(faceLen < duration){
            double value = myFaceDataPoints.get(faceLen-1);
            for(int i = faceLen; i <= duration; i++){
                myFaceDataPoints.add(value);
            }
        }
        if(gazeLen < duration){
            double value = myGazeDataPoints.get(gazeLen-1);
            for(int i = gazeLen; i <= duration; i++){
                myGazeDataPoints.add(value);
            }
        }
        if(poseLen < duration){
            double value = myPoseDataPoints.get(poseLen-1);
            for(int i = poseLen; i <= duration; i++){
                myPoseDataPoints.add(value);
            }
        }
        if(voiceLen < duration){
            double value = myVoiceDataPoints.get(voiceLen-1);
            for(int i = voiceLen; i <= duration; i++){
                myVoiceDataPoints.add(value);
            }
        }

    }

    ///COUNTERAFRA
    public ArrayList<Double> getFaceDataPoints(String filename){
        File file = new File(filename);
        ArrayList<Double> myDataPoints = new ArrayList<>();
        //Read text from file
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            int counter = 0;
            while ((line = br.readLine()) != null) {
                Log.d("line: ", line);
                text.append(line);
                text.append('\n');
                if(counter==0){
                    mood = line;
                }
                if(counter==2 ||counter==3||counter==4||counter==5||counter==6||counter==7||counter==8 ){
                    myOverallEmotionScores.add(Double.parseDouble(line)*100);
                }
                if(counter == 1|counter>8){
                    myDataPoints.add(Double.parseDouble(line)*100);
                }
                counter++;
            }
            fileSize = counter-9;
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return myDataPoints;
    }

    public ArrayList<Double> getGazeDataPoints(String filename){
        File file = new File(filename);
        ArrayList<Double> myDataPoints = new ArrayList<>();
        //Read text from file
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            int counter = 0;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                if(counter > 1){
                    myDataPoints.add(Double.parseDouble(line)*100);
                }
                counter++;
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return myDataPoints;
    }
    public ArrayList<Double> getPoseDataPoints(String filename){
        File file = new File(filename);
        ArrayList<Double> myDataPoints = new ArrayList<>();
        //Read text from file
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            int counter = 0;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                if(counter > 1){
                    myDataPoints.add(Double.parseDouble(line)*100);
                }
                counter++;
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return myDataPoints;
    }
    public ArrayList<Double> getVoiceDataPoints(String filename){
        File file = new File(filename);
        ArrayList<Double> myDataPoints = new ArrayList<>();
        //Read text from file
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            int counter = 0;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                if(counter > 1){
                    myDataPoints.add(Double.parseDouble(line)*100);
                }
                counter++;
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return myDataPoints;
    }
    private String getFilePath(String filename) {
        File direc = getActivity().getExternalFilesDir(null);
        String absoPath = direc.getAbsolutePath();

        String path = absoPath + "/" + filename;
        return path;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }
}
