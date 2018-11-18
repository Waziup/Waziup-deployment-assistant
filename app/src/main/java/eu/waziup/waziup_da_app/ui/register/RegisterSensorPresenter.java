package eu.waziup.waziup_da_app.ui.register;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.ui.base.BasePresenter;
import eu.waziup.waziup_da_app.utils.CommonUtils;
import eu.waziup.waziup_da_app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class RegisterSensorPresenter<V extends RegisterSensorMvpView> extends BasePresenter<V>
        implements RegisterSensorMvpPresenter<V> {

    private static final String TAG = "QRScanPresenter";

    @Inject
    public RegisterSensorPresenter(DataManager dataManager,
                                   SchedulerProvider schedulerProvider,
                                   CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onSubmitRegisterClicked(Sensor sensor) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().registerSensor(sensor)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(registerSensorResponse -> {
                    if (!isViewAttached())
                        return;

                    getMvpView().openSensorListFragment();
                }, throwable -> {
                    if (!isViewAttached())
                        return;

                    getMvpView().hideLoading();
                    getMvpView().onError(CommonUtils.getErrorMessage(throwable));
                }));
    }
}
