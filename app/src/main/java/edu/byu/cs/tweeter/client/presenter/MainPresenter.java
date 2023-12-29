package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.view.ViewInterface;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter<MainPresenter.View> {
    private final FollowService followService;
    private StatusService statusService;
    public MainPresenter(View view){
        super(view);
        followService = new FollowService();
    }
    protected StatusService getStatusService(){
        if(statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }

    public void followUser(User selectedUser) {
        getStatusService().followUser(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new StatusServiceObserver());
    }

    public void unfollowUser(User selectedUser) {
        getStatusService().unfollowUser(Cache.getInstance().getCurrUserAuthToken(), selectedUser, new StatusServiceObserver());
    }

    public boolean logout() {
        return getStatusService().logout(Cache.getInstance().getCurrUserAuthToken(), new StatusServiceObserver());
    }

    public void isFollower(User selectedUser) {
        followService.isFollower(Cache.getInstance().getCurrUserAuthToken(), Cache.getInstance().getCurrUser(), selectedUser, new FollowObserver());
    }

    public void postStatus(String post, List<String> parseURLs, List<String> parseMentions) {
        getStatusService().postStatus(Cache.getInstance().getCurrUser(), post, parseURLs, parseMentions,
                Cache.getInstance().getCurrUserAuthToken(), new StatusServiceObserver());
    }

    public void updateSelectedUserFollowingAndFollowers(User selectedUser) {
        followService.updateSelectedUserFollowingAndFollowers(selectedUser, Cache.getInstance().getCurrUserAuthToken(), new FollowObserver());
    }

    public class FollowObserver implements FollowService.MainObserver{

        @Override
        public void setFollowerCountText(String valueOf) {
            view.setFollowerCountText(valueOf);
        }


        @Override
        public void setFolloweeCount(int count) {
            view.setFolloweeCount(count);
        }

        @Override
        public void changeButtonToFollowing() {
            view.changeButtonToFollowing();
        }

        @Override
        public void changeButtonToFollow() {
            view.changeButtonToFollow();
        }

        @Override
        public void handleFailure(String message) {
            view.makeToast("A Failure occurred: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.makeToast("An exception occurred: " + exception.getMessage());
        }
    }
    
    public class StatusServiceObserver implements StatusService.MainObserver{

        @Override
        public void makeToast(String s) {
            view.makeToast(s);
        }

        @Override
        public void updateSelectedUserFollowingAndFollowers(User selectedUser) {
            followService.updateSelectedUserFollowingAndFollowers(selectedUser, Cache.getInstance().getCurrUserAuthToken(), new FollowObserver());
        }

        @Override
        public void updateFolloweButton(boolean b) {
            view.updateFollowButton(b);
        }

        @Override
        public void followButtonEnabled(boolean b) {
            view.followButtonEnabled(b);
        }

        @Override
        public void logout() {
            view.logout();
        }
        @Override
        public void postingToastCancel() {
            view.postingToastCancel();
        }

        @Override
        public void handleFailure(String message) {
            view.makeToast("Error occurred: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.makeToast("Exception occurred: " + exception.getMessage());
        }
    }
    public interface View extends ViewInterface {
        void setFollowerCountText(String valueOf);
        void setFolloweeCount(int count);
        void updateFollowButton(boolean b);
        void followButtonEnabled(boolean b);
        void logout();
        void changeButtonToFollowing();
        void changeButtonToFollow();
        void postingToastCancel();
    }
}
