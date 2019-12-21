package eu.waziup.app.ui.devicesdetail;

import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface DetailDevicesMvpPresenter<V extends DetailSensorMvpView> extends MvpPresenter<V> {

    // method interface for handling the edit button click on sensor item
    void onEditSensorClicked(Sensor sensor);

    // method interface for handling the delete sensor click
    void onDeleteSensorClicked(String deviceId, String sensorId);

    // method interface for handling deploying device
    void onDeployDevicesClicked();

    // method interface for handling the undeploying device
    void onUnDeployDevicesClicked();

    // method interface for handling the sensor addition
    void onAddSensorsClicked();

    // method interface for handling loading sensors on screen
    void loadSensors(String deviceId);

//    void onLocateOnMapClicked(); // todo find a better way
}
