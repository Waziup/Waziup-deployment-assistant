package eu.waziup.waziup_da_app.ui.register;

import eu.waziup.waziup_da_app.di.PerActivity;
import eu.waziup.waziup_da_app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface RegisterSensorMvpPresenter<V extends RegisterSensorMvpView> extends MvpPresenter<V> {

    void onSubmitClicked();

}
