package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;
import java.util.Random;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthenticatedTask {

    public static final String IS_FOLLOWER_KEY = "is-follower";

    /**
     * The alleged follower.
     */
    private final User follower;

    /**
     * The alleged followee.
     */
    private final User followee;

    private boolean isFollower;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    protected void runTask() throws IOException{
        try{
            IsFollowerRequest request = new IsFollowerRequest(follower.getAlias(), followee.getAlias(), authToken);
            ServerFacade serverFacade = new ServerFacade();
            IsFollowerResponse response = serverFacade.isFollower(request, "isFollower");
            isFollower = response.getIsFollowFlag();
            // Call sendSuccessMessage if successful
            sendSuccessMessage();
            // or call sendFailedMessage if not successful
            // sendFailedMessage()
        } catch(TweeterRemoteException e){
            throw new IOException(e.getMessage());
        }

    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }
}
