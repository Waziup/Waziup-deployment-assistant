package eu.waziup.app.ui.notificationdetail;

import java.util.List;

import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.base.MvpView;

/**
 * Created by KidusMT.
 */

public interface NotificationDetailMvpView extends MvpView {

    void showMeasurements(String sensorId, List<Measurement> measurements);

    void loadPage(Sensor sensor);

    void showCreateMeasurementsDialog();

    void openMapFragment();
}
