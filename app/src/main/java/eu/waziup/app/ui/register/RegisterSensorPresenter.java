package eu.waziup.app.ui.register;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.data.network.model.sensor.Device;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.CommonUtils;
import eu.waziup.app.utils.rx.SchedulerProvider;
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
    public void onSubmitRegisterClicked(Device device) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().registerSensor(device)
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
