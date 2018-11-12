package eu.waziup.waziup_da_app.ui.main;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.ui.base.BasePresenter;
import eu.waziup.waziup_da_app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V>
        implements MainMvpPresenter<V> {

    private static final String TAG = "RegisterSensorPresenter";

    @Inject
    public MainPresenter(DataManager dataManager,
                         SchedulerProvider schedulerProvider,
                         CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onLogOutClicked() {
        // removing the token when the user logout
        getDataManager().setUserAsLoggedOut();
        getMvpView().openLoginActivity();
    }


    @Override
    public void onNavMenuCreated() {
        if (!isViewAttached()) {
            return;
        }

        final String currentUserName = getDataManager().getCurrentUserName();
        if (currentUserName != null && !currentUserName.isEmpty()) {
            getMvpView().updateUserName(currentUserName);
        }

        final String currentUserEmail = getDataManager().getCurrentUserEmail();
        if (currentUserEmail != null && !currentUserEmail.isEmpty()) {
            getMvpView().updateUserEmail(currentUserEmail);
        }

        final String profilePicUrl = getDataManager().getCurrentUserProfilePicUrl();
        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
            getMvpView().updateUserProfilePic(profilePicUrl);
        }
    }

    @Override
    public void onFabClicked() {
        getMvpView().lockDrawer();
        getMvpView().openRegisterationSensor();
    }

}
