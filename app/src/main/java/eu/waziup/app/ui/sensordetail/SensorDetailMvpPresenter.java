package eu.waziup.app.ui.sensordetail;

import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface SensorDetailMvpPresenter<V extends SensorDetailMvpView> extends MvpPresenter<V> {

    void onCancelClicked();
}
