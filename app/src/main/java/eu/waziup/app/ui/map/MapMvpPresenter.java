package eu.waziup.app.ui.map;

import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface MapMvpPresenter<V extends MapMvpView> extends MvpPresenter<V> {

    void loadSensors();
}
