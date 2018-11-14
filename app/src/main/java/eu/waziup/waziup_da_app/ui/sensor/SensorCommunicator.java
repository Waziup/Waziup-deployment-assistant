package eu.waziup.waziup_da_app.ui.sensor;

import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;

public interface SensorCommunicator {

    void fabClicked();
    void onItemClicked(Sensor sensor);
}
