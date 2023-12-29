package edu.byu.cs.tweeter.client.presenter;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;


public class MainPresenterUnitTest {
    private MainPresenter.View mockView;
    private StatusService mockStatusService;
    private Cache mockCache;
    private MainPresenter mainPresenterSpy;
    private List<String> urls;
    List<String> mentions;

    @BeforeEach
    public void setup(){
        //create mock classes
        mockView = Mockito.mock(MainPresenter.View.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mockCache = Mockito.mock(Cache.class);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));
        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);

        Cache.setInstance(mockCache);
    }
    private void testPostSetup(){
        urls = new ArrayList<>();
        urls.add("byu.edu");
        mentions = new ArrayList<>();
        mentions.add("@duck");
    }
    @Test
    public void testPost_postSuccessful(){
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.MainObserver observer = invocation.getArgument(5, StatusService.MainObserver.class);
                observer.makeToast("Successfully Posted!");
                return null;
            }
        };
        testPostSetup();
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        mainPresenterSpy.postStatus("Hello world", urls, mentions);
        Mockito.verify(mockView).makeToast("Successfully Posted!");
    }

    @Test
    public void testPost_failedWithMessage(){
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.MainObserver observer = invocation.getArgument(5, StatusService.MainObserver.class);
                observer.handleFailure("the error");
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        testPostSetup();
        mainPresenterSpy.postStatus("Hello world", urls, mentions);
        Mockito.verify(mockView).makeToast("Error occurred: the error");
    }
    @Test
    public void testPost_failedWithException(){
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                StatusService.MainObserver observer = invocation.getArgument(5, StatusService.MainObserver.class);
                Exception exception = new Exception("the exception");
                observer.handleException(exception);
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        testPostSetup();
        mainPresenterSpy.postStatus("Hello world", urls, mentions);
        Mockito.verify(mockView).makeToast("Exception occurred: the exception");
    }
}
