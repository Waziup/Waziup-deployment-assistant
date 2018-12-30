package eu.waziup.app.ui.main;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V>
        implements MainMvpPresenter<V> {

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
        getMvpView().openRegistrationSensor();
    }

    @Override
    public void onSensorItemClicked(Sensor sensor, String parent) {
        getMvpView().lockDrawer();
        getMvpView().openSensorDetailFragment(sensor, parent);
    }

    @Override
    public void initializedView() {
        getMvpView().updateUserName(getDataManager().getCurrentUserName());
        getMvpView().updateUserProfilePic(getDataManager().getCurrentUserProfilePicUrl());
    }

}
