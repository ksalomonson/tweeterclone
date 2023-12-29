package edu.byu.cs.tweeter.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.internal.runners.statements.Fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.notification.Failure;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class ServerFacadeTests {
    private ServerFacade serverFacade;
    private AuthToken authToken;
    @BeforeEach
    public void setup(){
        serverFacade = new ServerFacade();
        authToken = new AuthToken("authtoken");
    }
    @Test
    public void RegisterTest(){
        RegisterRequest request = new RegisterRequest("@username", "password",
                "John", "Smith", "insert image url here");
        try {
            RegisterResponse response = serverFacade.register(request, "/register");
            assertTrue(response.isSuccess());
            assertEquals(response.getUser().getAlias(), "@allen");
        } catch (Exception e){
            System.out.print(e.getMessage());
            fail();
        }
    }
    @Test
    public void GetFollowersTest(){
        try {
            FollowersRequest request = new FollowersRequest(authToken, "@allen", 1, null);
            FollowersResponse response = serverFacade.getFollowers(request, "/getFollowers");
            assertTrue(response.isSuccess());
            assertTrue(response.getHasMorePages());
            assertEquals(response.getFollowers().size(), 1);
        } catch (Exception e){
            System.out.print(e.getMessage());
            fail();
        }
    }
    @Test
    public void getFollowingCount(){
        try{
            FollowerCountRequest request = new FollowerCountRequest(authToken, "@allen");
            CountResponse response = serverFacade.getFollowerCount(request, "/getFollowerCount");
            assertEquals(response.getCount(), 20);
        } catch (Exception e){
            System.out.print(e.getMessage());
            fail();
        }
    }

}
