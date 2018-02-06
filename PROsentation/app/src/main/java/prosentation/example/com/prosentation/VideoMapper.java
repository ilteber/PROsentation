package prosentation.example.com.prosentation;

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

}
