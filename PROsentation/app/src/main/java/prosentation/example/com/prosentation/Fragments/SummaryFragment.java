package prosentation.example.com.prosentation.Fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import prosentation.example.com.prosentation.GazeView;
import prosentation.example.com.prosentation.R;
import prosentation.example.com.prosentation.Utils.DataGenerator;

public class SummaryFragment extends Fragment implements OnChartValueSelectedListener {
    //DataGenerator
    private DataGenerator dataGenerator;

    //horizontal bar chart
    private HorizontalBarChart summaryChart;
    private Typeface mTfLight;

    private ArrayList<Double> myFaceDataPoints;
    private ArrayList<Double> myGazeDataPoints;
    private ArrayList<Double> myVoiceDataPoints;
    private ArrayList<Double> myPoseDataPoints;

    private int faceScore;
    private int gazeScore;
    private int voiceScore;
    private int poseScore;

    //AFRA
    private PieChart emotionChart;
    private GazeView gaze;
    private ArrayList<Double> myOverallEmotionScores=new ArrayList<Double>();
    private ImageView img;
    private ImageView img1;
    private ImageView img2;
    private ImageView img3;
    private ImageView img4;
    private ImageView img5;
    private String mood = "";

    private String videoURL;
    private int videoID;

    private ListView listView;
    private Thread thread;
    private int fileSize;
    private int currentTime;

    public SummaryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataGenerator = DataGenerator.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_summary, container, false);

        summaryChart = (HorizontalBarChart) fragmentView.findViewById(R.id.chart_summary);

        videoID = Integer.parseInt(getActivity().getIntent().getStringExtra("VIDEO_ID"));
        videoURL = getActivity().getIntent().getStringExtra("STREAM_VIDEO_URL");

        //AFRA
        emotionChart = (PieChart) fragmentView.findViewById(R.id.chart_emotion);
        emotionChart.setOnChartValueSelectedListener(this);
        img = fragmentView.findViewById(R.id.imgView_happy);
        img1 = fragmentView.findViewById(R.id.imgView_sad);
        img2 = fragmentView.findViewById(R.id.imgView_sup);
        img3 = fragmentView.findViewById(R.id.imgView_fear);
        img4 = fragmentView.findViewById(R.id.imgView_disg);
        img5 = fragmentView.findViewById(R.id.imgView_anger);

        gaze = fragmentView.findViewById(R.id.gaze_view);
        gaze.setMinimumWidth(videoID);
        //gaze = new GazeView(getActivity());
        //gaze.setVideoId(videoID);



        summaryChart.setOnChartValueSelectedListener(this);

        File direc = getActivity().getExternalFilesDir(null);
        String absoPath = direc.getAbsolutePath();

        String face_filename = absoPath + "/" + videoID + "_face.txt";
        String gaze_filename = absoPath + "/" + videoID + "_gaze.txt";
        String voice_filename = absoPath + "/" + videoID + "_voice.txt";
        String pose_filename = absoPath + "/" + videoID + "_pose.txt";

        setDataPointsFromFile();

        Log.d("face: ", "" + faceScore);
        Log.d("gaze ", "" + gazeScore);
        Log.d("pose: ", "" + poseScore);
        Log.d("voice: ", "" + voiceScore);
        dataGenerator.setSummaryChart(summaryChart, faceScore, gazeScore, poseScore, voiceScore, mTfLight);

        //AFRA
        dataGenerator.setEmotionsDistr(emotionChart,myOverallEmotionScores);
        dataGenerator.setEmotionsSymbol(img,img1,img2,img3,img4,img5,mood);

        return fragmentView;
    }

    public void setDataPointsFromFile(){
        File direc = getActivity().getExternalFilesDir(null);
        String absoPath = direc.getAbsolutePath();
        String face_filename = absoPath + "/" + videoID + "_face.txt";
        String gaze_filename = absoPath + "/" + videoID + "_gaze.txt";
        String voice_filename = absoPath + "/" + videoID + "_voice.txt";
        String pose_filename = absoPath + "/" + videoID + "_pose.txt";

        myFaceDataPoints = getFaceDataPoints(face_filename);
        myGazeDataPoints = getGazeDataPoints(gaze_filename);
        myPoseDataPoints = getPoseDataPoints(pose_filename);
        myVoiceDataPoints = getVoiceDataPoints(voice_filename);
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
                if(counter == 2){
                    faceScore = (int)(Double.parseDouble(line)*100);
                }
                if(counter==3||counter==4||counter==5||counter==6||counter==7||counter==8 ){
                    myOverallEmotionScores.add(Double.parseDouble(line)*100);
                }
                if(counter>8){
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
                if(counter == 1){
                    gazeScore = (int)(Double.parseDouble(line)*100);
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
                if(counter == 1){
                    poseScore = (int)(Double.parseDouble(line)*100);
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
                if(counter == 1){
                    voiceScore = (int)(Double.parseDouble(line)*100);
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

    }

    @Override
    public void onNothingSelected() {

    }
}