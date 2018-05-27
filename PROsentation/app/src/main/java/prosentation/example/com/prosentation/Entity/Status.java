package prosentation.example.com.prosentation.Entity;

/**
 * Created by ERKAN-PC on 7.05.2018.
 */

public class Status {

    private int faceStatus;
    private int gazeStatus;
    private int poseStatus;
    private int voiceStatus;

    public Status(){

    }

    public Status(int faceStatus, int gazeStatus, int poseStatus, int voiceStatus){
        this.faceStatus = faceStatus;
        this.gazeStatus = gazeStatus;
        this.poseStatus = poseStatus;
        this.voiceStatus = voiceStatus;
    }

    public int getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(int faceStatus) {
        this.faceStatus = faceStatus;
    }

    public int getGazeStatus() {
        return gazeStatus;
    }

    public void setGazeStatus(int gazeStatus) {
        this.gazeStatus = gazeStatus;
    }

    public int getPoseStatus() {
        return poseStatus;
    }

    public void setPoseStatus(int poseStatus) {
        this.poseStatus = poseStatus;
    }

    public int getVoiceStatus() {
        return voiceStatus;
    }

    public void setVoiceStatus(int voiceStatus) {
        this.voiceStatus = voiceStatus;
    }
}
