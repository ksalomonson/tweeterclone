package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() throws IOException {
        try{
            FollowerCountRequest request = new FollowerCountRequest(authToken, getTargetUser().getAlias());
            ServerFacade serverFacade = new ServerFacade();
            CountResponse response = serverFacade.getFollowerCount(request, "/getFollowerCount");
            return response.getCount();
        } catch (TweeterRemoteException e){
            throw new IOException(e.getMessage());
        }
    }
}
