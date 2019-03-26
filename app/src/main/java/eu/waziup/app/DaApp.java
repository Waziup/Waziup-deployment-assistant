package eu.waziup.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.di.component.ApplicationComponent;
import eu.waziup.app.di.component.DaggerApplicationComponent;
import eu.waziup.app.di.module.ApplicationModule;
import eu.waziup.app.utils.AppLogger;
import io.fabric.sdk.android.Fabric;

public class DaApp extends Application {

    public static Context context;

    public static final String LOG_TAG = "AppAuthSample";

    @Inject
    DataManager mDataManager;

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        mApplicationComponent.inject(this);

        AppLogger.init();

        context = this;

    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
