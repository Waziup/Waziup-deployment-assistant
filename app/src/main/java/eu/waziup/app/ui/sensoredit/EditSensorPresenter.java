package eu.waziup.app.ui.sensoredit;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class EditSensorPresenter<V extends EditSensorMvpView> extends BasePresenter<V>
        implements EditSensorMvpPresenter<V> {

    private static final String TAG = "QRScanPresenter";

    @Inject
    public EditSensorPresenter(DataManager dataManager,
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
//    public void onEditSensorClicked(Sensor measurement) {
//
//    }

//    @Override
//    public void onDeleteSensorClicked(String sensorId, String measurementId) {
//        getMvpView().showLoading();
//        getCompositeDisposable().add(getDataManager().deleteMeasurement(sensorId, measurementId)
//                .subscribeOn(getSchedulerProvider().io())
//                .observeOn(getSchedulerProvider().ui()).subscribe(responseBody -> {
//                            if (!isViewAttached())
//                                return;
//
//                            loadSensors(sensorId);
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
