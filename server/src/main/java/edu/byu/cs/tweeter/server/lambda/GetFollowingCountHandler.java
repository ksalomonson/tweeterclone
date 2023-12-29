package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.server.dao.ConcreteDaoFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowingCountHandler implements RequestHandler<FollowingCountRequest, CountResponse> {
    @Override
    public CountResponse handleRequest(FollowingCountRequest input, Context context) {
        FollowService followService = new FollowService();
        return followService.getFollowingCount(input, new ConcreteDaoFactory());
    }
}
