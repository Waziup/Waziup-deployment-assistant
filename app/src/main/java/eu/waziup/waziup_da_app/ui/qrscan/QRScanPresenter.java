package eu.waziup.waziup_da_app.ui.qrscan;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.ui.base.BasePresenter;
import eu.waziup.waziup_da_app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class QRScanPresenter<V extends QRScanMvpView> extends BasePresenter<V>
        implements QRScanMvpPresenter<V> {

    private static final String TAG = "QRScanPresenter";

    @Inject
    public QRScanPresenter(DataManager dataManager,
                           SchedulerProvider schedulerProvider,
                           CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

}
