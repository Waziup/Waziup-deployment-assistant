package eu.waziup.app.ui.sensoredit;

import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface EditMeasurementMvpPresenter<V extends EditMeasurementMvpView> extends MvpPresenter<V> {

    void onSubmitClicked();

    void onCancelClicked();
}
