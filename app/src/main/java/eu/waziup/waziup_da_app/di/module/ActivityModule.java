package eu.waziup.waziup_da_app.di.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import eu.waziup.waziup_da_app.di.ActivityContext;
import eu.waziup.waziup_da_app.di.PerActivity;
import eu.waziup.waziup_da_app.ui.detail.DetailSensorMvpPresenter;
import eu.waziup.waziup_da_app.ui.detail.DetailSensorMvpView;
import eu.waziup.waziup_da_app.ui.detail.DetailSensorPresenter;
import eu.waziup.waziup_da_app.ui.detail.MeasurementAdapter;
import eu.waziup.waziup_da_app.ui.login.LoginMvpPresenter;
import eu.waziup.waziup_da_app.ui.login.LoginMvpView;
import eu.waziup.waziup_da_app.ui.login.LoginPresenter;
import eu.waziup.waziup_da_app.ui.main.MainMvpPresenter;
import eu.waziup.waziup_da_app.ui.main.MainMvpView;
import eu.waziup.waziup_da_app.ui.main.MainPresenter;
import eu.waziup.waziup_da_app.ui.map.MapMvpPresenter;
import eu.waziup.waziup_da_app.ui.map.MapMvpView;
import eu.waziup.waziup_da_app.ui.register.RegisterSensorMvpPresenter;
import eu.waziup.waziup_da_app.ui.register.RegisterSensorMvpView;
import eu.waziup.waziup_da_app.ui.register.RegisterSensorPresenter;
import eu.waziup.waziup_da_app.ui.sensor.SensorAdapter;
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

    @Provides
    @PerActivity
    MainMvpPresenter<MainMvpView> provideMainPresenter(
            MainPresenter<MainMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    RegisterSensorMvpPresenter<RegisterSensorMvpView> provideRegisterSensorPresenter(
            RegisterSensorPresenter<RegisterSensorMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    DetailSensorMvpPresenter<DetailSensorMvpView> provideDetailSensorPresenter(
            DetailSensorPresenter<DetailSensorMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    MapMvpPresenter<MapMvpView> provideMapPresenter(
            MapMvpPresenter<MapMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    SensorAdapter provideSensorAdapter() {
        return new SensorAdapter(new ArrayList<>());
    }

    @Provides
    @PerActivity
    MeasurementAdapter provideMeasurementAdapter() {
        return new MeasurementAdapter(new ArrayList<>());
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(AppCompatActivity activity) {
        return new LinearLayoutManager(activity);
    }
}
