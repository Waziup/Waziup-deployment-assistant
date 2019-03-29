package eu.waziup.app.ui.neterror;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.CommonUtils;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class ErrorNetworkPresenter<V extends ErrorNetworkMvpView> extends BasePresenter<V>
        implements ErrorNetworkMvpPresenter<V> {

    @Inject
    public ErrorNetworkPresenter(DataManager dataManager,
                                 SchedulerProvider schedulerProvider,
                                 CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onRefreshClicked(String parent) {
        getMvpView().showSensorFragment(parent);
    }
}
