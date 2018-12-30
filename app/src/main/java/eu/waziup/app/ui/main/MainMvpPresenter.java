package eu.waziup.app.ui.main;

import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void onLogOutClicked();

    void onNavMenuCreated();

    void onFabClicked();

    void onSensorItemClicked(Sensor sensor, String parent);

    void initializedView();
}
