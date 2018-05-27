package prosentation.example.com.prosentation.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

//AFRA
import com.github.mikephil.charting.formatter.PercentFormatter;
import android.util.Log;
import com.github.mikephil.charting.data.PieData;


import java.util.ArrayList;

import prosentation.example.com.prosentation.FormatRelated.CategoryAxisValueFormatter;
import prosentation.example.com.prosentation.R;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

/**
 * Created by ERKAN-PC on 7.05.2018.
 */

public class DataGenerator {
    private Context context;
    private static DataGenerator instance = null;

    public DataGenerator(Context context){
        this.context = context;
    }

    public static DataGenerator getInstance(Context context) {
        if(instance == null) {
            instance = new DataGenerator(context);
        }
        return instance;
    }

    public void setSummaryChart(HorizontalBarChart summaryChart, int faceScore, int gazeScore,
                                int poseScore, int voiceScore, Typeface mTfLight){

        mTfLight = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");

        // mChart.setHighlightEnabled(false);

        summaryChart.setDrawBarShadow(false);

        summaryChart.setDrawValueAboveBar(true);

        summaryChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        summaryChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        summaryChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // mChart.setDrawBarShadow(true);

        summaryChart.setDrawGridBackground(false);

        IAxisValueFormatter custom = new CategoryAxisValueFormatter(summaryChart);
        XAxis xl = summaryChart.getXAxis();

        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(mTfLight);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(10f);

        YAxis yl = summaryChart.getAxisLeft();
        yl.setTypeface(mTfLight);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        //yl.setInverted(true);

        YAxis yr = summaryChart.getAxisRight();
        yr.setTypeface(mTfLight);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        //yr.setInverted(true);


        setSummaryChartData(summaryChart, faceScore, gazeScore, poseScore,  voiceScore, mTfLight);
        summaryChart.setFitBars(true);
        summaryChart.animateX(2500);
        summaryChart.animateY(2500);

        Legend legend1 = summaryChart.getLegend();
        legend1.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend1.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend1.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend1.setDrawInside(false);
        legend1.setFormSize(8f);
        legend1.setXEntrySpace(4f);
    }

    public void setRealtimeChart(LineChart realtimeChart, ArrayList<Double> myFaceDataPoints, ArrayList<Double> myGazeDataPoints,
                                 ArrayList<Double> myPoseDataPoints, ArrayList<Double> myVoiceDataPoints){
        // enable description text
        realtimeChart.getDescription().setEnabled(true);

        // enable touch gestures
        realtimeChart.setTouchEnabled(true);

        // enable scaling and dragging
        realtimeChart.setDragEnabled(true);
        realtimeChart.setScaleEnabled(true);
        realtimeChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        realtimeChart.setPinchZoom(true);

        // set an alternative background color
        realtimeChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        realtimeChart.setData(data);

        // get the legend (only possible after setting data)
        Legend legend2 = realtimeChart.getLegend();

        // modify the legend ...
        legend2.setForm(Legend.LegendForm.LINE);
        //l.setTypeface(mTfLight);
        legend2.setTextColor(Color.WHITE);

        XAxis x2 = realtimeChart.getXAxis();
        //xl.setTypeface(mTfLight);
        x2.setTextColor(Color.WHITE);
        x2.setDrawGridLines(true);
        x2.setAvoidFirstLastClipping(true);
        x2.setAxisMinimum(-4f);
        x2.setEnabled(true);

        YAxis leftAxis = realtimeChart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = realtimeChart.getAxisRight();
        rightAxis.setEnabled(false);
        addPresentationEntry(realtimeChart, myFaceDataPoints, myGazeDataPoints, myPoseDataPoints, myVoiceDataPoints, -1);
        addPresentationEntry(realtimeChart, myFaceDataPoints, myGazeDataPoints, myPoseDataPoints, myVoiceDataPoints, -1);
        addPresentationEntry(realtimeChart, myFaceDataPoints, myGazeDataPoints, myPoseDataPoints, myVoiceDataPoints, -1);
        addPresentationEntry(realtimeChart, myFaceDataPoints, myGazeDataPoints, myPoseDataPoints, myVoiceDataPoints, -1);
        //addEntry(realtimeChart, -1);

    }

