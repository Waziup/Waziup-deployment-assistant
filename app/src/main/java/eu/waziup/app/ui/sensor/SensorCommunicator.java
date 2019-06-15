package eu.waziup.app.ui.sensor;

import eu.waziup.app.data.network.model.devices.Device;
import eu.waziup.app.data.network.model.sensor.Sensor;

public interface SensorCommunicator {
    void onItemClicked(Device device);
    void showFab();
    void hideFab();
    boolean isFabShown();
    void visibleFab();
    void invisibleFab();
}
