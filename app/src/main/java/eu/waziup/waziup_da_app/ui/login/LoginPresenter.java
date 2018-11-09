package eu.waziup.waziup_da_app.ui.login;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.ui.base.BasePresenter;
import eu.waziup.waziup_da_app.utils.rx.SchedulerProvider;
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

    @Override
    public void onServerLoginClick(String email, String password, String imei) {
//        validate email and password
//        Log.e("---->imei", imei);
//        if (email == null || email.isEmpty()) {
//            getMvpView().onError(R.string.empty_email);
//            return;
//        }
//
//        if (password == null || password.isEmpty()) {
//            getMvpView().onError(R.string.empty_password);
//            return;
//        }

//        getMvpView().showLoading();

//        getCompositeDisposable().add(getDataManager()
//                .doServerLoginApiCall(new LoginRequest.ServerLoginRequest(email, password, "1234567"))
//                .subscribeOn(getSchedulerProvider().io())
//                .observeOn(getSchedulerProvider().ui())
//                .subscribe(loginResponse -> {
//
//                    getDataManager().updateUserInfo(loginResponse.getId(),
//                            null,
//                            DataManager.LoggedInMode.LOGGED_IN_MODE_SERVER,
//                            loginResponse.getFullName(),
//                            loginResponse.getUsername(),
//                            loginResponse.getPhoneNo(),
//                            loginResponse.getRole(),
//                            loginResponse.getEmail(),
//                            null);
//
//                    if (!isViewAttached()) {
//                        return;
//                    }
//
//
//                    if (getMvpView().getDeviceToken())
//                        getMvpView().openMainActivity();
//
//                }, throwable -> {
//
//                    if (!isViewAttached()) {
//                        return;
//                    }
//
//                    getMvpView().hideLoading();
//                    getMvpView().onError(CommonUtils.getErrorMessage(throwable));
//                }));
    }


    @Override
    public void onForgetPasswordClicked() {
        getMvpView().showLoading();
        getMvpView().openForgetPasswordActivity();
        getMvpView().hideLoading();
    }
}
