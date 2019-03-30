package eu.waziup.app.ui.notification;

import android.util.Log;

import javax.inject.Inject;

import eu.waziup.app.DaApp;
import eu.waziup.app.data.DataManager;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.CommonUtils;
import eu.waziup.app.utils.ConnectivityUtil;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class NotificationPresenter<V extends NotificationMvpView> extends BasePresenter<V>
        implements NotificationMvpPresenter<V> {

    @Inject
    public NotificationPresenter(DataManager dataManager,
                                 SchedulerProvider schedulerProvider,
                                 CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadNotifications() {
        if (ConnectivityUtil.isConnectedMobile(DaApp.getContext()) || ConnectivityUtil.isConnectedWifi(DaApp.getContext())) {
            getMvpView().showLoading();
            getCompositeDisposable().add(getDataManager().getNotifications()
                    .subscribeOn(getSchedulerProvider().io())
                    .observeOn(getSchedulerProvider().ui())
                    .subscribe(notificationResponses -> {
                        if (!isViewAttached())
                            return;

                        getMvpView().showNotifications(notificationResponses);

                    }, throwable -> {

                        if (!isViewAttached())
                            return;

                        getMvpView().hideLoading();
                        getMvpView().onError(CommonUtils.getErrorMessage(throwable));

                    }));
        } else {
            getMvpView().showNetworkErrorPage();
        }

    }
}
