package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() throws IOException {
        try{
            FollowingCountRequest request = new FollowingCountRequest(authToken, getTargetUser().getAlias());
            ServerFacade serverFacade = new ServerFacade();
            CountResponse response = serverFacade.getFollowingCount(request, "/getFollowingCount");
            return response.getCount();
        } catch (TweeterRemoteException e){
            throw new IOException(e.getMessage());
        }
    }
}
