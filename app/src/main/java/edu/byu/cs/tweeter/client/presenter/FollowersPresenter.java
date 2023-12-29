package edu.byu.cs.tweeter.client.presenter;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.ObserverSetIsLoading;
import edu.byu.cs.tweeter.client.model.service.observer.ToastableUserServiceObserver;
import edu.byu.cs.tweeter.client.view.ViewInterface;
import edu.byu.cs.tweeter.client.view.ViewLoadUser;
import edu.byu.cs.tweeter.client.view.ViewLoadingFooter;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends Presenter<FollowersPresenter.View> {
    private final UserService userService;
    private final FollowService followService;

    public void onUserClick(String userAlias) {
        userService.userClick(userAlias, Cache.getInstance().getCurrUserAuthToken(), new UserServiceObserver());
    }

    public void loadsMoreItems(User user, int pageSize, User lastFollower) {
        view.addLoadingFooter();
        followService.loadMoreItems(Cache.getInstance().getCurrUserAuthToken(), user, pageSize, lastFollower, new FollowObserver());
    }

    public FollowersPresenter(View view){
        super(view);
        userService = new UserService();
        followService = new FollowService();
    }
    private class UserServiceObserver implements ToastableUserServiceObserver {
        @Override
        public void changeActivityToUser(User user) {
            view.changeActivityToUser(user);
        }
        @Override
        public void makeToast(String s) {
            view.makeToast(s);
        }

        @Override
        public void handleFailure(String message) {
            view.makeToast("Failed to get user's profile: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.makeToast("Failed to get user's profile because of exception: " + exception.getMessage());
        }
    }
    private class FollowObserver implements FollowService.FollowersObserver {
        @Override
        public void setIsLoading(boolean b) {
            view.setIsLoading(b);
        }
        @Override
        public void addFollowers(List<User> followers, Bundle data) {
            view.addFollowers(followers, data);
        }
        @Override
        public void handleFailure(String message) {
            view.makeToast("Failed to get followers: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.makeToast("Failed to get followers because of exception: " + exception.getMessage());
        }
    }
    public interface View extends ViewLoadingFooter {
        void addFollowers(List<User> followers, Bundle data);
    }
}
