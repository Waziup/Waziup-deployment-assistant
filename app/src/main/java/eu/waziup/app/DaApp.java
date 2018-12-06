package eu.waziup.app;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.di.component.ApplicationComponent;
import eu.waziup.app.di.component.DaggerApplicationComponent;
import eu.waziup.app.di.module.ApplicationModule;
import eu.waziup.app.utils.AppLogger;

public class DaApp extends Application {

    public static Context context;

    @Inject
    DataManager mDataManager;

    private ApplicationComponent mApplicationComponent;

//    private BoxStore boxStore;
//    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        mApplicationComponent.inject(this);

        AppLogger.init();

        context = this;


//        boxStore = MyObjectBox.builder().androidContext(DaApp.this).build();
//        daoSession = new DaoSession(boxStore);


        // todo has to identify if the app run is for the first time
        // todo if fo the first time


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
