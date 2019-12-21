package eu.waziup.app.ui.map;

import eu.waziup.app.data.network.model.devices.Device;

public interface MapCommunicator {

    void onMarkerClicked(Device device, String parentFragment);
}
