package eu.waziup.app.ui.notificationdetail;

import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface NotificationDetailMvpPresenter<V extends NotificationDetailMvpView> extends MvpPresenter<V> {

    void onEditMeasurementClicked(Sensor sensor);

    void onDeleteMeasurementClicked(String sensorId, String measurementId);

//    void onDeployDevicesClicked();
//
//    void onUnDeployDevicesClicked();

    void onAddMeasurementClicked();

    void loadMeasurements(String sensorId);

//    void onLocateOnMapClicked(); // todo find a better way
}
