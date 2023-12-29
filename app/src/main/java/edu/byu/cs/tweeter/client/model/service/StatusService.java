package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.observer.Observer;
import edu.byu.cs.tweeter.client.model.service.observer.ObserverSetIsLoading;
import edu.byu.cs.tweeter.client.model.service.observer.ToastableObserver;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends Service{
    public void loadMoreItems(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, StoryObserver storyObserver) {
        GetStoryTask getStoryTask = new GetStoryTask(currUserAuthToken,
                user, pageSize, lastStatus, new GetStoryHandler(storyObserver));
        executeTask(getStoryTask);
    }

    public void followUser(AuthToken currUserAuthToken, User selectedUser, MainObserver observer) {
        FollowTask unfollowTask = new FollowTask(currUserAuthToken,
                selectedUser, new FollowHandler(observer, selectedUser));
        executeTask(unfollowTask);
        observer.makeToast("Adding " + selectedUser.getName() + "...");
    }

    public void unfollowUser(AuthToken currUserAuthToken, User selectedUser, MainPresenter.StatusServiceObserver statusServiceObserver) {
        UnfollowTask followTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new UnfollowHandler(statusServiceObserver, selectedUser));
        executeTask(followTask);
        statusServiceObserver.makeToast("Removing " + selectedUser.getName() + "...");
    }

    public boolean logout(AuthToken currUserAuthToken, MainObserver observer) {
        LogoutTask logoutTask = new LogoutTask(currUserAuthToken, new LogoutHandler(observer));
        executeTask(logoutTask);
        return true;
    }

    public void postStatus(User currUser,String post, List<String> parseURLs, List<String> parseMentions, AuthToken currUserAuthToken, MainPresenter.StatusServiceObserver observer) {
        Status newStatus = new Status(post, currUser, System.currentTimeMillis(), parseURLs, parseMentions);
        PostStatusTask statusTask = new PostStatusTask(currUserAuthToken,
                newStatus, new PostStatusHandler(observer));
        executeTask(statusTask);
    }
    /**Handlers*/

    private class GetStoryHandler extends BackgroundTaskHandler<StoryObserver> {
        public GetStoryHandler(StoryObserver storyObserver) {
            super(storyObserver);
        }
        @Override
        protected void handleSuccessMessage(StoryObserver observer, Bundle data) {
            observer.setIsLoading(false);
            List<Status> statuses = (List<Status>) data.getSerializable(GetStoryTask.ITEMS_KEY);
            observer.addMoreStatuses(statuses, data.getBoolean(GetStoryTask.MORE_PAGES_KEY), (statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null);
        }
    }
    private class UnfollowHandler extends BackgroundTaskHandler<MainObserver> {
        private final User selectedUser;
        public UnfollowHandler(MainObserver observer, User selectedUser) {
            super(observer);
            this.selectedUser = selectedUser;
        }
        @Override
        protected void handleSuccessMessage(MainObserver observer, Bundle data) {
            observer.updateSelectedUserFollowingAndFollowers(selectedUser);
            observer.updateFolloweButton(true);
            observer.followButtonEnabled(true);
        }
    }

    private class FollowHandler extends BackgroundTaskHandler<MainObserver> {
        User selectedUser;
        public FollowHandler(MainObserver observer, User selectedUser) {
            super(observer);
            this.selectedUser = selectedUser;
        }
        @Override
        protected void handleSuccessMessage(MainObserver observer, Bundle data) {
            observer.updateSelectedUserFollowingAndFollowers(selectedUser);
            observer.updateFolloweButton(false);
            observer.followButtonEnabled(true);
        }
    }

    private class LogoutHandler extends BackgroundTaskHandler<MainObserver> {
        public LogoutHandler(MainObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(MainObserver observer, Bundle data) {
            observer.logout();
        }
    }

    private class PostStatusHandler extends BackgroundTaskHandler<MainObserver> {
        public PostStatusHandler(MainObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(MainObserver observer, Bundle data) {
            observer.postingToastCancel();
            observer.makeToast("Successfully Posted!");
        }
    }
    /**Observers*/

    public interface MainObserver extends ToastableObserver {
        void updateSelectedUserFollowingAndFollowers(User selectedUser);

        void updateFolloweButton(boolean b);

        void followButtonEnabled(boolean b);

        void logout();

        void postingToastCancel();
    }
    public interface StoryObserver extends ObserverSetIsLoading {
        void addMoreStatuses(List<Status> statuses, boolean aBoolean, Status status);
    }
}
