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
                    // todo this part should be removed later when fixed on the api side
                    getDataManager().setCurrentUserName("Corentin Dupont");
                    getDataManager().setCurrentUserEmail("cdupont@fbk.eu");

                }, throwable -> {

                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideLoading();
                    getMvpView().onError(CommonUtils.getErrorMessage(throwable));
                }));
    }

    @Override
    public void onSaveUserInfo(String username, String email, String profilePic) {
        getDataManager().setCurrentUserName(username);
        getDataManager().setCurrentUserEmail(email);
        getDataManager().setCurrentUserProfilePicUrl(profilePic);
    }

    @Override
    public void onSaveName(String username) {
        getDataManager().setCurrentUserName(username);
    }

    @Override
    public void onSavePicture(String pic) {
        getDataManager().setCurrentUserProfilePicUrl(pic);
    }

    @Override
    public void updateUserInfo(String s, DataManager.LoggedInMode mode) {
        getDataManager().updateUserInfo(s, DataManager.LoggedInMode.LOGGED_IN_MODE_SERVER);
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
