package eu.waziup.waziup_da_app.ui.main;

import eu.waziup.waziup_da_app.di.PerActivity;
import eu.waziup.waziup_da_app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void onLogOutClicked();

    void onNavMenuCreated();
}
