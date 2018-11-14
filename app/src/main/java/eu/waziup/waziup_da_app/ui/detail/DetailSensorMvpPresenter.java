package eu.waziup.waziup_da_app.ui.detail;

import eu.waziup.waziup_da_app.data.network.model.sensor.Measurement;
import eu.waziup.waziup_da_app.di.PerActivity;
import eu.waziup.waziup_da_app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface DetailSensorMvpPresenter<V extends DetailSensorMvpView> extends MvpPresenter<V> {

    void onEditMeasurementClicked(Measurement measurement);

    void onDeleteMeasurementClicked(Measurement measurement);

    void onDeploySensorClicked();

    void onUndeploySensorClicked();

//    void onLocateOnMapClicked(); // todo find a better way
}
