package eu.waziup.app.ui.main;

import eu.waziup.app.data.network.model.devices.Device;
import eu.waziup.app.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void openLoginActivity();

    void openRegistrationSensor();

    void openSensorDetailFragment(Device device, String parentFragment);

    void lockDrawer();

    void unlockDrawer();

    // for the navigation drawer
    void updateUserName(String currentUserName);

    void updateUserEmail(String currentUserEmail);

    void updateUserProfilePic(String currentUserProfilePicUrl);
}
