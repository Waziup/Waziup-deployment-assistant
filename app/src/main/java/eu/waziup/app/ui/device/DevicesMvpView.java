package eu.waziup.app.ui.device;

import java.util.List;

import eu.waziup.app.data.network.model.devices.Device;
import eu.waziup.app.ui.base.MvpView;

/**
 * Created by KidusMT.
 */

public interface DevicesMvpView extends MvpView {

    // method interface for showing the devices list
    void showDevices(List<Device> deviceList);
    // method interface for loadingPage for refreshing the screen
    void loadPage();
    // method interface for handling the network error and display the message
    void showNetworkErrorPage();
}
