package prosentation.example.com.prosentation.Entity;

/**
 * Created by ERKAN-PC on 1.05.2018.
 */

public class Presentation {
//    private String mam
    private int overall;
    private int face;
    private int gaze;
    private int pose;
    private int voice;

    public Presentation() {
    }

    public Presentation(int overall, int face, int gaze, int pose, int voice){
        this.overall = overall;
        this.face = face;
        this.gaze = gaze;
        this.pose = pose;
        this.voice = voice;
    }

    public int getOverall() {
        return overall;
    }

    public void setOverall(int overall) {
        this.overall = overall;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public int getGaze() {
        return gaze;
    }

    public void setGaze(int gaze) {
        this.gaze = gaze;
    }

    public int getPose() {
        return pose;
    }

    public void setPose(int pose) {
        this.pose = pose;
    }

    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }
}
