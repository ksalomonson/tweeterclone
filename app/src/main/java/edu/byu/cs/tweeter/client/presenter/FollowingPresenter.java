package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.UserServiceObserver;
import edu.byu.cs.tweeter.client.view.ViewLoadUser;
import edu.byu.cs.tweeter.client.view.ViewLoadingFooter;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends Presenter<FollowingPresenter.View>{
    private static final int PAGE_SIZE = 10;
    private User lastFollowee;
    private boolean isLoading = false;

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
    public boolean isLoading() {
        return isLoading;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    private boolean hasMorePages;

    public void userClick(String userAlias) {
        userService.userClick(userAlias, Cache.getInstance().getCurrUserAuthToken(), new UserServiceObserver());
        view.makeToast("Getting user's profile...");
    }

    private final FollowService followService;
    private final UserService userService;

    public FollowingPresenter(View view){
        super(view);
        this.followService = new FollowService();
        this.userService = new UserService();
    }

    public void loadMoreItems(User user) {
        if (!isLoading()) {   // This guard is important for avoiding a race condition in the scrolling code.
            setLoading(true);
            view.addLoadingFooter();

            followService.loadMoreItems(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastFollowee, new FollowServiceObserver());
        }
    }

    private class FollowServiceObserver implements FollowService.FollowObserver{

        @Override
        public void addMoreFollowees(List<User> followees, boolean hasMorePages) {
            isLoading = false;
            view.removeLoadingFooter();
            FollowingPresenter.this.hasMorePages = hasMorePages;
            lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;
            view.addMoreFollowees(followees);
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.removeLoadingFooter();
            view.makeToast("Failed to get following: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            isLoading = false;
            view.removeLoadingFooter();
            view.makeToast("Failed to get following because of exception: " + exception.getMessage());
        }
    }
    private class UserServiceObserver implements edu.byu.cs.tweeter.client.model.service.observer.UserServiceObserver {
        @Override
        public void handleFailure(String message) {
            view.makeToast("Failed to get user's profile: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            view.makeToast("Failed to get user's profile because of exception: " + exception.getMessage());
        }
        @Override
        public void changeActivityToUser(User user) {
            view.changeActivityToUser(user);
        }
    }
    public interface View extends ViewLoadingFooter {
        void addMoreFollowees(List<User> followees);
    }
}
