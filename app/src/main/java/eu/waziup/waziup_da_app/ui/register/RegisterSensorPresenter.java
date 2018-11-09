package eu.waziup.waziup_da_app.ui.register;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.ui.base.BasePresenter;
import eu.waziup.waziup_da_app.utils.CommonUtils;
import eu.waziup.waziup_da_app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class RegisterSensorPresenter<V extends RegisterSensorMvpView> extends BasePresenter<V>
        implements RegisterSensorMvpPresenter<V> {

    private static final String TAG = "RegisterSensorPresenter";

    @Inject
    public RegisterSensorPresenter(DataManager dataManager,
                                   SchedulerProvider schedulerProvider,
                                   CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onSubmitClicked() {
        getMvpView().showLoading();
//        getCompositeDisposable().add(getDataManager().)
        getMvpView().hideLoading();
    }
}
