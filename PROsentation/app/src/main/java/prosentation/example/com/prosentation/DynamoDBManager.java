package prosentation.example.com.prosentation;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoIdentityProviderClientConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 6.2.2018.
 */

public class DynamoDBManager {
    public static AmazonDynamoDBClient dynamoDBClient;
    public static DynamoDBMapper dynamoDBMapper;

    CognitoCachingCredentialsProvider credentialsProvider = null;
    CognitoSyncManager syncManager = null;

    public CognitoCachingCredentialsProvider getCredentials(Context context){
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-2:c7b46579-b6b8-467e-87b0-e8a154abf684", // Identity pool ID
                Regions.US_EAST_2 // Region
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

    public void insertVideoToDB(Context context, int id, int type, int length, String name, int status, String username){
        CognitoCachingCredentialsProvider credentialsProvider = getCredentials(context);
        VideoMapper mapperClass = new VideoMapper();
        mapperClass.setId(id);
        mapperClass.setType(type);
        mapperClass.setLength(length);
        mapperClass.setName(name);
        mapperClass.setStatus(status);
        mapperClass.setUsername(username);
        mapperClass.setVideoDate(Calendar.getInstance().getTime().toString());
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
}
