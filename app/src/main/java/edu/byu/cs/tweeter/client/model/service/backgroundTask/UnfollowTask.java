package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {

    /**
     * The user that is being followed.
     */
    private final User followee;

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
    }

    @Override
    protected void runTask() throws IOException {
        try {
            UnfollowRequest request = new UnfollowRequest(followee.getAlias(), Cache.getInstance().getCurrUser().getAlias(), authToken);
            ServerFacade serverFacade = new ServerFacade();
            serverFacade.unfollow(request, "/unfollow");
            // Call sendSuccessMessage if successful
            sendSuccessMessage();
            // or call sendFailedMessage if not successful
            // sendFailedMessage
        } catch (TweeterRemoteException e){
            throw new IOException(e.getMessage());
        }
    }


}
