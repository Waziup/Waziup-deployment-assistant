package eu.waziup.waziup_da_app;

import android.app.Application;
import android.content.Context;

public class DaApp extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext(){
        return context;
    }
}
