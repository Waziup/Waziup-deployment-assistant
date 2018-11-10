package eu.waziup.waziup_da_app.ui.login;

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

public class LoginPresenter<V extends LoginMvpView> extends BasePresenter<V>
        implements LoginMvpPresenter<V> {

    private static final String TAG = "RegisterSensorPresenter";

    @Inject
    public LoginPresenter(DataManager dataManager,
                          SchedulerProvider schedulerProvider,
                          CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onServerLoginClick(String email, String password) {
//        validate email and password
        if (email == null || email.isEmpty()) {
            getMvpView().onError(R.string.empty_email);
            return;
        }

        if (password == null || password.isEmpty()) {
            getMvpView().onError(R.string.empty_password);
            return;
        }

        getMvpView().showLoading();

        getCompositeDisposable().add(getDataManager()
                .serverLogin(new LoginRequest.ServerLoginRequest(email, password))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(loginResponse -> {

                    // this is the token responded by the api after the user login.
                    getDataManager().updateUserInfo(loginResponse);

                    if (!isViewAttached()) {
                        return;
                    }

//                    if (getMvpView().getDeviceToken())
                    getMvpView().openSensorActivity();

                }, throwable -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();
                    getMvpView().onError(CommonUtils.getErrorMessage(throwable));
                }));
    }

    @Override
    public void onDecideNextActivity() {
        if (!(getDataManager().getCurrentUserLoggedInMode()
                == DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT.getType())) {
//            getMvpView().openActivityOnTokenExpire();
            return;
        } else {
            getMvpView().openSensorActivity();
        }
    }


}
