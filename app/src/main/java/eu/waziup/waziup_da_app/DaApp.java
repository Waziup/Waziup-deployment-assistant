package eu.waziup.waziup_da_app;

import android.app.Application;
import android.content.Context;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.di.component.ApplicationComponent;
import eu.waziup.waziup_da_app.di.component.DaggerApplicationComponent;
import eu.waziup.waziup_da_app.di.module.ApplicationModule;
import eu.waziup.waziup_da_app.utils.AppLogger;
import okhttp3.logging.HttpLoggingInterceptor;

public class DaApp extends Application {

    public static Context context;

    @Inject
    DataManager mDataManager;

    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

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

    public static Context getContext(){
        return context;
    }
}
