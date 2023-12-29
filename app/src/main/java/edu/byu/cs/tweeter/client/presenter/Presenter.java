package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.observer.Observer;
import edu.byu.cs.tweeter.client.view.ViewInterface;

public abstract class Presenter<T extends ViewInterface> {
    protected T view;
    Presenter(T view){
        this.view = view;
    }

}