    public void setOverallChart(LineChart averageChart, ArrayList<Double> myFaceDataPoints, ArrayList<Double> myGazeDataPoints,
                                ArrayList<Double> myPoseDataPoints, ArrayList<Double> myVoiceDataPoints){
        // enable description text
        averageChart.getDescription().setEnabled(true);

        // enable touch gestures
        averageChart.setTouchEnabled(true);

        // enable scaling and dragging
        averageChart.setDragEnabled(true);
        averageChart.setScaleEnabled(true);
        averageChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        averageChart.setPinchZoom(true);

        // set an alternative background color
        averageChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        averageChart.setData(data);

        // get the legend (only possible after setting data)
        Legend legend2 = averageChart.getLegend();

        // modify the legend ...
        legend2.setForm(Legend.LegendForm.LINE);
        //l.setTypeface(mTfLight);
        legend2.setTextColor(Color.WHITE);

        XAxis x2 = averageChart.getXAxis();
        //xl.setTypeface(mTfLight);
        x2.setTextColor(Color.WHITE);
        x2.setDrawGridLines(false);
        x2.setAxisMinimum(-4f);
        x2.setAvoidFirstLastClipping(true);
        x2.setEnabled(true);

        YAxis leftAxis = averageChart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = averageChart.getAxisRight();
        rightAxis.setEnabled(false);

        addAverageEntry(averageChart, myFaceDataPoints, myGazeDataPoints, myPoseDataPoints, myVoiceDataPoints, -1);
        addAverageEntry(averageChart, myFaceDataPoints, myGazeDataPoints, myPoseDataPoints, myVoiceDataPoints, -1);
        addAverageEntry(averageChart, myFaceDataPoints, myGazeDataPoints, myPoseDataPoints, myVoiceDataPoints, -1);
        addAverageEntry(averageChart, myFaceDataPoints, myGazeDataPoints, myPoseDataPoints, myVoiceDataPoints, -1);
    }

