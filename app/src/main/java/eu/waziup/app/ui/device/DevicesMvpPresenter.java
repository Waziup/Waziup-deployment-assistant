package eu.waziup.app.ui.device;

import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface DevicesMvpPresenter<V extends DevicesMvpView> extends MvpPresenter<V> {

    void onLogOutClicked();
    void loadSensors();
}
