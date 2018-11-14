package eu.waziup.waziup_da_app.ui.main;

import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void openLoginActivity();

    void openRegisterationSensor();

    void openSensorDetailFragment(Sensor sensor);

    void lockDrawer();

    void unlockDrawer();

    // for the navigation drawer
    void updateUserName(String currentUserName);

    void updateUserEmail(String currentUserEmail);

    void updateUserProfilePic(String currentUserProfilePicUrl);
}