    public void setCustomChart(LineChart lineChart, ArrayList<Double> dataPoints, int type){
        // enable description text
        lineChart.getDescription().setEnabled(true);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        // set an alternative background color
        lineChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        lineChart.setData(data);

        // get the legend (only possible after setting data)
        Legend legend2 = lineChart.getLegend();

        // modify the legend ...
        legend2.setForm(Legend.LegendForm.LINE);
        //l.setTypeface(mTfLight);
        legend2.setTextColor(Color.WHITE);

        XAxis x2 = lineChart.getXAxis();
        //xl.setTypeface(mTfLight);
        x2.setTextColor(Color.WHITE);
        x2.setDrawGridLines(false);
        x2.setAxisMinimum(-4f);
        x2.setAvoidFirstLastClipping(true);
        x2.setEnabled(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        addCustomEntry(lineChart, dataPoints, type, -1);
        addCustomEntry(lineChart, dataPoints, type, -1);
        addCustomEntry(lineChart, dataPoints, type, -1);
        addCustomEntry(lineChart, dataPoints, type, -1);
    }

    //AFRA
    public void setEmotionsDistr(PieChart emotionChart, ArrayList<Double> myFaceDataPoints){

        emotionChart.setUsePercentValues(true);
        ArrayList<PieEntry> data = new ArrayList<PieEntry>();

        data.add(new PieEntry( myFaceDataPoints.get(0).intValue(), "Happy"));
        data.add(new PieEntry( myFaceDataPoints.get(1).intValue(), "Sad"));
        data.add(new PieEntry( myFaceDataPoints.get(2).intValue(), "Surprise"));
        data.add(new PieEntry( myFaceDataPoints.get(3).intValue(), "Fear"));
        data.add(new PieEntry( myFaceDataPoints.get(4).intValue(), "Disgust"));
        data.add(new PieEntry( myFaceDataPoints.get(5).intValue(), "Anger"));

        emotionChart.setDrawSliceText(false);

        PieDataSet dataSet = new PieDataSet(data, "");


        PieData piedata = new PieData(dataSet);
        piedata.setValueFormatter(new PercentFormatter());

        final int[] MY_COLORS = {Color.rgb(192,0,0), Color.rgb(255,192,0),
                Color.rgb(127,127,127), Color.rgb(146,208,80), Color.rgb(0,176,80), Color.rgb(79,129,189)};
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for(int c: MY_COLORS) colors.add(c);

        dataSet.setColors(colors);

        piedata.setValueTextSize(13f);
        piedata.setValueTextColor(Color.DKGRAY);

        emotionChart.getDescription().setText("Overall Emotion Distribution");
        emotionChart.setDrawHoleEnabled(false);

        emotionChart.setData(piedata);


    }
    //ENDAFRA

    //AFRA
    public void setEmotionsSymbol(ImageView symbol,ImageView symbol1,ImageView symbol2,ImageView symbol3,ImageView symbol4,ImageView symbol5,String mood){

        Log.d("moodas",mood);
        if(mood.equalsIgnoreCase("happy")){
            symbol.setVisibility(View.VISIBLE);
        }
        if(mood.equalsIgnoreCase("sad")){
            symbol1.setVisibility(View.VISIBLE);
        }
        if(mood.equalsIgnoreCase("surprise")){
            symbol2.setVisibility(View.VISIBLE);
        }
        if(mood.equalsIgnoreCase("fear")){
            symbol3.setVisibility(View.VISIBLE);
        }
        if(mood.equalsIgnoreCase("disgust")){
            symbol4.setVisibility(View.VISIBLE);
        }
        if(mood.equalsIgnoreCase("anger")){
            symbol5.setVisibility(View.VISIBLE);
        }

    }

    //ENDAFRA

    public void setSummaryChartData(HorizontalBarChart summaryChart, int faceScore, int gazeScore,
                                    int poseScore, int voiceScore,Typeface mTfLight) {

        float barWidth = 8f;
        float spaceForBar = 15f;

        /*ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals4 = new ArrayList<BarEntry>();

        float val1 = (float) (Math.random() * 100);
        float val2 = (float) (Math.random() * 100);
        float val3 = (float) (Math.random() * 100);
        float val4 = (float) (Math.random() * 100);

        yVals1.add(new BarEntry(0 * spaceForBar, val1));
        yVals2.add(new BarEntry(1 * spaceForBar, val2));
        yVals3.add(new BarEntry(2 * spaceForBar, val3));
        yVals4.add(new BarEntry(3 * spaceForBar, val4));

        BarDataSet set1 = new BarDataSet(yVals1, "Face");
        set1.setColor(Color.rgb(104, 241, 175));
        BarDataSet set2 = new BarDataSet(yVals2, "Gaze");
        set2.setColor(Color.rgb(164, 228, 251));
        BarDataSet set3 = new BarDataSet(yVals3, "Pose");
        set3.setColor(Color.rgb(242, 247, 158));
        BarDataSet set4 = new BarDataSet(yVals4, "Voice");
        set4.setColor(Color.rgb(255, 102, 0));

        set1.setValues(yVals1);
        set2.setValues(yVals2);
        set3.setValues(yVals3);
        set4.setValues(yVals4);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);
        dataSets.add(set4);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(mTfLight);
        data.setBarWidth(barWidth);

        summaryChart.setData(data);
        summaryChart.getData().notifyDataChanged();
        summaryChart.notifyDataSetChanged();*/

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Face");
        labels.add("Gaze");
        labels.add("Pose");
        labels.add("Voice");



        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        float val1 = (float) (Math.random() * 100);
        float val2 = (float) (Math.random() * 100);
        float val3 = (float) (Math.random() * 100);
        float val4 = (float) (Math.random() * 100);

        //afra
        yVals1.add(new BarEntry(0, faceScore,"face"));
        yVals1.add(new BarEntry(1, gazeScore,"gaze"));
        yVals1.add(new BarEntry(2, poseScore,"pose"));
        yVals1.add(new BarEntry(3, voiceScore,"voice"));
        XAxis xl = summaryChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        CategoryBarChartXaxisFormatter xaxisFormatter = new CategoryBarChartXaxisFormatter(labels);
        xl.setValueFormatter(xaxisFormatter);
        xl.setGranularity(1);

        BarDataSet set1;
        if (summaryChart.getData() != null && summaryChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) summaryChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            summaryChart.getData().notifyDataChanged();
            summaryChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Analysis Categories");

            set1.setDrawIcons(false);

            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            summaryChart.setData(data);
        }
    }

