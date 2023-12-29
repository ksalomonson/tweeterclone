package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {

    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
    }

    @Override
    protected void runTask() throws IOException {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually remove the auth token from  the DB and will need this then.
        try {
            LogoutRequest request = new LogoutRequest(authToken);
            ServerFacade serverFacade = new ServerFacade();
            LogoutResponse response = serverFacade.logout(request, "/logout");
            // Call sendSuccessMessage if successful
            sendSuccessMessage();
            // or call sendFailedMessage if not successful
            // sendFailedMessage()
        } catch(TweeterRemoteException e){
            throw new IOException(e.getMessage());
        }
    }
}
