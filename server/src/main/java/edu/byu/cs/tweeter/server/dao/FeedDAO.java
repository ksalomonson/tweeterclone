package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;
//depreciated, remove
public class FeedDAO {
    public Pair<List<Status>, Boolean> getItems(Status lastStatus, int limit) {
        return getFakeData().getPageOfStatus(lastStatus, limit);
    }

    FakeData getFakeData(){return FakeData.getInstance();}

    public Pair<List<Status>, Boolean> getStoryItems(Status lastStatus, int limit) {
        return getFakeData().getPageOfStatus(lastStatus, limit);
    }
}
