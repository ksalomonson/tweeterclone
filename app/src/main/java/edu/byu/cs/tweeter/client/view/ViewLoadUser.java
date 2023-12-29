package edu.byu.cs.tweeter.client.view;

import edu.byu.cs.tweeter.model.domain.User;

public interface ViewLoadUser extends ViewInterface{
    void changeActivityToUser(User user);
    void setIsLoading(boolean b);
}
