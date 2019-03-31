package eu.waziup.app.ui.measurementedit;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.CommonUtils;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class EditMeasurementPresenter<V extends EditMeasurementMvpView> extends BasePresenter<V>
        implements EditMeasurementMvpPresenter<V> {

    private static final String TAG = "QRScanPresenter";

    @Inject
    public EditMeasurementPresenter(DataManager dataManager,
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
