package eu.waziup.app.ui.neterror;

import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.base.MvpPresenter;

/**
 * Created by KidusMT.
 */

@PerActivity
public interface ErrorNetworkMvpPresenter<V extends ErrorNetworkMvpView> extends MvpPresenter<V> {

    void onRefreshClicked(String parent);
}
