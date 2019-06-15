package eu.waziup.app.ui.map;

import eu.waziup.app.data.network.model.devices.Device;
import eu.waziup.app.data.network.model.sensor.Sensor;

public interface MapCommunicator {

    void onMarkerClicked(Device device, String parentFragment);
}