    public void addCustomEntry(LineChart customChart, ArrayList<Double> customDataPoints, int type, int index) {

        LineData data = customChart.getData();


        if (data != null) {
            ILineDataSet customeSet = data.getDataSetByIndex(0);

            if (customeSet == null) {
                /*LineDataSet set = new LineDataSet(null, "Face");
                set.setHighlightEnabled(true);
                set.setDrawHighlightIndicators(true);
                set.setHighLightColor(context.getResources().getColor(R.color.colorPrimary));
                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                set.setColor(ColorTemplate.rgb("#ff0000"));
                set.setCircleColor(Color.WHITE);
                set.setLineWidth(2f);
                set.setCircleRadius(1f);
                set.setFillAlpha(65);
                set.setFillColor(ColorTemplate.getHoloBlue());
                //set.setHighLightColor(Color.rgb(244, 117, 117));
                set.setValueTextColor(Color.WHITE);
                set.setValueTextSize(9f);
                set.setDrawValues(false);
                customeSet = set;*/
                if(type == 0){
                    customeSet = createSetFace();
                }
                if(type == 1){
                    customeSet = createSetGaze();
                }
                if(type == 2){
                    customeSet = createSetPose();
                }
                if(type == 3){
                    customeSet = createSetVoice();
                }
                data.addDataSet(customeSet);
            }
            //myFaceDataPoints
            //Entry faceEntry = new Entry(faceSet.getEntryCount(), (float) (Math.random() * 40) + 30f);
            Entry customEntry;
            if(index < 0) {
                customEntry = new Entry(customeSet.getEntryCount(), 0);
            }
            else {
                customEntry = new Entry(customeSet.getEntryCount(), (float) customDataPoints.get(index).doubleValue());
            }

            data.addEntry(customEntry, 0);
            data.notifyDataChanged();
            customChart.notifyDataSetChanged();

            // limit the number of visible entries
            customChart.setVisibleXRangeMaximum(8);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            customChart.moveViewToX(data.getEntryCount());
        }
    }

    public void addAverageEntry(LineChart averageChart, ArrayList<Double> myFaceDataPoints, ArrayList<Double> myGazeDataPoints,
                                ArrayList<Double> myPoseDataPoints, ArrayList<Double> myVoiceDataPoints, int index) {

        LineData averageData = averageChart.getData();


        if (averageData != null) {
            ILineDataSet customeSet = averageData.getDataSetByIndex(0);

            if (customeSet == null) {
                /*LineDataSet set = new LineDataSet(null, "Face");
                set.setHighlightEnabled(true);
                set.setDrawHighlightIndicators(true);
                set.setHighLightColor(context.getResources().getColor(R.color.colorPrimary));
                set.setAxisDependency(YAxis.AxisDependency.LEFT);
                set.setColor(ColorTemplate.rgb("#ff0000"));
                set.setCircleColor(Color.WHITE);
                set.setLineWidth(2f);
                set.setCircleRadius(1f);
                set.setFillAlpha(65);
                set.setFillColor(ColorTemplate.getHoloBlue());
                //set.setHighLightColor(Color.rgb(244, 117, 117));
                set.setValueTextColor(Color.WHITE);
                set.setValueTextSize(9f);
                set.setDrawValues(false);
                customeSet = set;*/
                customeSet = createSetFace();
                averageData.addDataSet(customeSet);
            }
            //myFaceDataPoints
            //Entry faceEntry = new Entry(faceSet.getEntryCount(), (float) (Math.random() * 40) + 30f);
            Entry averageEntry;
            if(index < 0) {
                averageEntry = new Entry(customeSet.getEntryCount(), 0);
            }
            else {
                double avg = (myFaceDataPoints.get(index) + myGazeDataPoints.get(index)
                        + myPoseDataPoints.get(index) + myVoiceDataPoints.get(index))/4;

                averageEntry = new Entry(customeSet.getEntryCount(), (float) (avg));
            }

            averageData.addEntry(averageEntry, 0);
            averageData.notifyDataChanged();
            averageChart.notifyDataSetChanged();

            // limit the number of visible entries
            averageChart.setVisibleXRangeMaximum(8);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            averageChart.moveViewToX(averageData.getEntryCount());
        }
    }

