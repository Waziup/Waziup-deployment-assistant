package eu.waziup.app.ui.sensordetail;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class SensorDetailPresenter<V extends SensorDetailMvpView> extends BasePresenter<V>
        implements SensorDetailMvpPresenter<V> {

    private static final String TAG = "QRScanPresenter";

    @Inject
    public SensorDetailPresenter(DataManager dataManager,
                                 SchedulerProvider schedulerProvider,
                                 CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onCancelClicked() {
        getMvpView().closeDialog();
    }
}
