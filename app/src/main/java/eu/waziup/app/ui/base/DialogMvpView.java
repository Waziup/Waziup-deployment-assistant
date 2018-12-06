package eu.waziup.app.ui.base;


public interface DialogMvpView extends MvpView {

    void dismissDialog(String tag, String parentFragment);
}
