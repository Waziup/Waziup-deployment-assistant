package eu.waziup.app.ui.main;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.data.network.model.devices.Device;
import eu.waziup.app.data.network.model.user.User;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.CommonUtils;
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
    public void onSensorItemClicked(Device device, String parent) {
        getMvpView().lockDrawer();
        getMvpView().openSensorDetailFragment(device, parent);
    }

    @Override
    public boolean onUserLoggedIn() {
        return getDataManager().getCurrentUserLoggedInMode() != DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT.getType();
    }

    @Override
    public void initializedView() {
        getMvpView().updateUserName(getDataManager().getCurrentUserName());
        getMvpView().updateUserProfilePic(getDataManager().getCurrentUserProfilePicUrl());
    }

    @Override
    public void updateAccessToken(String accessToken) {
        getDataManager().updateApiHeader(accessToken);
        getDataManager().updateUserToken(accessToken);
    }

    @Override
    public void updateUserInfo(String name, String preferredName, String givenName, String familyName, String email) {
        if (name != null && !TextUtils.isEmpty(name)) {
            getDataManager().setCurrentUserName(name);
        } else if (preferredName != null && !TextUtils.isEmpty(preferredName)) {
            getDataManager().setCurrentUserName(preferredName);
        } else if (givenName != null && !TextUtils.isEmpty(givenName)) {
            getDataManager().setCurrentUserName(givenName);
        } else if (familyName != null && !TextUtils.isEmpty(familyName)) {
            getDataManager().setCurrentUserName(familyName);
        }

        if (email != null && !TextUtils.isEmpty(email)) {
            getDataManager().setCurrentUserEmail(email);
        }
    }

    private User filterByUsername(List<User> users, String predicate) {
        List<User> filteredList = new ArrayList<>();

        for (User user : users) {
            if (user.getUsername().equals(predicate))
                filteredList.add(user);
        }

        // todo get the first user found in the list
        if (filteredList.size() > 0) {
            return filteredList.get(0);
        } else {
            return null;
        }

    }

    @Override
    public void fetchUserInfo(String username) {
        // the username is used as a filtering name
        getCompositeDisposable().add(getDataManager()
                .getUsers()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(users -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    // filtering the users and updating the user information
                    User foundUser = filterByUsername(users, username);
                    if (foundUser != null)
                        updateUserInfo(String.format("%s %s", foundUser.getFirstName(), foundUser.getFirstName()),
                                foundUser.getFirstName(), foundUser.getFirstName(), foundUser.getLastName(), foundUser.getEmail());

                }, throwable -> {

                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideLoading();
                    getMvpView().onError(CommonUtils.getErrorMessage(throwable));
                }));
    }

    @Override
    public void setLoggedInMode() {
        getDataManager().setLoggedInMode();
    }

}
