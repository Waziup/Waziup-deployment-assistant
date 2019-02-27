package eu.waziup.app.ui.register;

import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface RegisterSensorMvpPresenter<V extends RegisterSensorMvpView> extends MvpPresenter<V> {

    void onSubmitRegisterClicked(Sensor sensor);
}
