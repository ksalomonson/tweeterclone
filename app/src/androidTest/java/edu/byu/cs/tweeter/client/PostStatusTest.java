package edu.byu.cs.tweeter.client;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.os.Looper;
import android.util.Log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginFragment;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class PostStatusTest {
    private LoginPresenter loginPresenter;
    private CountDownLatch countDownLatch;
    private MockView myMockView;

    private ServerFacade serverFacade;

    private LoginRequest loginRequest;

    private LoginResponse loginResponse;
    private MainPresenter mainPresenterSpy;
    private class MockView implements MainPresenter.View{

        @Override
        public void setFollowerCountText(String valueOf) {

        }

        @Override
        public void setFolloweeCount(int count) {

        }

        @Override
        public void updateFollowButton(boolean b) {

        }

        @Override
        public void followButtonEnabled(boolean b) {

        }

        @Override
        public void logout() {

        }

        @Override
        public void changeButtonToFollowing() {

        }

        @Override
        public void changeButtonToFollow() {

        }

        @Override
        public void postingToastCancel() {

        }

        @Override
        public void makeToast(String s) {
            System.out.println(s);
            countDownLatch.countDown();
        }
    }
    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }
    @BeforeEach
    public void setup(){
        serverFacade = new ServerFacade();
        loginRequest = new LoginRequest("@j","asdf");
        myMockView = Mockito.spy(new MockView());
        mainPresenterSpy = Mockito.spy(new MainPresenter(myMockView));
        try{
            loginResponse = serverFacade.login(loginRequest,"/login");
            Cache.getInstance().setCurrUser(loginResponse.getUser());
            Cache.getInstance().setCurrUserAuthToken(loginResponse.getAuthToken());
        }catch (IOException | TweeterRemoteException ex){
            Log.e("Exception",ex.getMessage());
            throw new RuntimeException("problem with logging in");
        }

        resetCountDownLatch();
    }

    @Test
    public void TestLoginAndPost() throws IOException, TweeterRemoteException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mainPresenterSpy.postStatus("A really cool post I swear guys you should totally read it", null, null);
                Looper.loop();
            }
        }).start();

        try {
            awaitCountDownLatch();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        verify(myMockView,times(1)).makeToast("Successfully Posted!");

        GetStoryRequest getStoryRequest = new GetStoryRequest("@j",Cache.getInstance().getCurrUserAuthToken(),1,null);
        GetStoryResponse response = serverFacade.getStory(getStoryRequest,"/getStory");
        Status post = response.getStory().get(0);
        Log.i("Testing",post.toString());
        assert post.post.equals("A really cool post I swear guys you should totally read it");
        assert post.user.equals(Cache.getInstance().getCurrUser());

    }
}
