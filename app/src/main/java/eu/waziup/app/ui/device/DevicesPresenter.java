package eu.waziup.app.ui.device;

import android.util.Log;

import java.lang.annotation.Target;
import java.util.ArrayList;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.CommonUtils;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class DevicesPresenter<V extends DevicesMvpView> extends BasePresenter<V>
        implements DevicesMvpPresenter<V> {

    private static final String TAG = "DevicesPresenter";

    @Inject
    public DevicesPresenter(DataManager dataManager,
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
            getCompositeDisposable().add(getDataManager()
                    .fetchSensors("cdupont")//getDataManager().getCurrentUserName())//fetchSensors(1000, 0)
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(
                            devices -> {
                                Log.e("------>", "==========");
                                if (!isViewAttached())
                                    return;
                                getMvpView().showSensors(devices);

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
