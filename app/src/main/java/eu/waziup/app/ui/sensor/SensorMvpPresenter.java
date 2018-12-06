package eu.waziup.app.ui.sensor;

import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface SensorMvpPresenter<V extends SensorMvpView> extends MvpPresenter<V> {

    void onLogOutClicked();
    void loadSensors();
}
