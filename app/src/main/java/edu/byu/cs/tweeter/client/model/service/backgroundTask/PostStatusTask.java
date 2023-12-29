package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthenticatedTask {

    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */
    private final Status status;

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(authToken, messageHandler);
        this.status = status;
    }

    @Override
    protected void runTask() throws IOException {
        try {
            PostStatusRequest request = new PostStatusRequest(authToken, status);
            ServerFacade serverFacade = new ServerFacade();
            PostStatusResponse response = serverFacade.postStatus(request, "/postStatus");
            // Call sendSuccessMessage if successful
            sendSuccessMessage();
            // or call sendFailedMessage if not successful
            // sendFailedMessage()
        } catch (TweeterRemoteException e){
            throw new IOException(e.getMessage());
        }
    }

}
