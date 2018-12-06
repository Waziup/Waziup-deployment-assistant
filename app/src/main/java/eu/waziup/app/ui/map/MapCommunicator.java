package eu.waziup.app.ui.map;

import eu.waziup.app.data.network.model.sensor.Sensor;

public interface MapCommunicator {

    void onMarkerClicked(Sensor sensor, String parentFragment);
}
