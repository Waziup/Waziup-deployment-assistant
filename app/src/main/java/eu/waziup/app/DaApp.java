package eu.waziup.app;

import android.app.Application;
import android.content.Context;
import android.support.customtabs.CustomTabsIntent;
import android.support.multidex.MultiDex;
import android.util.Log;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.di.component.ApplicationComponent;
import eu.waziup.app.di.component.DaggerApplicationComponent;
import eu.waziup.app.di.module.ApplicationModule;
import eu.waziup.app.utils.AppLogger;

public class DaApp extends Application {

    public static final String LOG_TAG = "AppAuthSample";
    public static final String TAG = DaApp.class.getSimpleName();
    public static Context context;
    public static Application application;
    @Inject
    DataManager mDataManager;

    private ApplicationComponent mApplicationComponent;

    public static Context getContext() {
        return context;
    }

    public static Application getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        mApplicationComponent.inject(this);

        AppLogger.init();

        context = this;
        application = this;

    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void warmUpBrowser() {
        Log.e(TAG, "Warming up browser instance for auth request");
        Log.e(TAG, "mAuthRequest " + mAuthRequest.get().toUri());
        CustomTabsIntent.Builder intentBuilder = mAuthService.createCustomTabsIntentBuilder(mAuthRequest.get().toUri());
        intentBuilder.setToolbarColor(getColorCompat(R.color.chromeTab));
        mAuthIntent.set(intentBuilder.build());
    }
}
