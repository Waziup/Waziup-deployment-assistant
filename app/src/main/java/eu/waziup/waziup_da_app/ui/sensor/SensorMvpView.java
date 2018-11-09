package eu.waziup.waziup_da_app.ui.sensor;

import java.util.List;

import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.ui.base.MvpView;

/**
 * Created by KidusMT.
 */

public interface SensorMvpView extends MvpView {

    void openRegisterSensorActivity();
    void showSensors(List<Sensor> sensorList);
    void openLoginActivity();
    void openDetailSensorActivity(Sensor sensor);
}
