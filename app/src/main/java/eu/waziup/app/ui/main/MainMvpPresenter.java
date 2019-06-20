package eu.waziup.app.ui.main;

import eu.waziup.app.data.network.model.devices.Device;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void onLogOutClicked();

    void onNavMenuCreated();

    void onFabClicked();

    void onSensorItemClicked(Device device, String parent);

    boolean onUserLoggedIn();

    void initializedView();

//    void updateUserInformation(String accessToken, String name, String picture, DataManager.LoggedInMode loggedInMode);

    void updateAccessToken(String accessToken);

    void updateUserInfo(String name, String preferredName, String givenName, String familyName, String email);

    void fetchUserInfo(String username);

}
