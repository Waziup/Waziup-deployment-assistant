package eu.waziup.app.ui.measurementdetail;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class MeasurementDetailDetailPresenter<V extends MeasurementDetailMvpView> extends BasePresenter<V>
        implements MeasurementDetailMvpPresenter<V> {

    private static final String TAG = "QRScanPresenter";

    @Inject
    public MeasurementDetailDetailPresenter(DataManager dataManager,
                                            SchedulerProvider schedulerProvider,
                                            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onSubmitClicked() {

    }

    @Override
    public void onCancelClicked() {
        getMvpView().closeDialog();
    }

//    @Override
//    public void onEditMeasurementClicked(Measurement measurement) {
//
//    }

//    @Override
//    public void onDeleteMeasurementClicked(String sensorId, String measurementId) {
//        getMvpView().showLoading();
//        getCompositeDisposable().add(getDataManager().deleteMeasurement(sensorId, measurementId)
//                .subscribeOn(getSchedulerProvider().io())
//                .observeOn(getSchedulerProvider().ui()).subscribe(responseBody -> {
//                            if (!isViewAttached())
//                                return;
//
//                            loadMeasurements(sensorId);
//                        }, throwable -> {
//                            if (!isViewAttached())
//                                return;
//
//                            getMvpView().hideLoading();
//                            getMvpView().onError(CommonUtils.getErrorMessage(throwable));
//                        }
//                ));
//    }
}
