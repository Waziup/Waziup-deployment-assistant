package eu.waziup.app.ui.measurementdetail;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class MeasurementDetailPresenter<V extends MeasurementDetailMvpView> extends BasePresenter<V>
        implements MeasurementDetailMvpPresenter<V> {

    private static final String TAG = "QRScanPresenter";

    @Inject
    public MeasurementDetailPresenter(DataManager dataManager,
                                      SchedulerProvider schedulerProvider,
                                      CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onCancelClicked() {
        getMvpView().closeDialog();
    }
}
