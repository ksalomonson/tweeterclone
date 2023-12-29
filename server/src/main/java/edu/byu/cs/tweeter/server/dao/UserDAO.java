package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.FakeData;
//DEPRECIATED, REMOVE
public class UserDAO {
    public User getUser(String userAlias){
        return getFakeData().findUserByAlias(userAlias);
    }
    public FakeData getFakeData(){
        return FakeData.getInstance();
    }
}
