package edu.byu.cs.tweeter.server.dao;

import static software.amazon.awssdk.regions.Region.US_EAST_2;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public abstract class BaseDAO {
    private static DynamoDbClient dynamoDbClient;
    private static DynamoDbEnhancedClient enhancedClient;

    protected DynamoDbClient getDynamoDbClient(){
        if(dynamoDbClient == null){
            dynamoDbClient = DynamoDbClient.builder()
                    .region(US_EAST_2)
                    .build();
        }

        return dynamoDbClient;
    }

    protected DynamoDbEnhancedClient getEnhancedClient(){
        if(enhancedClient == null){
            enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(getDynamoDbClient())
                    .build();
        }
        return enhancedClient;
    }

}
