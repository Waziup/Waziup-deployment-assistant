package eu.waziup.app.ui.device;

import eu.waziup.app.data.network.model.devices.Device;

public interface DevicesCommunicator {
    // method interface handling the onItemClick of the devices from the list
    void onItemClicked(Device device);
    // handle the visibility of the floating action button on the fragment screen
    void showFab();
    // handle the visibility of the floating action button on the fragment screen
    void hideFab();
    // checks if the floating action button is already visible on the screen
    boolean isFabShown();
    // sets the floating action button's visibility to VISIBLE
    void visibleFab();
    // sets the floating action button's visibility to INVISIBLE
    void invisibleFab();
}
