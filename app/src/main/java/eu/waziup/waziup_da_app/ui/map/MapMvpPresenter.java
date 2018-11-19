package eu.waziup.waziup_da_app.ui.map;

import eu.waziup.waziup_da_app.di.PerActivity;
import eu.waziup.waziup_da_app.ui.base.MvpPresenter;
import eu.waziup.waziup_da_app.ui.register.RegisterSensorMvpView;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface MapMvpPresenter<V extends MapMvpView> extends MvpPresenter<V> {

    void loadSensors();
}
