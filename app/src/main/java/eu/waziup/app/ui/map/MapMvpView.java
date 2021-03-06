package eu.waziup.app.ui.map;

import java.util.List;

import eu.waziup.app.data.network.model.devices.Device;
import eu.waziup.app.ui.base.MvpView;

public interface MapMvpView extends MvpView {

    void showSensorsOnMap(List<Device> sensors);
}
