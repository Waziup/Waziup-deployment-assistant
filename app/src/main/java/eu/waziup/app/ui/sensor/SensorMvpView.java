package eu.waziup.app.ui.sensor;

import java.util.List;

import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.base.MvpView;

/**
 * Created by KidusMT.
 */

public interface SensorMvpView extends MvpView {

    void showSensors(List<Sensor> sensorList);
    void openDetailSensorActivity(Sensor sensor);
    void loadPage();
}
