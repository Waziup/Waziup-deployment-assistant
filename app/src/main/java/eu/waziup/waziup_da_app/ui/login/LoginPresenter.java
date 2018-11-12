package eu.waziup.waziup_da_app.ui.login;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.data.network.model.ApiError;
import eu.waziup.waziup_da_app.data.network.model.LoginRequest;
import eu.waziup.waziup_da_app.ui.base.BasePresenter;
import eu.waziup.waziup_da_app.utils.CommonUtils;
import eu.waziup.waziup_da_app.utils.ErrorUtils;
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
                .subscribe(s -> {

                    if (!isViewAttached()) {
                        return;
                    }
                    getDataManager().updateUserInfo( s, DataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
                    getMvpView().openSensorActivity();

                }, throwable -> {

                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideLoading();
                    getMvpView().onError(CommonUtils.getErrorMessage(throwable));
                }));
    }

    public void onDecideNextActivity() {
        if (getDataManager().getCurrentUserLoggedInMode()
                == DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT.getType()) {
           return;
        } else {
            getMvpView().openSensorActivity();
        }
    }

    @Override
    public void onAttach(V mvpView) {
        super.onAttach(mvpView);
        onDecideNextActivity();
    }
}
