package eu.waziup.app.ui.device;

import eu.waziup.app.data.network.model.devices.Device;

public interface DevicesCommunicator {
    void onItemClicked(Device device);
    void showFab();
    void hideFab();
    boolean isFabShown();
    void visibleFab();
    void invisibleFab();
}
