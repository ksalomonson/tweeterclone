package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.server.dao.ConcreteDaoFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersHandler implements RequestHandler<FollowersRequest, FollowersResponse> {

    @Override
    public FollowersResponse handleRequest(FollowersRequest input, Context context) {
        System.out.print("Beginning log");
        FollowService followService = new FollowService();
        return followService.getFollowers(input, new ConcreteDaoFactory());
    }
}
