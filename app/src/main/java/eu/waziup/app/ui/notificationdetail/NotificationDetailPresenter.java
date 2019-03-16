package eu.waziup.app.ui.notificationdetail;

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

public class NotificationDetailPresenter<V extends NotificationDetailMvpView> extends BasePresenter<V>
        implements NotificationDetailMvpPresenter<V> {

    private static final String TAG = "QRScanPresenter";

    @Inject
    public NotificationDetailPresenter(DataManager dataManager,
                                       SchedulerProvider schedulerProvider,
                                       CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onEditMeasurementClicked(Measurement measurement) {

    }

    @Override
    public void onDeleteMeasurementClicked(String sensorId, String measurementId) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().deleteMeasurement(sensorId, measurementId)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui()).subscribe(responseBody -> {
                            if (!isViewAttached())
                                return;

                            loadMeasurements(sensorId);
                        }, throwable -> {
                            if (!isViewAttached())
                                return;

                            getMvpView().hideLoading();
                            getMvpView().onError(CommonUtils.getErrorMessage(throwable));
                        }
                ));
    }

//    @Override
//    public void onDeploySensorClicked() {
//
//    }

//    @Override
//    public void onUnDeploySensorClicked() {
//
//    }

    @Override
    public void onAddMeasurementClicked() {
        getMvpView().showCreateMeasurementsDialog();
    }

    @Override
    public void loadMeasurements(String sensorId) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().getMeasurements(sensorId)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(measurements -> {
                    if (!isViewAttached())
                        return;

                    getMvpView().showMeasurements(sensorId, measurements);
                }, throwable -> {
                    if (!isViewAttached())
                        return;

                    getMvpView().hideLoading();
                    getMvpView().onError(CommonUtils.getErrorMessage(throwable));
                }));
    }
}
