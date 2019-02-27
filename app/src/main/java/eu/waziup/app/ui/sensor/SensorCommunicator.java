package eu.waziup.app.ui.sensor;

import eu.waziup.app.data.network.model.sensor.Sensor;

public interface SensorCommunicator {

    void fabClicked();
    void onItemClicked(Sensor sensor);
}
