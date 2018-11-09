package eu.waziup.waziup_da_app.ui.detail;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.data.network.model.LoginRequest;
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
}
