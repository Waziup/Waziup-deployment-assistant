package eu.waziup.app.ui.login;

import javax.inject.Inject;

import eu.waziup.app.R;
import eu.waziup.app.data.DataManager;
import eu.waziup.app.data.network.model.LoginRequest;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.CommonUtils;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class LoginPresenter<V extends LoginMvpView> extends BasePresenter<V>
        implements LoginMvpPresenter<V> {

    private static final String TAG = "LoginPresenter";

    @Inject
    public LoginPresenter(DataManager dataManager,
                          SchedulerProvider schedulerProvider,
                          CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    public void onDecideNextActivity() {
        if (getDataManager().getCurrentUserLoggedInMode()
                != DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT.getType()) {
            getMvpView().openSensorActivity();
        }
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
        onDecideNextActivity();
    }
}
