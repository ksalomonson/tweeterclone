package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.server.dao.ConcreteDaoFactory;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dto.FeedDTO;
import edu.byu.cs.tweeter.server.dao.dto.FollowDTO;
import edu.byu.cs.tweeter.server.dao.dto.StoryDTO;
import edu.byu.cs.tweeter.util.Pair;


public class PostUpdateQueue implements RequestHandler<SQSEvent,Void>{

    DAOFactory daoFactory;
    Gson gson;
    AmazonSQS sqs;

    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        System.out.println("Entered Function correctly");
        for(SQSEvent.SQSMessage msg:input.getRecords()){

            String queueUrl = "https://sqs.us-east-2.amazonaws.com/988506542866/updateFeedQueue";
            StoryDTO storyDTO = getGson().fromJson(msg.getBody(),StoryDTO.class);
            FollowDAO followDAO = getDaoFactory().makeFollowDAO();
            Pair<List<FollowDTO>,Boolean> followData = followDAO.getFollowers(storyDTO.getUserAlias(),100,null);
            System.out.println("Returned from Follow DAO");
            if(!followData.getSecond()){
                System.out.println("Loop broken");
                createAndSendMessage(followData,storyDTO,followDAO,queueUrl);
                return null;
            }
            System.out.println("After worked check");
            while (followData.getSecond()){
                List<FeedDTO> feedDTOList = new ArrayList<>();
                int size = followData.getFirst().size();
                for(FollowDTO follow:followData.getFirst()){
                    FeedDTO feedDTO = new FeedDTO(follow.getFollower_handle(),
                            follow.getFollowee_handle(),storyDTO.getTimeStamp(),storyDTO.getUrls(),
                            storyDTO.getMentions(),storyDTO.getPost());
                    feedDTOList.add(feedDTO);
                }
                followData = followDAO.getFollowers(storyDTO.getUserAlias(),100,
                        followData.getFirst().get(size-1).getFollower_handle());

                String feedJson = getGson().toJson(feedDTOList);
                SendMessageRequest sendMessageRequest = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody(feedJson);
                SendMessageResult sendMessageResult = getSQSClient().sendMessage(sendMessageRequest);
                String msgId = sendMessageResult.getMessageId();
                System.out.println("Message ID: " + msgId);
            }

        }

        return null;
    }

    private void createAndSendMessage(Pair<List<FollowDTO>,Boolean> followData,StoryDTO storyDTO,FollowDAO followDAO,String queueUrl){
        List<FeedDTO> feedDTOList = new ArrayList<>();
        for(FollowDTO follow:followData.getFirst()){
            FeedDTO feedDTO = new FeedDTO(follow.getFollower_handle(),
                    follow.getFollowee_handle(),storyDTO.getTimeStamp(),storyDTO.getUrls(),
                    storyDTO.getMentions(),storyDTO.getPost());
            feedDTOList.add(feedDTO);
        }

        String feedJson = getGson().toJson(feedDTOList);
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(feedJson);
        SendMessageResult sendMessageResult = getSQSClient().sendMessage(sendMessageRequest);
        String msgId = sendMessageResult.getMessageId();
        System.out.println("Message ID: " + msgId);
    }

    private DAOFactory getDaoFactory(){
        if(daoFactory == null){
            daoFactory = new ConcreteDaoFactory();
        }
        return daoFactory;
    }

    private Gson getGson(){
        if(gson == null){
            gson = new Gson();
        }
        return gson;
    }

    private AmazonSQS getSQSClient(){
        if(sqs == null){
            sqs = AmazonSQSClientBuilder.defaultClient();
        }

        return sqs;
    }
}
