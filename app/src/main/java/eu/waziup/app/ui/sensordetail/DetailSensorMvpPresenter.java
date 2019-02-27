package eu.waziup.app.ui.sensordetail;

import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface DetailSensorMvpPresenter<V extends DetailSensorMvpView> extends MvpPresenter<V> {

    void onEditMeasurementClicked(Measurement measurement);

    void onDeleteMeasurementClicked(String sensorId, String measurementId);

    void onDeploySensorClicked();

    void onUnDeploySensorClicked();

    void onAddMeasurementClicked();

    void loadMeasurements(String sensorId);

//    void onLocateOnMapClicked(); // todo find a better way
}
