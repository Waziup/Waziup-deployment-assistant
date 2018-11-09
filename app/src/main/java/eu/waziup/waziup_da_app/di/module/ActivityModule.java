package eu.waziup.waziup_da_app.di.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import dagger.Module;
import dagger.Provides;
import eu.waziup.waziup_da_app.di.ActivityContext;
import eu.waziup.waziup_da_app.di.PerActivity;
import eu.waziup.waziup_da_app.ui.login.LoginMvpPresenter;
import eu.waziup.waziup_da_app.ui.login.LoginMvpView;
import eu.waziup.waziup_da_app.ui.login.LoginPresenter;
import eu.waziup.waziup_da_app.ui.sensor.SensorMvpPresenter;
import eu.waziup.waziup_da_app.ui.sensor.SensorMvpView;
import eu.waziup.waziup_da_app.ui.sensor.SensorPresenter;
import eu.waziup.waziup_da_app.utils.rx.AppSchedulerProvider;
import eu.waziup.waziup_da_app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class ActivityModule {

    private AppCompatActivity mActivity;

    public ActivityModule(AppCompatActivity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    AppCompatActivity provideActivity() {
        return mActivity;
    }

    @Provides
    CompositeDisposable provideCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return new AppSchedulerProvider();
    }

    @Provides
    @PerActivity
    LoginMvpPresenter<LoginMvpView> provideLoginPresenter(
            LoginPresenter<LoginMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    SensorMvpPresenter<SensorMvpView> provideSensorPresenter(
            SensorPresenter<SensorMvpView> presenter) {
        return presenter;
    }
}
