package eu.waziup.app.ui.base;


/**
 * Every presenter in the app must either implement this interface or extend BasePresenter
 * indicating the MvpView type that wants to be attached with.
 */
public interface MvpPresenter<V extends MvpView> {

    void onAttach(V mvpView);

    void onDetach();

//    void handleApiError(ANError error);

    void setUserAsLoggedOut();
}
