package eu.waziup.app.ui.sensor;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.CommonUtils;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class SensorPresenter<V extends SensorMvpView> extends BasePresenter<V>
        implements SensorMvpPresenter<V> {

    private static final String TAG = "SensorPresenter";

    @Inject
    public SensorPresenter(DataManager dataManager,
                           SchedulerProvider schedulerProvider,
                           CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onLogOutClicked() {
        // removing the token when the user logout
        getDataManager().setUserAsLoggedOut();
//        getMvpView().openLoginActivity();
    }

    @Override
    public void loadSensors() {
//        if (ConnectivityUtil.isConnectedMobile(DaApp.getContext()) || ConnectivityUtil.isConnectedWifi(DaApp.getContext())) {
        if (getMvpView().isNetworkConnected()) {
            getMvpView().showLoading();
            getCompositeDisposable().add(getDataManager().fetchSensors()//fetchSensors(1000, 0)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(
                            sensors -> {
                                if (!isViewAttached())
                                    return;

                                getMvpView().showSensors(sensors);

                            }, throwable -> {

                                if (!isViewAttached())
                                    return;

                                getMvpView().hideLoading();
                                getMvpView().onError(CommonUtils.getErrorMessage(throwable));

                            }
                    ));
        } else {
            getMvpView().showNetworkErrorPage();
        }

    }

}
