package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.Observer;
import edu.byu.cs.tweeter.client.view.ViewInterface;

public abstract class PagedPresenter<T extends ViewInterface> extends Presenter {
    PagedPresenter(T view) {
        super(view);
    }


}
