package edu.byu.cs.tweeter.client;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusServiceTest {
    private StatusService statusService;
    private CountDownLatch latch;
    private class StatusObserver implements StatusService.StoryObserver {
        private boolean success;
        private String message;
        private Exception ex;
        private List<Status> story;
        private boolean hasMorePages;

        @Override
        public void addMoreStatuses(List<Status> statuses, boolean aBoolean, Status status) {
            success = true;
            latch.countDown();
        }

        @Override
        public void handleFailure(String message) {

        }

        @Override
        public void handleException(Exception exception) {

        }

        @Override
        public void setIsLoading(boolean b) {

        }
        public boolean getSucess(){
            return success;
        }
    }
    private void resetCountDownLatch() {
        latch = new CountDownLatch(1);
    }
    private void awaitCountDownLatch() throws InterruptedException {
        latch.await();
        resetCountDownLatch();
    }

    @BeforeEach
    public void setup() {
        statusService = new StatusService();
        resetCountDownLatch();
    }
    @Test
    public void TestGetStory() throws InterruptedException {
        User user = new User("John", "Smith", "@allen", "Insert image");
        AuthToken authToken = new AuthToken();
        authToken.setToken("authtoken");
        StatusObserver observer = new StatusObserver();
        statusService.loadMoreItems(authToken, user, 10, null, observer);
        awaitCountDownLatch();
        assert(observer.success);
    }
}
