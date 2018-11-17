package eu.waziup.waziup_da_app.ui.detail;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.data.network.model.sensor.Measurement;
import eu.waziup.waziup_da_app.ui.base.BasePresenter;
import eu.waziup.waziup_da_app.utils.CommonUtils;
import eu.waziup.waziup_da_app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class DetailSensorPresenter<V extends DetailSensorMvpView> extends BasePresenter<V>
        implements DetailSensorMvpPresenter<V> {

    private static final String TAG = "RegisterSensorPresenter";

    @Inject
    public DetailSensorPresenter(DataManager dataManager,
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

    @Override
    public void onDeploySensorClicked() {

    }

    @Override
    public void onUnDeploySensorClicked() {

    }

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
