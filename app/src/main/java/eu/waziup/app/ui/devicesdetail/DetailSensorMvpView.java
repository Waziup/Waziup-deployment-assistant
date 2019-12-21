package eu.waziup.app.ui.devicesdetail;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

import eu.waziup.app.data.network.model.sensor.Device;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.base.MvpView;

/**
 * Created by KidusMT.
 */

public interface DetailSensorMvpView extends MvpView {

    // method for showing the sensors with deviceId filter and list of sensor
    void showSensors(String devicesId, List<Sensor> sensors);

    // method for loading the page - refreshing the screen
    void loadPage(Device device);

    void showCreateSensorsDialog();

    void openMapFragment(LatLng latLng);
}
