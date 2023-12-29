package edu.byu.cs.tweeter.server.dao.dao_interfaces;

import java.util.List;

import edu.byu.cs.tweeter.server.dao.dto.FeedDTO;
import edu.byu.cs.tweeter.server.dao.dto.FollowDTO;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;

public interface FeedDAO {
    /**
     *
     * @param pageLimit limited the number of datapages returned
     * @param targetUser
     * @param lastStatus
     * @return A list of the appropriate feeds
     */
    Pair<List<FeedDTO>, Boolean> getFeed(int pageLimit, String targetUser, FeedDTO lastStatus);

    void addStatus(FeedDTO feedDTO);

    void addFeedBatch(List<FeedDTO> feedDTOList);



}
