package prosentation.example.com.prosentation.Fragments.VideoDetailsActivityFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import prosentation.example.com.prosentation.Activities.MainActivity;
import prosentation.example.com.prosentation.Activities.MyVideosActivity;
import prosentation.example.com.prosentation.Activities.VideoDetailsActivity;
import prosentation.example.com.prosentation.R;

public class TotalFragment extends Fragment {


    private final Handler mHandler = new Handler();
    private Runnable mTimer1;
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint> mSeries1;
    private LineGraphSeries<DataPoint> mSeries2;
    private LineGraphSeries<DataPoint> mSeries3;
    private LineGraphSeries<DataPoint> mSeries4;
    private double graph2LastXValue = 0;

    private int videoID;
    private static int curSec = 0;
    private boolean isDataReady;
    private ArrayList<Double> myFaceDataPoints;
    private ArrayList<Double> myGazeDataPoints;
    private ArrayList<Double> myVoiceDataPoints;
    private ArrayList<Double> myPoseDataPoints;

    private Button button;
    private VideoView videov;
    private GraphView graph;
    private MediaController mediaController;
    private ProgressDialog progressDialog;
    private String videoURL;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view_fragment_total = inflater.inflate(R.layout.fragment_total, container, false);

        videov = (VideoView)view_fragment_total.findViewById(R.id.videoView);
        videov.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                isDataReady = false;
                Log.d("Video completed","Video completed");
            }
        });
        mediaController = new MediaController(getActivity());
        button = (Button)view_fragment_total.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoplay();
                //graph.removeAllSeries();
            }
        });

        videoID = Integer.parseInt(getArguments().getString("VIDEO_ID"));
        videoURL = getArguments().getString("STREAM_VIDEO_URL");
        isDataReady = false;
        Log.d("videoID: ","videoID: " + videoID);
        //graph view test (static)
       /* GraphView graph = (GraphView) view_fragment_total.findViewById(R.id.graph);
        if(graph == null)
            Log.d("null","null graph");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6),
                new DataPoint(7, 11),
                new DataPoint(15, 56),
                new DataPoint(34, 66),
                new DataPoint(38, 69),
                new DataPoint(48, 85),
                new DataPoint(100, 115)
        });
        series.setTitle("Points");
        series.setDrawBackground(true);
        series.setColor(Color.argb(255, 255, 60, 60));
        series.setBackgroundColor(Color.argb(100, 204, 119, 119));
        series.setDrawDataPoints(true);
        graph.addSeries(series);

        // legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);*/

        //graph view test (static)
        /*GraphView graph = (GraphView) view_fragment_total.findViewById(R.id.graph);
        mSeries1 = new LineGraphSeries<>(generateData());
        graph.addSeries(mSeries1);*/

        //
        File direc = getActivity().getExternalFilesDir(null);
        String absoPath = direc.getAbsolutePath();
        String face_filename = absoPath + "/" + videoID + "_face.txt";
        String gaze_filename = absoPath + "/" + videoID + "_gaze.txt";
        String voice_filename = absoPath + "/" + videoID + "_voice.txt";
        String pose_filename = absoPath + "/" + videoID + "_pose.txt";

        myFaceDataPoints = getFaceDataPoints(face_filename);
        myGazeDataPoints = getFaceDataPoints(gaze_filename);
        myVoiceDataPoints = getFaceDataPoints(voice_filename);
        myPoseDataPoints = getFaceDataPoints(voice_filename);

        isDataReady = false;
        /*for(int i = 0; i < myFaceDataPoints.size(); i++){
            Log.d("Face Point " + i + ":",  "" + myFaceDataPoints.get(i));
        }
        for(int i = 0; i < myFaceDataPoints.size(); i++){
            Log.d("Gaze Point " + i + ":",  "" + myGazeDataPoints.get(i));
        }
        for(int i = 0; i < myFaceDataPoints.size(); i++){
            Log.d("Voice Point " + i + ":",  "" + myVoiceDataPoints.get(i));
        }*/


        graph = (GraphView) view_fragment_total.findViewById(R.id.graph);
        mSeries1 = new LineGraphSeries<>();
        mSeries2 = new LineGraphSeries<>();
        mSeries3 = new LineGraphSeries<>();
        mSeries4 = new LineGraphSeries<>();

        mSeries1.setTitle("Face");
        mSeries2.setTitle("Gaze");
        mSeries3.setTitle("Voice");
        mSeries4.setTitle("Pose");

        mSeries1.setColor(Color.argb(255, 255, 0, 0));
        mSeries2.setColor(Color.argb(255, 0, 255, 0));
        mSeries3.setColor(Color.argb(255, 0, 0, 255));
        mSeries4.setColor(Color.argb(255, 255, 255, 0));

        graph.addSeries(mSeries1);
        graph.addSeries(mSeries2);
        graph.addSeries(mSeries3);
        graph.addSeries(mSeries4);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling

        graph.getGridLabelRenderer().setVerticalAxisTitle("Points");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("In seconds");

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getLegendRenderer().setPadding(15);
        graph.getLegendRenderer().setSpacing(5);

        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(50);
        graph.getGridLabelRenderer().setTextSize(30);

        return view_fragment_total;
    }

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*((MyVideosActivity) activity).onSectionAttached(
                getArguments().getInt(MyVideosActivity.ARG_SECTION_NUMBER));*/
    }

    @Override
    public void onResume() {
        super.onResume();

        mTimer2 = new Runnable() {
            int index = 0;
            @Override
            public void run() {
                /*graph2LastXValue += 1;
                mSeries1.appendData(new DataPoint(graph2LastXValue, getRandom()), true, 1000);
                mSeries2.appendData(new DataPoint(graph2LastXValue, getRandom()+1), true, 1000);
                Log.d("test", "test");
                mHandler.postDelayed(this, 200);
                count++;*/
                //if(isDataReady == false)

                Log.d("Duration: ", "" + videov.getDuration());
                Log.d("Current Position: ", "" + videov.getCurrentPosition());
                if ( videov.isPlaying() )
                {
                    Log.d("Video is playing: ", "" + videov.getCurrentPosition());
                    //isDataReady = true;
                }
                else
                {
                    Log.d("Video is stopped ", "" + videov.getCurrentPosition());
                    //isDataReady = false;
                }
                if(isDataReady){
                    //int index = (TotalFragment.curSec)/1000;
                    Log.d("RDY", "RDY");
                    if(index < myFaceDataPoints.size())
                        mSeries1.appendData(new DataPoint(graph2LastXValue, myFaceDataPoints.get(index) + getRandom()), false, 10000);

                    if(index < myGazeDataPoints.size())
                        mSeries2.appendData(new DataPoint(graph2LastXValue, myGazeDataPoints.get(index) + getRandom()), false, 10000);

                    if(index < myVoiceDataPoints.size())
                        mSeries3.appendData(new DataPoint(graph2LastXValue, myVoiceDataPoints.get(index) + getRandom()), false, 10000);

                    if(index < myPoseDataPoints.size())
                        mSeries4.appendData(new DataPoint(graph2LastXValue, myPoseDataPoints.get(index) + getRandom()), false, 10000);

                    index++;
                    graph2LastXValue += 1;
                    mHandler.postDelayed(this, 1000);
                }
                if(isDataReady == false)
                    mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mTimer2, 1000);
    }

    @Override
    public void onPause() {
        //mHandler.removeCallbacks(mTimer1);
        mHandler.removeCallbacks(mTimer2);
        super.onPause();
    }

    /*private DataPoint[] generateData() {
        int count = 30;
        DataPoint[] values = new DataPoint[count];
        for (int i=0; i<count; i++) {
            double x = i;
            double f = mRand.nextDouble()*0.15+0.3;
            double y = Math.sin(i*f+2) + mRand.nextDouble()*0.3;
            DataPoint v = new DataPoint(x, y);
            values[i] = v;
        }
        return values;
    }*/

    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
        //return mLastRandom += mRand.nextDouble()*0.5 - 0.25;
        return mRand.nextInt(25);
    }

    public void videoplay(){
        videov.setVideoPath(videoURL);
        videov.setMediaController(mediaController);
        mediaController.setAnchorView(videov);
        videov.start();

        //isDataReady = true;
        progressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Buffering video ...", true);
        videov.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
                isDataReady = true;
            }
        });
    }

}

/*graph2LastXValue += 1;
                    mSeries1.appendData(new DataPoint(graph2LastXValue, getRandom()), false, 1000);
                    mHandler.postDelayed(this, 1000);*/