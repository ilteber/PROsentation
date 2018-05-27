package prosentation.example.com.prosentation;

/**
 * Created by ERKAN-PC on 16.03.2018.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import prosentation.example.com.prosentation.Entity.Video;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder>{
    private Context mContext;
    private List<Video> albumList;
    private final OnItemClickListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }

        //İşe yaramıyor sonra sil bunu
        @Override
        public void onClick(View view) {
            int position  =   getAdapterPosition();
            Log.w("Listener Clicked", "Selected: " + position);
        }

        public void bind(final Video video, final OnItemClickListener listener) {
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(video);
                }
            });
        }
    }


    public VideoAdapter(Context mContext, List<Video> albumList, OnItemClickListener listener) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Video video = albumList.get(position);
        holder.title.setText(video.getId() + ".mp4");
        holder.count.setText(video.getNumOfContents() + " contents");
        holder.bind(albumList.get(position), listener);

        // loading thumnail using Glide library
        //Glide.with(mContext).load(video.getThumbnail()).into(holder.thumbnail);

        //String mUri = "http://ec2-18-222-71-24.us-east-2.compute.amazonaws.com/erkan/unprocessed/album10.png";
        String mUri = video.getThumbnailURL();

        Bitmap thumbnailFromLocalStorage = ThumbnailUtils.createVideoThumbnail(getFilePath(video.getId() + "_unprocessed.MP4"), MediaStore.Images.Thumbnails.MINI_KIND);
        Log.d("path: ", getFilePath(video.getId() + "_unprocessed.MP4"));
        holder.thumbnail.setImageBitmap(thumbnailFromLocalStorage);

        //If you stream, do the thumbnail set operation as shown in below
        //new DownloadThumbnailTask((ImageView)holder.thumbnail).execute(mUri);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    private class DownloadThumbnailTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadThumbnailTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_video, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    private String getFilePath(String filename) {
        File direc = mContext.getExternalFilesDir(null);
        String absoPath = direc.getAbsolutePath();

        String path = absoPath + "/" + filename;
        return path;
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
