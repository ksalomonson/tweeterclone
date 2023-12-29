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
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.observer.Observer;
import edu.byu.cs.tweeter.client.model.service.observer.ObserverSetIsLoading;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends Service {

    public void isFollower(AuthToken currUserAuthToken, User currUser, User selectedUser, MainPresenter.FollowObserver followServiceObserver) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(currUserAuthToken,
                currUser, selectedUser, new IsFollowerHandler(followServiceObserver));
        executeTask(isFollowerTask);
    }
    //takes different parameters, can't be overwritten
    public void loadMoreItems(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, FollowObserver followObserver) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new GetFollowingHandler(followObserver));
        executeTask(getFollowingTask);
    }
    //Takes different parameters, can't be overwritten
    public void loadMoreItems(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, FeedObserver feedObserver) {
        GetFeedTask getFeedTask = new GetFeedTask(currUserAuthToken,
                user, pageSize, lastStatus, new GetFeedHandler(feedObserver));
        executeTask(getFeedTask);
    }
    public void loadMoreItems(AuthToken authToken, User user, int pageSize, User lastFollower, FollowersObserver observer){
        GetFollowersTask getFollowersTask = new GetFollowersTask(authToken,
                user, pageSize, lastFollower, new GetFollowersHandler(observer));
        executeTask(getFollowersTask);
    }
    public void updateSelectedUserFollowingAndFollowers(User selectedUser, AuthToken currUserAuthToken, MainObserver observer) {
        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowersCountHandler(observer));
        executeTask(followersCountTask);

        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new GetFollowingCountHandler(observer));
        executeTask(followingCountTask);
    }
    /**Handlers*/
    private class GetFollowingHandler extends BackgroundTaskHandler<FollowObserver> {
        public GetFollowingHandler(FollowObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(FollowObserver observer, Bundle data) {
            List<User> followees = (List<User>) data.getSerializable(GetFollowingTask.ITEMS_KEY);
            boolean hasMorePages = data.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
            observer.addMoreFollowees(followees, hasMorePages);
        }
    }
    private class GetFeedHandler extends BackgroundTaskHandler<FeedObserver> {
        public GetFeedHandler(FeedObserver feedObserver) {
            super(feedObserver);
        }
        @Override
        protected void handleSuccessMessage(FeedObserver observer, Bundle data) {
            observer.setIsLoading(false);
            observer.removeLoadingFooter();
            List<Status> statuses = (List<Status>) data.getSerializable(GetFeedTask.ITEMS_KEY);
            observer.addMoreStatuses(statuses, data.getBoolean(GetFeedTask.MORE_PAGES_KEY));
        }
    }
    private class GetFollowersHandler extends BackgroundTaskHandler<FollowersObserver> {
        public GetFollowersHandler(FollowersObserver followersObserver) {
            super(followersObserver);
        }
        @Override
        protected void handleSuccessMessage(FollowersObserver observer, Bundle data) {
            observer.setIsLoading(false);
            List<User> followers = (List<User>) data.getSerializable(GetFollowersTask.ITEMS_KEY);
            observer.addFollowers(followers, data);
        }
    }
    private class GetFollowersCountHandler extends BackgroundTaskHandler<MainObserver> {
        public GetFollowersCountHandler(MainObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(MainObserver observer, Bundle data) {
            int count = data.getInt(GetFollowersCountTask.COUNT_KEY);
            observer.setFollowerCountText(String.valueOf(count));
        }
    }

    // GetFollowingCountHandler

    private class GetFollowingCountHandler extends BackgroundTaskHandler<MainObserver> {
        public GetFollowingCountHandler(MainObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(MainObserver observer, Bundle data) {
            int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
            observer.setFolloweeCount(count);
        }
    }

    private class IsFollowerHandler extends BackgroundTaskHandler<MainObserver> {
        public IsFollowerHandler(MainObserver observer) {
            super(observer);
        }
        @Override
        protected void handleSuccessMessage(MainObserver observer, Bundle data) {
            boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            // If logged in user if a follower of the selected user, display the follow button as "following"
            if (isFollower) {
                observer.changeButtonToFollowing();
            } else {
                observer.changeButtonToFollow();
            }
        }
    }
    /**Observers*/
    public interface MainObserver extends Observer {
        void setFollowerCountText(String valueOf);
        void setFolloweeCount(int count);
        void changeButtonToFollowing();
        void changeButtonToFollow();
    }

    public interface FollowersObserver extends ObserverSetIsLoading {
        void addFollowers(List<User> followers, Bundle data);
    }
    public interface FollowObserver extends Observer{

        void addMoreFollowees(List<User> followees, boolean hasMorePages);
    }
    public interface FeedObserver extends ObserverSetIsLoading {
        void removeLoadingFooter();

        void addMoreStatuses(List<Status> statuses, boolean aBoolean);
    }
}
