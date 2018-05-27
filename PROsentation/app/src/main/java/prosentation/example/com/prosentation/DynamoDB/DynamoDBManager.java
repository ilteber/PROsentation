package prosentation.example.com.prosentation.DynamoDB;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import prosentation.example.com.prosentation.Entity.Presentation;
import prosentation.example.com.prosentation.Entity.Status;
import prosentation.example.com.prosentation.Entity.User;
import prosentation.example.com.prosentation.Entity.Video;

/**
 * Created by lenovo on 6.2.2018.
 */

public class DynamoDBManager {
    private static DynamoDBManager instance = null;
    private static AmazonDynamoDBClient dynamoDBClient;
    private static DynamoDBMapper dynamoDBMapper;
    private static final String AWSIP = "ec2-52-17-110-82.eu-west-1.compute.amazonaws.com";
    private static final String IDENTITYPOOLID = "us-east-2:0bb1ea3d-2a6a-4df9-a018-eda854cac306";
    private static final Regions REGION = Regions.US_EAST_2;

    CognitoCachingCredentialsProvider credentialsProvider = null;
    CognitoSyncManager syncManager = null;

    public static DynamoDBManager getInstance() {
        if(instance == null) {
            instance = new DynamoDBManager();
        }
        return instance;
    }
    //"us-east-2:c7b46579-b6b8-467e-87b0-e8a154abf684", // Identity pool ID
    public CognitoCachingCredentialsProvider getCredentials(Context context){
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                IDENTITYPOOLID, // Identity pool ID
                REGION // Region
        );
        syncManager = new CognitoSyncManager(context, Regions.US_EAST_2, credentialsProvider);
        Dataset dataset = syncManager.openOrCreateDataset("myDataset");
        dataset.put("myKey", "myValue");
        dataset.synchronize(new DefaultSyncCallback());

