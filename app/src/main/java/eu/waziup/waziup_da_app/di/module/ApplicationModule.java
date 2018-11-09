package eu.waziup.waziup_da_app.di.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import eu.waziup.waziup_da_app.BuildConfig;
import eu.waziup.waziup_da_app.data.AppDataManager;
import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.data.network.ApiCall;
import eu.waziup.waziup_da_app.data.network.ApiHeader;
import eu.waziup.waziup_da_app.data.network.ApiHelper;
import eu.waziup.waziup_da_app.data.network.ApiInterceptor;
import eu.waziup.waziup_da_app.data.network.AppApiHelper;
import eu.waziup.waziup_da_app.data.prefs.AppPreferencesHelper;
import eu.waziup.waziup_da_app.data.prefs.PreferencesHelper;
import eu.waziup.waziup_da_app.di.ApiInfo;
import eu.waziup.waziup_da_app.di.ApplicationContext;
import eu.waziup.waziup_da_app.di.DatabaseInfo;
import eu.waziup.waziup_da_app.di.PreferenceInfo;
import eu.waziup.waziup_da_app.utils.AppConstants;

@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application application) {
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

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Provides
    @Singleton
    ApiHelper provideApiHelper(AppApiHelper appApiHelper) {
        return appApiHelper;
    }

    @Provides
    @Singleton
    ApiCall provideApiCall(ApiInterceptor apiInterceptor) {
        return ApiCall.Factory.create(apiInterceptor);
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Provides
    @Singleton
    ApiHeader provideApiHeader(@ApiInfo String apiKey, PreferencesHelper preferencesHelper) {
        return new ApiHeader(apiKey, preferencesHelper.getCurrentUserId(), preferencesHelper.getAccessToken());
    }
}
