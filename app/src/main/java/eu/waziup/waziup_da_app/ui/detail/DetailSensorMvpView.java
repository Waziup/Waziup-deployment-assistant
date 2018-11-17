package eu.waziup.waziup_da_app.ui.detail;

import java.util.List;

import eu.waziup.waziup_da_app.data.network.model.sensor.Measurement;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.ui.base.MvpView;

/**
 * Created by KidusMT.
 */

public interface DetailSensorMvpView extends MvpView {

    void showMeasurements(String sensorId, List<Measurement> measurements);

    void loadPage(Sensor sensor);

    void showCreateMeasurementsDialog();
}
