package prosentation.example.com.prosentation.DynamoDB;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by lenovo on 6.2.2018.
 */

@DynamoDBTable(tableName = "videos")
public class VideoMapper {
    int Id;
    int type;
    int length;
    String name;
    int status;
    String username;
    int published;
    int overall;
    int face;
    int gaze;
    int pose;
    int voice;
    int faceStatus;
    int gazeStatus;
    int poseStatus;
    int voiceStatus;

    @DynamoDBHashKey(attributeName = "Id")
    @DynamoDBAttribute(attributeName = "Id")
    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    @DynamoDBRangeKey(attributeName = "type")
    @DynamoDBAttribute(attributeName = "type")
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @DynamoDBAttribute(attributeName = "length")
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @DynamoDBAttribute(attributeName = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBAttribute(attributeName = "published")
    public int getPublished() {
        return published;
    }

    public void setPublished(int published) {
        this.published = published;
    }

    @DynamoDBAttribute(attributeName = "overall")
    public int getOverall() {
        return overall;
    }

    public void setOverall(int overall) {
        this.overall = overall;
    }

    @DynamoDBAttribute(attributeName = "face")
    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    @DynamoDBAttribute(attributeName = "gaze")
    public int getGaze() {
        return gaze;
    }

    public void setGaze(int gaze) {
        this.gaze = gaze;
    }

    @DynamoDBAttribute(attributeName = "pose")
    public int getPose() {
        return pose;
    }

    public void setPose(int pose) {
        this.pose = pose;
    }

    @DynamoDBAttribute(attributeName = "voice")
    public int getVoice() {
        return voice;
    }

    public void setVoice(int voice) {
        this.voice = voice;
    }

    @DynamoDBAttribute(attributeName = "faceStatus")
    public int getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(int faceStatus) {
        this.faceStatus = faceStatus;
    }

    @DynamoDBAttribute(attributeName = "gazeStatus")
    public int getGazeStatus() {
        return gazeStatus;
    }

    public void setGazeStatus(int gazeStatus) {
        this.gazeStatus = gazeStatus;
    }

    @DynamoDBAttribute(attributeName = "poseStatus")
    public int getPoseStatus() {
        return poseStatus;
    }

    public void setPoseStatus(int poseStatus) {
        this.poseStatus = poseStatus;
    }

    @DynamoDBAttribute(attributeName = "voiceStatus")
    public int getVoiceStatus() {
        return voiceStatus;
    }

    public void setVoiceStatus(int voiceStatus) {
        this.voiceStatus = voiceStatus;
    }
}
