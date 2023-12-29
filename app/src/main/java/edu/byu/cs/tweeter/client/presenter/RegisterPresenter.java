package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.ViewLoadUser;
import edu.byu.cs.tweeter.client.view.ViewSetErrorView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends Presenter<RegisterPresenter.View> {
    private final UserService userService;
    public RegisterPresenter(View view){
        super(view);
        this.userService = new UserService();
    }
    private class RegisterServiceObserver implements UserService.RegisterObserver{
        @Override
        public void changeActivity(User registeredUser) {
            view.changeActivityToUser(registeredUser);
        }
        @Override
        public void handleFailure(String message) {
            view.makeToast("Failed to register: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.makeToast("Failed to register because of exception: " + exception.getMessage());
        }
    }
    public void register(Editable firstName, Editable lastName, Editable alias, Editable password, Drawable drawable) {
        // Register and move to MainActivity.
        try {
            validateRegistration(firstName, lastName, alias, password, drawable);
            view.setErrorView(null);
            view.registerToast();
            Bitmap image = ((BitmapDrawable) drawable).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] imageBytes = bos.toByteArray();

            // Intentionally, Use the java Base64 encoder so it is compatible with M4.
            String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);
            userService.register(firstName.toString(), lastName.toString(), alias.toString(), password.toString(), imageBytesBase64, new RegisterServiceObserver());
        } catch (Exception e) {
            view.setErrorView(e.getMessage());
        }
    }

    public void validateRegistration(Editable firstName, Editable lastName, Editable alias, Editable password, Drawable imageToUpload) {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (imageToUpload == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }
    public interface View extends ViewSetErrorView {
        void registerToast();
    }

}
