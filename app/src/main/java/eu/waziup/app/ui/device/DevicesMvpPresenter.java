package eu.waziup.app.ui.device;

import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface DevicesMvpPresenter<V extends DevicesMvpView> extends MvpPresenter<V> {

    // for logging out the user if authentication with username and password is required
    void onLogOutClicked();
    // handled loading fetching the devices list from API and display the items found on the screen
    void loadSensors();
}
