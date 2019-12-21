package eu.waziup.app.ui.notificationdetail;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

import eu.waziup.app.data.network.model.sensor.Device;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.base.MvpView;

/**
 * Created by KidusMT.
 */

public interface NotificationDetailMvpView extends MvpView {

    void showMeasurements(String sensorId, List<Sensor> sensors);

    void loadPage(Device device);

    void showCreateMeasurementsDialog();

    void openMapFragment(LatLng latLng);
}
