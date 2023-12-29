package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() throws IOException {
        String lastFolloweeAlias = getLastItem() == null ? null : getLastItem().getAlias();
        try {
            FollowersRequest followersRequest = new FollowersRequest(authToken, getTargetUser().getAlias(), getLimit(), lastFolloweeAlias);
            ServerFacade serverFacade = new ServerFacade();
            FollowersResponse followersResponse = serverFacade.getFollowers(followersRequest, "/getFollowers");
            return new Pair<>(followersResponse.getFollowers(), followersResponse.getHasMorePages());
        } catch (TweeterRemoteException e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