        return credentialsProvider;
    }

    public DynamoDBMapper initDynamoClient(CognitoCachingCredentialsProvider credentialsProvider){
        if(dynamoDBClient == null){
            dynamoDBClient = new AmazonDynamoDBClient(credentialsProvider);
            dynamoDBClient.setRegion(Region.getRegion(Regions.US_EAST_2));
            dynamoDBMapper = new DynamoDBMapper(dynamoDBClient);
        }
        return dynamoDBMapper;
    }

    public void insertVideoToDB(Context context, int id, int type, int length, String name, int status, String username, int published){
        CognitoCachingCredentialsProvider credentialsProvider = getCredentials(context);
        VideoMapper mapperClass = new VideoMapper();
        mapperClass.setId(id);
        mapperClass.setType(type);
        mapperClass.setLength(length);
        mapperClass.setName(name);
        mapperClass.setStatus(status);
        mapperClass.setUsername(username);
        mapperClass.setPublished(published);
        mapperClass.setOverall(0);
        mapperClass.setFace(0);
        mapperClass.setGaze(0);
        mapperClass.setPose(0);
        mapperClass.setVoice(0);
        mapperClass.setFaceStatus(25);
        mapperClass.setGazeStatus(25);
        mapperClass.setPoseStatus(25);
        mapperClass.setVoiceStatus(25);

        if(credentialsProvider != null){
            DynamoDBMapper dynamoDBMapper = initDynamoClient(credentialsProvider);
            dynamoDBMapper.save(mapperClass);
        }else{
            Log.d("Unsuccessful Insert", "Unsuccessful Insert");
        }
        Log.d("Successful Insert", "Successful Insert");
    }

    public boolean insertUserToDB(Context context, String username, String email, String password){
        CognitoCachingCredentialsProvider credentialsProvider = getCredentials(context);
        UserMapper mapperClass = new UserMapper();
        mapperClass.setUsername(username);
        mapperClass.setEmail(email);
        mapperClass.setPassword(password);

        if(credentialsProvider != null){
            DynamoDBMapper dynamoDBMapper = initDynamoClient(credentialsProvider);
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(username));
            //eav.put(":val2", new AttributeValue().withS("VideoMapper"));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("username = :val1").withExpressionAttributeValues(eav);
            List<UserMapper> scanResult = dynamoDBMapper.scan(UserMapper.class, scanExpression);
            if(scanResult.size() == 0){
                dynamoDBMapper.save(mapperClass);
                return true;
            }
            else{
                return false;
            }
        }
        else{
            Log.d("Unsuccessful Insert", "Unsuccessful Insert");
            return false;
        }
    }

    public boolean loginToSystem(Context context, String username, String password){
        CognitoCachingCredentialsProvider credentialsProvider = getCredentials(context);

        if(credentialsProvider != null){
            DynamoDBMapper dynamoDBMapper = initDynamoClient(credentialsProvider);
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(username));
            eav.put(":val2", new AttributeValue().withS(password));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("username = :val1 and password = :val2").withExpressionAttributeValues(eav);
            List<UserMapper> scanResult = dynamoDBMapper.scan(UserMapper.class, scanExpression);
            if(scanResult.size() == 1){
                return true;
            }
            else{
                return false;
            }
        }
        else{
            Log.d("Unsuccessful Insert", "Unsuccessful Insert");
            return false;
        }
    }

    public boolean getMyEmail(Context context, String username, String password, User user){
        CognitoCachingCredentialsProvider credentialsProvider = getCredentials(context);

        if(credentialsProvider != null){
            DynamoDBMapper dynamoDBMapper = initDynamoClient(credentialsProvider);
            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(username));
            eav.put(":val2", new AttributeValue().withS(password));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("username = :val1 and password = :val2").withExpressionAttributeValues(eav);
            List<UserMapper> scanResult = dynamoDBMapper.scan(UserMapper.class, scanExpression);
            if(scanResult.size() == 1){
                user.setEmail(scanResult.get(0).getEmail());
                return true;
            }
            else{
                return false;
            }
        }
        else{
            Log.d("Unsuccessful Insert", "Unsuccessful Insert");
            return false;
        }
    }

    public void deleteEntryInDB(Context context, int id, int type){
        CognitoCachingCredentialsProvider credentialsProvider = getCredentials(context);

        if(credentialsProvider != null){
            DynamoDBMapper dynamoDBMapper = initDynamoClient(credentialsProvider);
            VideoMapper mapperClass = new VideoMapper();
            mapperClass.setId(id);
            mapperClass.setType(type);

            dynamoDBMapper.delete(mapperClass);
        }else{
            Log.d("Unsuccessful delete", "Unsuccessful delete");
        }
        Log.d("Successful delete", "Successful delete");
    }
    public int getProcessStatus(int Id, Context context){
        credentialsProvider = getCredentials(context);

        if(credentialsProvider != null){
            DynamoDBMapper dynamoDBMapper = initDynamoClient(credentialsProvider);
            VideoMapper video = dynamoDBMapper.load(VideoMapper.class, Id, 1);

            if(video != null){
                return video.getStatus();
            }else{
                Log.d("NULL: ", " " + "NULL");
                return -1;
            }
        }else{
            Log.d("Unsuccessful", "Unsuccessful");
            return -1;
        }
    }

    public int getNewVideoId(Context context){
        credentialsProvider = getCredentials(context);

        if(credentialsProvider != null){
            DynamoDBMapper dynamoDBMapper = initDynamoClient(credentialsProvider);
            //VideoMapper video = dynamoDBMapper.load(VideoMapper.class, "1");

            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withN("1"));
            //eav.put(":val2", new AttributeValue().withS("VideoMapper"));

            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("Id >= :val1").withExpressionAttributeValues(eav);

            List<VideoMapper> scanResult = dynamoDBMapper.scan(VideoMapper.class, scanExpression);

            int max = 0;
            if(scanResult.size() != 0 ) {
                max = scanResult.get(0).getId();

                for (VideoMapper video : scanResult) {
                    if(video.getId() > max){
                        max = video.getId();
                    }
                }
            }
            Log.d("MAX:", "" + max);
            return max+1;
        }else{
            Log.d("Unsuccessful", "Unsuccessful");
            return -1;
        }
        //Log.d("Successful", "Successful");
    }

    public void getMyVideos(Context context, String myUsername, ArrayList<Video> myVideos){
        credentialsProvider = getCredentials(context);

        if(credentialsProvider != null){
            DynamoDBMapper dynamoDBMapper = initDynamoClient(credentialsProvider);
            //VideoMapper video = dynamoDBMapper.load(VideoMapper.class, "1");

            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(myUsername));
            eav.put(":val2", new AttributeValue().withN("1"));

            // dynamdbdeki reserve keylerle keyimiz aynı olunca, keyimiz için placeholder olarak
            // expressionattributenames kullanıyoruz, yani "#my_type", "type" için bir placeholder
            Map<String, String> eav2 = new HashMap<String, String>();
            eav2.put("#my_type", "type");
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("username = :val1 and #my_type = :val2")
                    .withExpressionAttributeValues(eav)
                    .withExpressionAttributeNames(eav2);

            List<VideoMapper> scanResult = dynamoDBMapper.scan(VideoMapper.class, scanExpression);

            if(scanResult.size() != 0 ) {
                int i = 0;
                for (VideoMapper video : scanResult) {
                    Video curVideo = new Video();
                    curVideo.setId(video.getId());
                    curVideo.setName(video.getName());
                    curVideo.setNumOfContents(video.getLength());
                    curVideo.setThumbnailURL("http://" + AWSIP + "/videos/" + video.getId() + ".png");
                    curVideo.setVideoURL("http://" + AWSIP + "/videos/" + video.getId() + ".mp4");
                    myVideos.add(curVideo);
                    Log.d("ID: ", "" + curVideo.getThumbnailURL());
                    i++;
                }
            }
        }else{
            Log.d("Unsuccessful", "Unsuccessful");
        }
        Log.d("Successful herererer", "Successful");
    }

    public void getPublishedVideos(Context context, String myUsername, ArrayList<Video> myVideos){
        credentialsProvider = getCredentials(context);

        if(credentialsProvider != null){
            DynamoDBMapper dynamoDBMapper = initDynamoClient(credentialsProvider);
            //VideoMapper video = dynamoDBMapper.load(VideoMapper.class, "1");

            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(myUsername));
            eav.put(":val2", new AttributeValue().withN("1"));
            eav.put(":val3", new AttributeValue().withN("1"));

            // dynamdbdeki reserve keylerle keyimiz aynı olunca, keyimiz için placeholder olarak
            // expressionattributenames kullanıyoruz, yani "#my_type", "type" için bir placeholder
            Map<String, String> eav2 = new HashMap<String, String>();
            eav2.put("#my_type", "type");
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("username = :val1 and #my_type = :val2 and published = :val3")
                    .withExpressionAttributeValues(eav)
                    .withExpressionAttributeNames(eav2);

            List<VideoMapper> scanResult = dynamoDBMapper.scan(VideoMapper.class, scanExpression);

            if(scanResult.size() != 0 ) {
                int i = 0;
                for (VideoMapper video : scanResult) {
                    Video curVideo = new Video();
                    curVideo.setId(video.getId());
                    curVideo.setName(video.getName());
                    curVideo.setNumOfContents(video.getLength());
                    curVideo.setThumbnailURL("http://" + AWSIP + "/videos/" + video.getId() + ".png");
                    curVideo.setVideoURL("http://" + AWSIP + "/videos/" + video.getId() + ".mp4");
                    myVideos.add(curVideo);
                    Log.d("ID: ", "" + curVideo.getThumbnailURL());
                    i++;
                }
            }
        }else{
            Log.d("Unsuccessful", "Unsuccessful");
        }
        Log.d("Successful herererer", "Successful");
    }

    public void getMyStatuses(Context context, String myUsername, List<Status> myStatuses){
        credentialsProvider = getCredentials(context);

        if(credentialsProvider != null){
            DynamoDBMapper dynamoDBMapper = initDynamoClient(credentialsProvider);
            //VideoMapper video = dynamoDBMapper.load(VideoMapper.class, "1");

            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(myUsername));
            eav.put(":val2", new AttributeValue().withN("1"));
            eav.put(":val3", new AttributeValue().withN("100"));

            // dynamdbdeki reserve keylerle keyimiz aynı olunca, keyimiz için placeholder olarak
            // expressionattributenames kullanıyoruz, yani "#my_type", "type" için bir placeholder
            Map<String, String> eav2 = new HashMap<String, String>();
            eav2.put("#my_type", "type");
            eav2.put("#my_status", "status");
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("username = :val1 and #my_type = :val2 and #my_status <> :val3")
                    .withExpressionAttributeValues(eav)
                    .withExpressionAttributeNames(eav2);

            List<VideoMapper> scanResult = dynamoDBMapper.scan(VideoMapper.class, scanExpression);

            if(scanResult.size() != 0 ) {
                int i = 0;
                for (VideoMapper video : scanResult) {
                    Status curStatus = new Status();
                    curStatus.setFaceStatus(video.getFaceStatus());
                    curStatus.setGazeStatus(video.getGazeStatus());
                    curStatus.setPoseStatus(video.getPoseStatus());
                    curStatus.setVoiceStatus(video.getVoiceStatus());
                    myStatuses.add(curStatus);
                    i++;
                }
            }
        }else{
            Log.d("Unsuccessful", "Unsuccessful");
        }
        Log.d("Successful herererer", "Successful");
    }

    public void getAllPresentationScores(Context context, String myUsername, ArrayList<Presentation> myPresentations){
        credentialsProvider = getCredentials(context);

        if(credentialsProvider != null){
            DynamoDBMapper dynamoDBMapper = initDynamoClient(credentialsProvider);
            //VideoMapper video = dynamoDBMapper.load(VideoMapper.class, "1");

            Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
            eav.put(":val1", new AttributeValue().withS(myUsername));
            eav.put(":val2", new AttributeValue().withN("1"));

            // dynamdbdeki reserve keylerle keyimiz aynı olunca, keyimiz için placeholder olarak
            // expressionattributenames kullanıyoruz, yani "#my_type", "type" için bir placeholder
            Map<String, String> eav2 = new HashMap<String, String>();
            eav2.put("#my_type", "type");
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                    .withFilterExpression("username = :val1 and #my_type = :val2")
                    .withExpressionAttributeValues(eav)
                    .withExpressionAttributeNames(eav2);

            List<VideoMapper> scanResult = dynamoDBMapper.scan(VideoMapper.class, scanExpression);

            if(scanResult.size() != 0 ) {
                int i = 0;
                for (VideoMapper video : scanResult) {
                    Presentation presentation = new Presentation();
                    presentation.setOverall(video.getOverall());
                    presentation.setFace(video.getFace());
                    presentation.setGaze(video.getGaze());
                    presentation.setPose(video.getPose());
                    presentation.setVoice(video.getVoice());
                    myPresentations.add(presentation);
                    i++;
                }
            }
        }else{
            Log.d("Unsuccessful", "Unsuccessful");
        }
        Log.d("Successful herererer", "Successful");
    }

    //public int getFaceStatus

    public void changeCredentials(Context context, String oldUsername, String newUsername, String newPassword, String newEmail){
        credentialsProvider = getCredentials(context);

        if(credentialsProvider != null){
            DynamoDBMapper dynamoDBMapper = initDynamoClient(credentialsProvider);

            Log.d("credentialsProvider", oldUsername);
            //first delete the old row and add new one
            //delete old
            UserMapper mapperClassFirst = new UserMapper();
            mapperClassFirst.setUsername(oldUsername);
            dynamoDBMapper.delete(mapperClassFirst);

            //add new
            UserMapper mapperClassSecond = new UserMapper();
            mapperClassSecond.setUsername(newUsername);
            mapperClassSecond.setPassword(newPassword);
            mapperClassSecond.setEmail(newEmail);
            dynamoDBMapper.delete(mapperClassSecond);
            dynamoDBMapper.save(mapperClassSecond);

        }else{
            Log.d("Credentials Update Fail", "Credentials Update Fail");
        }
        Log.d("Credentials Changed", "Credentials Changed");
    }
}
