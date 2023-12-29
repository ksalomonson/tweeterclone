package edu.byu.cs.tweeter.client.presenter;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.ToastableUserServiceObserver;
import edu.byu.cs.tweeter.client.view.ViewSetErrorView;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends Presenter<ViewSetErrorView> {
    public void login(String text, String text1) {
        try {
            validateLogin(text, text1);
            view.setErrorView(null);
            view.makeToast("Logging In...");
            userService.login(text, text1, new LoginServiceObserver());
        } catch (Exception e) {
            view.setErrorView(e.getMessage());
        }
    }
    private final UserService userService;
    public LoginPresenter(ViewSetErrorView view){
        super(view);
        userService = new UserService();
    }
    public void validateLogin(String userAlias, String passPhrase) {
        if (userAlias.length() > 0 && userAlias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (userAlias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (passPhrase.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }
    private class LoginServiceObserver implements ToastableUserServiceObserver {
        @Override
        public void changeActivityToUser(User loggedInUser) {
            view.changeActivityToUser(loggedInUser);
        }
        @Override
        public void handleFailure(String message) {
            view.makeToast("Failed to login: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.makeToast("Failed to login because of exception: " + exception.getMessage());
        }

        @Override
        public void makeToast(String s) {
            view.makeToast(s);
        }
    }
}
