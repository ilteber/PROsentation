package prosentation.example.com.prosentation;

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
    private String name;
    private String url;
    private int numOfContents;
    private int thumbnail;

    public Video() {
    }

    public Video(String name, int numOfContents, int thumbnail) {
        this.name = name;
        this.numOfContents = numOfContents;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfContents() {
        return numOfContents;
    }

    public void setNumOfContents(int numOfContents) {
        this.numOfContents = numOfContents;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }
}

