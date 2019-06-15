package eu.waziup.app.ui.device;

import java.util.List;

import eu.waziup.app.data.network.model.devices.Device;
import eu.waziup.app.ui.base.MvpView;

/**
 * Created by KidusMT.
 */

public interface DevicesMvpView extends MvpView {

    void showSensors(List<Device> deviceList);
    void loadPage();
    void showNetworkErrorPage();
}
