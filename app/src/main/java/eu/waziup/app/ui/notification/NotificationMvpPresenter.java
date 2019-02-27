package eu.waziup.app.ui.notification;

import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface NotificationMvpPresenter<V extends NotificationMvpView> extends MvpPresenter<V> {

    void loadNotifications();
}
