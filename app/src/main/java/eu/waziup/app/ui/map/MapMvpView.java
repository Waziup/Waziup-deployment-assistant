package eu.waziup.app.ui.map;

import java.util.List;

import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.base.MvpView;

public interface MapMvpView extends MvpView {

    void showSensorsOnMap(List<Sensor> sensors);
}
