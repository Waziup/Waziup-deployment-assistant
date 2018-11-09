package eu.waziup.waziup_da_app.ui.login;

import eu.waziup.waziup_da_app.di.PerActivity;
import eu.waziup.waziup_da_app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface LoginMvpPresenter<V extends LoginMvpView> extends MvpPresenter<V> {

    void onServerLoginClick(String email, String password, String imei);

    void onForgetPasswordClicked();

}
