package eu.waziup.app.ui.sensor;

import eu.waziup.app.data.network.model.sensor.Measurement;

public interface SensorMethod {

    void loadMeasurement(Measurement measurement);
}
