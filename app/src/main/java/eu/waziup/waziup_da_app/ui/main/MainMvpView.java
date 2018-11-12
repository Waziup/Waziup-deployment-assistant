package eu.waziup.waziup_da_app.ui.main;

import eu.waziup.waziup_da_app.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void openLoginActivity();

    void openRegisterationSensor();

    void lockDrawer();

    void unlockDrawer();

    // for the navigation drawer
    void updateUserName(String currentUserName);

    void updateUserEmail(String currentUserEmail);

    void updateUserProfilePic(String currentUserProfilePicUrl);
}
