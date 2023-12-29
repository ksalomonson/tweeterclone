package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.UserServiceObserver;
import edu.byu.cs.tweeter.client.view.ViewLoadUser;
import edu.byu.cs.tweeter.client.view.ViewLoadingFooter;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter {
    private final FollowService followService;
    
    public FeedPresenter(){
        this.followService = new FollowService();
    }

    public void userProfileClick(String toString) {
        userService.userClick(toString, Cache.getInstance().getCurrUserAuthToken(), new FeedServiceObserver());
    }

    public void loadMoreItems(User user, int pageSize, Status lastStatus) {
        followService.loadMoreItems(Cache.getInstance().getCurrUserAuthToken(), user, pageSize, lastStatus, new FeedInterfaceObserver());
    }
    private View view;
    private StatusService statusService;
    private UserService userService;
    public FeedPresenter(View view){
        this.view = view;
        statusService = new StatusService();
        userService = new UserService();
        followService = new FollowService();
    }
    private class FeedServiceObserver implements UserServiceObserver {

        @Override
        public void changeActivityToUser(User user) {
            view.changeActivityToUser(user);
        }
        @Override
        public void handleFailure(String message) {
            view.makeToast(message);
        }

        @Override
        public void handleException(Exception exception) {
            view.makeToast(exception.getMessage());
        }
    }
    private class FeedInterfaceObserver implements FollowService.FeedObserver{

        @Override
        public void setIsLoading(boolean b) {
            view.setIsLoading(b);
        }

        @Override
        public void removeLoadingFooter() {
            view.removeLoadingFooter();
        }

        @Override
        public void addMoreStatuses(List<Status> statuses, boolean aBoolean) {
            view.addMoreStatuses(statuses, aBoolean);
        }
        @Override
        public void handleFailure(String message) {
            view.makeToast("A failure occurred " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.makeToast("An exception was thrown: " + exception.getMessage());
        }
    }

    public interface View extends ViewLoadingFooter {
        void setIsLoading(boolean b);
        void addMoreStatuses(List<Status> statuses, boolean aBoolean);
    }
}
