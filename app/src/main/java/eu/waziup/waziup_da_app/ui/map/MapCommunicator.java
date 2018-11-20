package eu.waziup.waziup_da_app.ui.map;

import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;

public interface MapCommunicator {

    void onMarkerClicked(Sensor sensor, String parentFragment);
}
