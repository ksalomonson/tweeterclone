package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {
    /**
     * The user that is being followed.
     */
    private final User followee;

    public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
    }

    @Override
    protected void runTask() throws IOException {
        try {
            FollowRequest request = new FollowRequest(followee.getAlias(), Cache.getInstance().getCurrUser().getAlias(), authToken);
            ServerFacade serverFacade = new ServerFacade();
            FollowResponse response = serverFacade.follow(request, "/follow");
            // Call sendSuccessMessage if successful
            sendSuccessMessage();
            // or call sendFailedMessage if not successful
            // sendFailedMessage()
        } catch (TweeterRemoteException e){
            throw new IOException(e.getMessage());
        }
    }

}
