package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() throws IOException {
        try{
            Status lastStatus = getLastItem() == null ? null : getLastItem();
            ServerFacade serverFacade = new ServerFacade();
            GetStoryRequest request = new GetStoryRequest(getTargetUser().getAlias(), authToken, getLimit(), lastStatus);
            GetStoryResponse response = serverFacade.getStory(request, "/getStory");
            return new Pair<>(response.getStory(), response.getHasMorePages());
        } catch (TweeterRemoteException e){
            throw new IOException(e.getMessage());
        }
    }
}
