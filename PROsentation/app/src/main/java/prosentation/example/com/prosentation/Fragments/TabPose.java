package prosentation.example.com.prosentation.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import prosentation.example.com.prosentation.DynamoDB.DynamoDBManager;
import prosentation.example.com.prosentation.Entity.Presentation;
import prosentation.example.com.prosentation.FormatRelated.CustomMarkerView;
import prosentation.example.com.prosentation.R;

/**
 * Created by ilgin on 01.05.2018.
 */

public class TabPose extends Fragment implements OnChartValueSelectedListener {

    View view;

    private static DynamoDBManager managerClass = DynamoDBManager.getInstance();
    private ArrayList<Presentation> presentations;
    private TextView text_username;
    private TextView text_user_email;
    private TextView text_face;
    private TextView text_gaze;
    private TextView text_pose;
    private TextView text_voice;

    //user details
    private String username;
    private String email;
    private String password;


    //chart related
    private LineChart progChart;
    private Typeface mTf;

    public TabPose(){

    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_userprofile_posetab,container, false);

        username = getActivity().getIntent().getStringExtra("USERNAME");
        email = getActivity().getIntent().getStringExtra("EMAIL");
        password = getActivity().getIntent().getStringExtra("PASSWORD");

        progChart = (LineChart)view.findViewById(R.id.chart_progress);
        CustomMarkerView mv = new CustomMarkerView (getActivity(), R.layout.tvcontentview);
        mTf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");

        //use this
        presentations = new ArrayList<>();

        try {
            new MyPresentationsGetterBackground(presentations).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        setProgChart();
        //presentations'ı kullan

        return view;

    }

    class MyPresentationsGetterBackground extends AsyncTask<Void, Void, Void> {
        ArrayList<Presentation> myPresentations;
        public MyPresentationsGetterBackground(ArrayList<Presentation> myPresentations){
            this.myPresentations = myPresentations;
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.d("DB operations", "DB operations");
            managerClass.getAllPresentationScores(getActivity(), username, myPresentations);
            Log.d("Presentations.size()", myPresentations.size() + "");
            return null;
        }
    }

    public void setProgChart(){
        ///////////////////////////
        progChart.getDescription().setEnabled(false);
        progChart.setOnChartValueSelectedListener(this);
        progChart.setDrawGridBackground(false);


        progChart.setTouchEnabled(true);
        progChart.setDragEnabled(true);
        progChart.setScaleEnabled(true);
        progChart.setPinchZoom(true);
        //progChart.setMarkerView(mv);

        XAxis xAxis = progChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);

        YAxis leftAxis = progChart.getAxisLeft();
        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5, false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = progChart.getAxisRight();
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(5, false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        // set data

        //progChart.setData((LineData) generateDataLine(5));
        progChart.setData((LineData) setTotalResults());

        // do not forget to refresh the chart
        // holder.chart.invalidate();
        progChart.animateX(750);
        progChart.animateY(750);
    }

    private LineData generateDataLine(int cnt) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            e1.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d1 = new LineDataSet(e1, "Presentations in order");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);

        LineData cd = new LineData(sets);
        return cd;
    }

    public LineData setTotalResults(){
        /*ArrayList<Entry> e1 = new ArrayList<Entry>();
        int size = presentations.size();
        for(int i = 0; i < 5; i++){
            //Presentation presentation = presentations.get(i);

            //e1.add(new Entry(i, (int) presentation.getPose()));
            Random rand = new Random();

            int  n = rand.nextInt(60) + 40;

            e1.add(new Entry(i, n));
        }

        LineDataSet d1 = new LineDataSet(e1, "Presentations in order");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);

        LineData cd = new LineData(sets);
        return cd;*/

        final HashMap<Integer, String> numMap = new HashMap<>();

        progChart.getXAxis().setLabelCount(presentations.size(),true);

        for(int i = 0; i < presentations.size(); i++){
            int value = (i+1);
            numMap.put(i, "" + value);
        }

        progChart.getAxisLeft().setAxisMaxValue(100);
        progChart.getAxisRight().setAxisMaxValue(100);

        List<Entry> entries1 = new ArrayList<Entry>();

        for(int i = 0; i < presentations.size(); i++){
            entries1.add(new Entry(i, presentations.get(i).getPose()));
        }

        LineDataSet dataSet = new LineDataSet(entries1, "Presentation Scores by name");


        LineData data = new LineData(dataSet);

        XAxis xAxis = progChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return numMap.get((int)value);
            }

            //@Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        progChart.setData(data);
        progChart.invalidate();

        return data;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {

    }


}
