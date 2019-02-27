package eu.waziup.app.di.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import eu.waziup.app.data.AppDataManager;
import eu.waziup.app.data.DataManager;
import eu.waziup.app.data.network.ApiHelper;
import eu.waziup.app.data.network.AppApiHelper;
import eu.waziup.app.data.prefs.AppPreferencesHelper;
import eu.waziup.app.data.prefs.PreferencesHelper;
import eu.waziup.app.di.ApiInfo;
import eu.waziup.app.di.ApplicationContext;
import eu.waziup.app.di.DatabaseInfo;
import eu.waziup.app.di.PreferenceInfo;
import eu.waziup.app.test.BuildConfig;
import eu.waziup.app.utils.AppConstants;

@Module
public class ApplicationTestModule {

    private final Application mApplication;

    public ApplicationTestModule(Application application) {
        mApplication = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @DatabaseInfo
    String provideDatabaseName() {
        return AppConstants.DB_NAME;
    }

    @Provides
    @ApiInfo
    String provideApiKey() {
        return BuildConfig.API_KEY;
    }

    @Provides
    @PreferenceInfo
    String providePreferenceName() {
        return AppConstants.PREF_NAME;
    }

    // TODO : Mock all below for UI testing

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Provides
    @Singleton
    ApiHelper provideApiHelper(AppApiHelper appApiHelper) {
        return appApiHelper;
    }


}
