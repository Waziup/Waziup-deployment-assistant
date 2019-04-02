package eu.waziup.app.ui.measurementdetail;

import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface MeasurementDetailMvpPresenter<V extends MeasurementDetailMvpView> extends MvpPresenter<V> {

    void onSubmitClicked();

    void onCancelClicked();
}
