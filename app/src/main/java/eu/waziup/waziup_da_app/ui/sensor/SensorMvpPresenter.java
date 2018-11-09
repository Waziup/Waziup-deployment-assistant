package eu.waziup.waziup_da_app.ui.sensor;

import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.di.PerActivity;
import eu.waziup.waziup_da_app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface SensorMvpPresenter<V extends SensorMvpView> extends MvpPresenter<V> {

    void onLogOutClicked();
    void loadSensors();
    void onRegisterFabClicked();
    void onSensorItemClicked(Sensor sensor);
}
