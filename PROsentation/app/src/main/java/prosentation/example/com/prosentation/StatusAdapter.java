package prosentation.example.com.prosentation;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.ArrayList;
import java.util.List;

import prosentation.example.com.prosentation.DynamoDB.DynamoDBManager;
import prosentation.example.com.prosentation.Entity.Status;

/**
 * Created by ERKAN-PC on 7.05.2018.
 */

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyViewHolder>{
    private List<Status> statusList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ArcProgress faceStatus, gazeStatus, poseStatus, voiceStatus;

        public MyViewHolder(View view){
            super(view);
            faceStatus = (ArcProgress) view.findViewById(R.id.arc_progress_face);
            gazeStatus = (ArcProgress) view.findViewById(R.id.arc_progress_gaze);
            poseStatus = (ArcProgress) view.findViewById(R.id.arc_progress_pose);
            voiceStatus = (ArcProgress) view.findViewById(R.id.arc_progress_voice);
        }
    }

    public StatusAdapter(List<Status> statusList) {
        this.statusList = statusList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Status status = statusList.get(position);
        holder.faceStatus.setProgress(status.getFaceStatus());
        holder.gazeStatus.setProgress(status.getGazeStatus());
        holder.poseStatus.setProgress(status.getPoseStatus());
        holder.voiceStatus.setProgress(status.getVoiceStatus());
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }
}
