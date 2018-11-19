package eu.waziup.waziup_da_app.ui.map;

import java.util.List;

import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.ui.base.MvpView;

public interface MapMvpView extends MvpView {

    void showSensorsOnMap(List<Sensor> sensors);
}
