package prosentation.example.com.prosentation;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by ilteber on 13.05.2018.
 */

public class GazeView extends View {
    private int height, width = 0;
    private int videoId;
    private int totalCount;
    private Paint mPaint;
    private int fontSize;
    private boolean isInit = false;
    ArrayList<Float> xGazeData = new ArrayList<Float>();
    ArrayList<Float> yGazeData = new ArrayList<Float>();

    public GazeView (Context context){
        super(context);
    }
    public GazeView (Context context, AttributeSet attrs){
        super(context,attrs);

    }
    public GazeView (Context context, AttributeSet attrs,int defStyleArr){
        super(context,attrs,defStyleArr);

    }

    public void inits(){
        this.height = getHeight();
        this.width = getWidth();
        this.videoId = getMinimumWidth();
        this.fontSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP
                ,13
                ,getResources().getDisplayMetrics());
        mPaint = new Paint();
        isInit = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!isInit){
            inits();
        }
        mPaint.setColor(Color.BLACK);
        int circleRadius = 250;
        int circleX = width/2;
        int circleY = height/2;
        canvas.drawCircle(circleX,circleY,circleRadius,mPaint);
        mPaint.setColor(Color.WHITE);
        getGazeData();

        if((xGazeData.size()==0||yGazeData.size()==0)&&xGazeData.size()!= yGazeData.size()){
            throw new ArithmeticException("xGaze yGaze Lists are empty");
        }
        for(int i = 0; i<xGazeData.size(); i++){
            canvas.drawCircle(circleX+circleRadius*xGazeData.get(i),
                    circleY-circleRadius*yGazeData.get(i),
                    3,mPaint);
        }
        postInvalidateDelayed(1000);
    }
    protected void getGazeData(){
        File direc = getContext().getExternalFilesDir(null);

        String absoPath = direc.getAbsolutePath();
        Log.d("filen:",absoPath);

        String gaze_filename = absoPath + "/" + Integer.toString(videoId)+ "_gaze_angle.txt";
        File gaze_file = new File(gaze_filename);
        Log.d("filename:",gaze_filename);


        try{
            Scanner scan = new Scanner(gaze_file);
            //scan.useDelimiter(",");
            while(scan.hasNext()){
//                Log.d();
                String asd = scan.nextLine();
                String[] arr = asd.split(",");

                float xtemp=Float.parseFloat(arr[0]);
                Log.d("Line:",xtemp+"");
                float ytemp=Float.parseFloat(arr[1]);
                Log.d("Line:",xtemp+""+ytemp);
                xGazeData.add(xtemp);
                yGazeData.add(ytemp);


            }
        }catch(IOException e){
            e.printStackTrace();
        }
        Log.d("X-Y Size : ","x: "+xGazeData.size() + " y:"+yGazeData.size());
    }
    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

}