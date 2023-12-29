package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.observer.Observer;
import edu.byu.cs.tweeter.client.model.service.observer.ToastableUserServiceObserver;
import edu.byu.cs.tweeter.client.model.service.observer.UserServiceObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service {


    public void login(String userAlias, String passPhrase, ToastableUserServiceObserver loginObserver) {
        // Send the login request.
        LoginTask loginTask = new LoginTask(userAlias, passPhrase, new LoginHandler(loginObserver));
        executeTask(loginTask);
    }
    public void register(String firstName, String lastName, String alias, String password, String drawable, RegisterObserver registerObserver) {
        // Send register request.
        RegisterTask registerTask = new RegisterTask(firstName, lastName,
                alias, password, drawable, new RegisterHandler(registerObserver));
        executeTask(registerTask);
    }

    /**----------------------------Changes user activity in Feed-------------------------**/
    public void userClick(String userAlias, AuthToken currUserAuthToken, UserServiceObserver feedObserver) {
        GetUserTask getUserTask = new GetUserTask(currUserAuthToken,
                userAlias, new GetUserHandlerFeed(feedObserver));
        executeTask(getUserTask);
    }
    /**-------------------------Followers Code-----------------------------------**/
    public void userClick(String userAlias, AuthToken currUserAuthToken, ToastableUserServiceObserver followersObserver){
        GetUserTask getUserTask = new GetUserTask(currUserAuthToken, userAlias, new GetUserHandlerFollowers(followersObserver));
        executeTask(getUserTask);
        followersObserver.makeToast("Getting user's profile...");
    }
    /**Handlers*/
    private class RegisterHandler extends BackgroundTaskHandler<RegisterObserver> {
        public RegisterHandler(RegisterObserver registerObserver) {
            super(registerObserver);
        }
        @Override
        protected void handleSuccessMessage(RegisterObserver observer, Bundle data) {
            User registeredUser = (User) data.getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(RegisterTask.AUTH_TOKEN_KEY);
            Cache.getInstance().setCurrUserAuthToken(authToken);
            observer.changeActivity(registeredUser);
        }
    }
    private class GetUserHandlerFeed extends BackgroundTaskHandler<UserServiceObserver> {
        public GetUserHandlerFeed(UserServiceObserver feedObserver) {
            super(feedObserver);
        }
        @Override
        protected void handleSuccessMessage(UserServiceObserver observer, Bundle data) {
            User user = (User) data.getSerializable(GetUserTask.USER_KEY);
            observer.changeActivityToUser(user);
        }
    }
    private class GetUserHandlerUser extends BackgroundTaskHandler<UserServiceObserver> {
        public GetUserHandlerUser(UserServiceObserver userObserver) {
            super(userObserver);
        }
        @Override
        protected void handleSuccessMessage(UserServiceObserver observer, Bundle data) {
            User user = (User) data.getSerializable(GetUserTask.USER_KEY);
            observer.changeActivityToUser(user);
        }
    }
    private class GetUserHandlerFollowers extends BackgroundTaskHandler<ToastableUserServiceObserver> {
        public GetUserHandlerFollowers(ToastableUserServiceObserver followersObserver){
            super(followersObserver);
        }
        @Override
        protected void handleSuccessMessage(ToastableUserServiceObserver observer, Bundle data) {
            User user = (User) data.getSerializable(GetUserTask.USER_KEY);
            observer.changeActivityToUser(user);
        }
    }
    private class GetUserHandlerStory extends BackgroundTaskHandler<ToastableUserServiceObserver> {
        public GetUserHandlerStory(ToastableUserServiceObserver storyObserver){
            super(storyObserver);

        }
        @Override
        protected void handleSuccessMessage(ToastableUserServiceObserver observer, Bundle data) {
            User user = (User) data.getSerializable(GetUserTask.USER_KEY);
            observer.changeActivityToUser(user);
        }
    }
    private class LoginHandler extends BackgroundTaskHandler<ToastableUserServiceObserver> {
        public LoginHandler(ToastableUserServiceObserver loginObserver) {
            super(loginObserver);
        }
        @Override
        protected void handleSuccessMessage(ToastableUserServiceObserver observer, Bundle data) {
            User loggedInUser = (User) data.getSerializable(LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(LoginTask.AUTH_TOKEN_KEY);

            // Cache user session information
            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);
            observer.makeToast("Hello " + Cache.getInstance().getCurrUser().getName());
            observer.changeActivityToUser(loggedInUser);
        }
    }
    /**--------Observers------------*/
    public interface RegisterObserver extends Observer{
        //This one is unique because it requires an authtoken
        void changeActivity(User registeredUser);
    }
}
