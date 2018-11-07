package eu.waziup.waziup_da_app.utils;

import android.app.Activity;
import android.view.WindowManager;
import android.widget.Toast;

import eu.waziup.waziup_da_app.DaApp;

public class CommonUtils {

    public static void hideKeyBoard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void toast(String msg) {
        Toast.makeText(DaApp.getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
