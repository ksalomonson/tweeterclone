package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.ConcreteFollowDAO;
import edu.byu.cs.tweeter.server.dao.DataAccessException;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.FeedDAO;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.FollowDAO;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.StoryDAO;
import edu.byu.cs.tweeter.server.dao.dao_interfaces.UserDAO;
import edu.byu.cs.tweeter.server.dao.dto.FollowDTO;
import edu.byu.cs.tweeter.server.dao.dto.StoryDTO;
import edu.byu.cs.tweeter.server.dao.dto.UserDTO;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends BaseService {

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link ConcreteFollowDAO} to
     * get the followees.
     *
     * @param request    contains the data required to fulfill the request.
     * @param daoFactory
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request, DAOFactory daoFactory) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }


        AuthToken updatedAuthToken = authenticate(request.getAuthToken(),daoFactory);
        FollowDAO followDAO = daoFactory.makeFollowDAO();
        UserDAO userDAO = daoFactory.makeUserDao();
        Pair<List<FollowDTO>,Boolean> data = followDAO.getFollowees(request.getFollowerAlias(),
                request.getLimit(),request.getLastFolloweeAlias());
        List<User> followees = new ArrayList<>();
        System.out.println(data.getFirst().size());
        for(FollowDTO follow:data.getFirst()){
            User user = null;
            try{
                user = convertUserDTO(userDAO.getItem(follow.getFollowee_handle()));
            }catch (DataAccessException ex){
                System.out.println(ex.getMessage());
                throw new RuntimeException("[Bad Request]" + ex.getMessage());
            }

            System.out.println(user.getFirstName());
            followees.add(user);
        }
        return new FollowingResponse(followees,data.getSecond(),updatedAuthToken);

    }

    /**
     * Answers the age old question, how popular am I?
     * @param request
     * @param daoFactory
     * @return
     */
    public IsFollowerResponse isFollower(IsFollowerRequest request, DAOFactory daoFactory){
        if(request.getAllegedFollowerAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an alleged Follower");
        }else if(request.getAllegedFolloweeAlias()==null){
            throw new RuntimeException("[Bad Request] Request needs to have an alleged followee");
        }
        AuthToken updatedAuthToken = authenticate(request.getAuthToken(),daoFactory);
        FollowDAO followDAO = daoFactory.makeFollowDAO();
        FollowDTO followDTO = followDAO.getFollow(request.getAllegedFollowerAlias(),
                request.getAllegedFolloweeAlias());
        if(followDTO == null){
            //System.out.println("You are not following:"+request.getAllegedFolloweeAlias() );
            return new IsFollowerResponse(false,updatedAuthToken);
        }
        //System.out.println("You are following:"+request.getAllegedFolloweeAlias() );
        return new IsFollowerResponse(true,updatedAuthToken);
    }

    /**
     * returns a list of useraliases "following" the current user
     * @param request
     * @param daoFactory
     * @return
     */
    public FollowersResponse getFollowers(FollowersRequest request, DAOFactory daoFactory){
        System.out.print("Entered getFollowers\n");
        if(request.getFolloweeAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }else if(request.getLimit() <= 0){
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        System.out.print("Authenticating authtoken\n");
        AuthToken updatedAuthToken = authenticate(request.getAuthToken(),daoFactory);
        System.out.print("Authtoken authenticated successfully\n");
        FollowDAO followDAO = daoFactory.makeFollowDAO();
        UserDAO userDAO = daoFactory.makeUserDao();
        System.out.print("Beginning get followers\n");
        Pair<List<FollowDTO>,Boolean> data = followDAO.getFollowers(request.getFolloweeAlias(),
                request.getLimit(),request.getLastFollowerAlias());
        System.out.print("After get Followers");
        List<User> followers = new ArrayList<>();
        System.out.println(data.getFirst().size());
        for(FollowDTO follow:data.getFirst()){
            User user = null;
            try{
                user = convertUserDTO(userDAO.getItem(follow.getFollower_handle()));
            }catch (DataAccessException ex){
                System.out.println(ex.getMessage());
                throw new RuntimeException("[Bad Request]" + ex.getMessage());
            }

            System.out.println(user.getFirstName());
            followers.add(user);
        }
        return new FollowersResponse(followers,data.getSecond(),request.getAuthToken());
    }

    /**
     * updates following and the user's feed
     * @param request
     * @param daoFactory
     * @return
     */
    public FollowResponse follow(FollowRequest request, DAOFactory daoFactory) {

        if (request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing followee user alias attribute");
        }
        if (request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Missing follower user alias attribute");
        }
        AuthToken updatedAuthToken = authenticate(request.getAuthToken(),daoFactory);
        UserDAO userDAO = daoFactory.makeUserDao();
        FollowDAO followDAO = daoFactory.makeFollowDAO();
        FeedDAO feedDAO = daoFactory.makeFeedDAO();
        StoryDAO storyDAO = daoFactory.makeStoryDAO();
        try {
            UserDTO follower = userDAO.getItem(request.getFollowerAlias());
            UserDTO followee = userDAO.getItem(request.getFolloweeAlias());
            followDAO.putFollow(request.getFollowerAlias(),follower.getFirstName(),
                    request.getFolloweeAlias(),followee.getFirstName());
            follower.setFolloweeCount(follower.getFolloweeCount()+1);
            followee.setFollowerCount(followee.getFollowerCount()+1);
            userDAO.updateUser(follower);
            userDAO.updateUser(followee);
            List<StoryDTO> storyDTO = storyDAO.getStory(10, followee.getUserAlias(), null).getFirst();
            List<Status> convertedStatuses = new ArrayList<>();
            StatusService statusService = new StatusService();
            for (int i = 0; i < storyDTO.size(); i++) {
                convertedStatuses.add(statusService.convertStoryDtoToStatus(storyDTO.get(i), daoFactory));
            }
            for (int i = 0; i < convertedStatuses.size(); i++) {
                feedDAO.addStatus(statusService.convertStatusToFeedDTO(convertedStatuses.get(i), follower.getUserAlias()));
            }
        }catch (DataAccessException ex){
            System.out.println(ex.getMessage());
            throw new RuntimeException("there is a problem with following");
        }



        return new FollowResponse(updatedAuthToken);
    }

    /**
     * Authenticated task
     * @param request
     * @param daoFactory
     * @return CountResponse, int of users following
     */
    public CountResponse getFollowerCount(FollowerCountRequest request, DAOFactory daoFactory){
        if(request.getTargetUserAlias() == null){
            throw new RuntimeException("[Bad Request] Missing target user alias attribute");
        }
        AuthToken updatedAuthToken = authenticate(request.getAuthToken(),daoFactory);
        UserDAO userDAO = daoFactory.makeUserDao();
        UserDTO userDTO = null;
        try {
            System.out.println(request.getTargetUserAlias());
             userDTO = userDAO.getItem(request.getTargetUserAlias());
        } catch (DataAccessException ex){
            System.out.println(ex.getMessage());
            throw new RuntimeException("[Bad Request] something went wrong with FollowerCount");
        }


        return new CountResponse(userDTO.getFollowerCount(),updatedAuthToken);
    }

    /**
     * Authenticated Task, gets number of users current user is following
     * @param request
     * @param daoFactory
     * @return
     */
    public CountResponse getFollowingCount(FollowingCountRequest request, DAOFactory daoFactory){
        if(request.getTargetUserAlias() == null){
            throw new RuntimeException("[Bad Request] Missing target user alias attribute");
        }
        AuthToken updatedAuthToken = authenticate(request.getAuthToken(),daoFactory);

        UserDAO userDAO = daoFactory.makeUserDao();
        UserDTO userDTO = null;
        try {
            System.out.println(request.getTargetUserAlias());
            userDTO = userDAO.getItem(request.getTargetUserAlias());
        } catch (DataAccessException ex){
            System.out.println(ex.getMessage());
            throw new RuntimeException("[Bad Request] something went wrong with FollowerCount");
        }

        return new CountResponse(userDTO.getFolloweeCount(),updatedAuthToken);
    }

    /**
     * updates database, past statuses are not removed from feed
     * @param request
     * @param daoFactory
     * @return
     */
    public UnfollowResponse unfollow(UnfollowRequest request, DAOFactory daoFactory){

        if (request.getFolloweeAlias() == null){
            throw new RuntimeException("[Bad Request] Missing followee user alias");
        }
        if(request.getFollowerAlias() == null){
            throw new RuntimeException("[Bad Request] Missing follower user alias");
        }
        AuthToken updatedAuthToken = authenticate(request.getAuthToken(),daoFactory);

        UserDAO userDAO = daoFactory.makeUserDao();
        FollowDAO followDAO = daoFactory.makeFollowDAO();
        try {
            UserDTO follower = userDAO.getItem(request.getFollowerAlias());
            UserDTO followee = userDAO.getItem(request.getFolloweeAlias());
            FollowDTO followDTO = followDAO.getFollow(request.getFollowerAlias(),request.getFolloweeAlias());
            followDAO.delete(followDTO);
            if(followDTO == null){
                throw new RuntimeException("[Bad Request] failed to unfollow");
            }
            follower.setFolloweeCount(follower.getFolloweeCount()-1);
            followee.setFollowerCount(followee.getFollowerCount()-1);
            userDAO.updateUser(follower);
            userDAO.updateUser(followee);
        }catch (DataAccessException ex){
            System.out.println(ex.getMessage());
            throw new RuntimeException("there is a problem with unfollowing");
        }

        return new UnfollowResponse(updatedAuthToken);
    }



    /**
     * Returns an instance of {@link ConcreteFollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    ConcreteFollowDAO getFollowingDAO() {
        return new ConcreteFollowDAO();
    }


}
