package eu.waziup.waziup_da_app.ui.login;

import eu.waziup.waziup_da_app.ui.base.MvpView;

/**
 * Created by KidusMT.
 */

public interface LoginMvpView extends MvpView {

    void openMainActivity();
    void openForgetPasswordActivity();
    boolean getDeviceToken();
}
