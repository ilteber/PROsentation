package prosentation.example.com.prosentation.Entity;

/**
 * Created by ERKAN-PC on 16.03.2018.
 */

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;

import java.util.HashMap;

/**
 * Created by ERKAN-PC on 16.03.2018.
 */

public class Video {
    private int Id;
    private String name;
    private String videoURL;
    private int numOfContents;
    private String thumbnailURL;

    public Video() {
    }

    public Video(int Id, String name, String videoURL, int numOfContents, String thumbnailURL) {
        this.Id = Id;
        this.name = name;
        this.videoURL = videoURL;
        this.numOfContents = numOfContents;
        this.thumbnailURL = thumbnailURL;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public int getNumOfContents() {
        return numOfContents;
    }

    public void setNumOfContents(int numOfContents) {
        this.numOfContents = numOfContents;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}

