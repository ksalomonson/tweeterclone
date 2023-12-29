package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.ToastableUserServiceObserver;
import edu.byu.cs.tweeter.client.view.ViewLoadUser;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends Presenter<StoryPresenter.View> {
    private final UserService userService;
    private final StatusService statusService;
    public StoryPresenter(View view){
        super(view);
        userService = new UserService();
        statusService = new StatusService();
    }

    public void onUserClick(String userAlias) {
        userService.userClick(userAlias, Cache.getInstance().getCurrUserAuthToken(), new UserServiceObserver());
    }

    public void loadMoreItems(User user, int pageSize, Status lastStatus) {
        statusService.loadMoreItems(Cache.getInstance().getCurrUserAuthToken(), user, pageSize, lastStatus, new StatusObserver());
    }
    private class UserServiceObserver implements ToastableUserServiceObserver {

        @Override
        public void makeToast(String s) {
            view.makeToast(s);
        }
        @Override
        public void changeActivityToUser(User user) {
            view.changeActivityToUser(user);
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
    public class StatusObserver implements StatusService.StoryObserver{

        @Override
        public void setIsLoading(boolean b) {
            view.setIsLoading(b);
        }

        @Override
        public void addMoreStatuses(List<Status> statuses, boolean aBoolean, Status status) {
            view.addMoreStatuses(statuses, aBoolean, status);
        }
        @Override
        public void handleFailure(String message) {
            view.makeToast("Failed to get story: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.makeToast("Failed to get story because of exception: " + exception.getMessage());
        }
    }
    public interface View extends ViewLoadUser {
        void addMoreStatuses(List<Status> statuses, boolean aBoolean, Status status);
    }
}
