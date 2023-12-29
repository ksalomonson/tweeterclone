package edu.byu.cs.tweeter.client.model.service.observer;

public interface ToastableUserServiceObserver extends UserServiceObserver{
    void makeToast(String s);
}
