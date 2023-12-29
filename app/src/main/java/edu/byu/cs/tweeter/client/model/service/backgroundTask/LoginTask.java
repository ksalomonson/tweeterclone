package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() throws IOException {
        try {
            LoginRequest loginRequest = new LoginRequest(this.username, this.password);
            ServerFacade serverFacade = new ServerFacade();
            LoginResponse loginResponse = serverFacade.login(loginRequest, "/login");
            if(loginResponse == null || loginResponse.getUser() == null || loginResponse.getUser().getName() == null){
                sendFailedMessage(loginResponse.getMessage());
            }
            return new Pair<>(loginResponse.getUser(), loginResponse.getAuthToken());
        }catch (TweeterRemoteException e){
            throw new IOException(e.getMessage());
        }
    }
}