    public void addPresentationEntry(LineChart realtimeChart, ArrayList<Double> myFaceDataPoints, ArrayList<Double> myGazeDataPoints,
                                     ArrayList<Double> myPoseDataPoints, ArrayList<Double> myVoiceDataPoints, int index){
        LineData data = realtimeChart.getData();

        if (data != null) {

            ILineDataSet faceSet = data.getDataSetByIndex(0);
            ILineDataSet gazeSet = data.getDataSetByIndex(1);
            ILineDataSet voiceSet = data.getDataSetByIndex(2);
            ILineDataSet poseSet = data.getDataSetByIndex(3);
            // set.addEntry(...); // can be called as well

            if (faceSet == null) {
                faceSet = createSetFace();
                data.addDataSet(faceSet);
            }
            if (gazeSet == null) {
                gazeSet = createSetGaze();
                data.addDataSet(gazeSet);
            }
            if (poseSet == null) {
                poseSet = createSetPose();
                data.addDataSet(poseSet);
            }
            if (voiceSet == null) {
                voiceSet = createSetVoice();
                data.addDataSet(voiceSet);
            }

            //Entry gazeEntry = new Entry(gazeSet.getEntryCount(), (float) (Math.random() * 40) + 30f,getResources().getDrawable(R.drawable.star));
            Entry faceEntry;
            Entry gazeEntry;
            Entry poseEntry;
            Entry voiceEntry;
            if (index < 0) {
                faceEntry = new Entry(faceSet.getEntryCount(), 0);
                gazeEntry = new Entry(gazeSet.getEntryCount(), 0);
                poseEntry = new Entry(poseSet.getEntryCount(), 0);
                voiceEntry = new Entry(voiceSet.getEntryCount(), 0);
            }else{
                faceEntry = new Entry(faceSet.getEntryCount(), (float) myFaceDataPoints.get(index).floatValue());
                gazeEntry = new Entry(gazeSet.getEntryCount(), (float) myGazeDataPoints.get(index).floatValue());
                poseEntry = new Entry(poseSet.getEntryCount(), (float) myPoseDataPoints.get(index).floatValue());
                voiceEntry = new Entry(voiceSet.getEntryCount(), (float) myVoiceDataPoints.get(index).floatValue());
            }


            data.addEntry(faceEntry, 0);
            data.addEntry(gazeEntry, 1);
            data.addEntry(poseEntry, 2);
            data.addEntry(voiceEntry, 3);

            //data.addEntry(new Entry(), (float) (Math.random() * 40) + 30f), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            realtimeChart.notifyDataSetChanged();

            // limit the number of visible entries
            realtimeChart.setVisibleXRangeMaximum(8);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            realtimeChart.moveViewToX(data.getEntryCount());
        }
    }

    public LineDataSet createSetOverall() {

        LineDataSet set = new LineDataSet(null, "Overall");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.rgb("#ff0000"));
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(1f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(context.getResources().getColor(R.color.blue));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    public LineDataSet createSetFace() {

        LineDataSet set = new LineDataSet(null, "Face");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.rgb("#ff0000"));
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(1f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(context.getResources().getColor(R.color.blue));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
    public LineDataSet createSetGaze() {

        LineDataSet set = new LineDataSet(null, "Gaze");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.rgb("#0000ff"));
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(1f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(context.getResources().getColor(R.color.blue));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
    public LineDataSet createSetVoice() {

        LineDataSet set = new LineDataSet(null, "Voice");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.rgb("#00ff00"));
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(1f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(context.getResources().getColor(R.color.blue));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
    public LineDataSet createSetPose() {

        LineDataSet set = new LineDataSet(null, "Pose");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.rgb("#ffffff"));
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(1f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(context.getResources().getColor(R.color.blue));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    /*
    public LineData generateDataLine(int cnt) {

        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            e1.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d1 = new LineDataSet(e1, "New DataSet " + cnt + ", (1)");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<Entry> e2 = new ArrayList<Entry>();

        for (int i = 0; i < 12; i++) {
            e2.add(new Entry(i, e1.get(i).getY() - 30));
        }

        LineDataSet d2 = new LineDataSet(e2, "New DataSet " + cnt + ", (2)");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);

        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        sets.add(d1);
        sets.add(d2);

        LineData cd = new LineData(sets);
        return cd;
    }

    public BarData generateDataBar(int cnt) {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();

        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, (int) (Math.random() * 70) + 30));
        }

        BarDataSet d = new BarDataSet(entries, "New DataSet " + cnt);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setHighLightAlpha(255);

        BarData cd = new BarData(d);
        cd.setBarWidth(0.9f);
        return cd;
    }

    public PieData generateDataPie(int cnt) {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        for (int i = 0; i < 4; i++) {
            entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "Quarter " + (i+1)));
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData cd = new PieData(d);
        return cd;
    }*/


    public class CategoryBarChartXaxisFormatter implements IAxisValueFormatter {

        ArrayList<String> mValues;

        public CategoryBarChartXaxisFormatter(ArrayList<String> values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {

            int val = (int) value;
            String label = "";
            if (val >= 0 && val < mValues.size()) {
                label = mValues.get(val);
            } else {
                label = "";
            }
            return label;
        }
    }

}