package eu.waziup.app.di.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import eu.waziup.app.di.ActivityContext;
import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.main.MainMvpPresenter;
import eu.waziup.app.ui.main.MainMvpView;
import eu.waziup.app.ui.main.MainPresenter;
import eu.waziup.app.ui.map.MapMvpPresenter;
import eu.waziup.app.ui.map.MapMvpView;
import eu.waziup.app.ui.map.MapPresenter;
import eu.waziup.app.ui.measurementdetail.MeasurementDetailMvpPresenter;
import eu.waziup.app.ui.measurementdetail.MeasurementDetailMvpView;
import eu.waziup.app.ui.measurementdetail.MeasurementDetailPresenter;
import eu.waziup.app.ui.measurementedit.EditMeasurementMvpPresenter;
import eu.waziup.app.ui.measurementedit.EditMeasurementMvpView;
import eu.waziup.app.ui.measurementedit.EditMeasurementPresenter;
import eu.waziup.app.ui.neterror.ErrorNetworkMvpPresenter;
import eu.waziup.app.ui.neterror.ErrorNetworkMvpView;
import eu.waziup.app.ui.neterror.ErrorNetworkPresenter;
import eu.waziup.app.ui.notification.NotificationAdapter;
import eu.waziup.app.ui.notification.NotificationMvpPresenter;
import eu.waziup.app.ui.notification.NotificationMvpView;
import eu.waziup.app.ui.notification.NotificationPresenter;
import eu.waziup.app.ui.notificationdetail.NotificationDetailMvpPresenter;
import eu.waziup.app.ui.notificationdetail.NotificationDetailMvpView;
import eu.waziup.app.ui.notificationdetail.NotificationDetailPresenter;
import eu.waziup.app.ui.register.RegisterSensorMvpPresenter;
import eu.waziup.app.ui.register.RegisterSensorMvpView;
import eu.waziup.app.ui.register.RegisterSensorPresenter;
import eu.waziup.app.ui.sensor.SensorAdapter;
import eu.waziup.app.ui.sensor.SensorMvpPresenter;
import eu.waziup.app.ui.sensor.SensorMvpView;
import eu.waziup.app.ui.sensor.SensorPresenter;
import eu.waziup.app.ui.sensordetail.DetailSensorMvpPresenter;
import eu.waziup.app.ui.sensordetail.DetailSensorMvpView;
import eu.waziup.app.ui.sensordetail.DetailSensorPresenter;
import eu.waziup.app.ui.sensordetail.MeasurementAdapter;
import eu.waziup.app.utils.rx.AppSchedulerProvider;
import eu.waziup.app.utils.rx.SchedulerProvider;
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
    NotificationDetailMvpPresenter<NotificationDetailMvpView> provideNotificationDetailPresenter(
            NotificationDetailPresenter<NotificationDetailMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    MapMvpPresenter<MapMvpView> provideMapPresenter(
            MapPresenter<MapMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    NotificationMvpPresenter<NotificationMvpView> provideNotificationPresenter(
            NotificationPresenter<NotificationMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    ErrorNetworkMvpPresenter<ErrorNetworkMvpView> provideErrorNetworkPresenter(
            ErrorNetworkPresenter<ErrorNetworkMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    EditMeasurementMvpPresenter<EditMeasurementMvpView> provideEditMeasurementPresenter(
            EditMeasurementPresenter<EditMeasurementMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    MeasurementDetailMvpPresenter<MeasurementDetailMvpView> provideMeasurementDetailPresenter(
            MeasurementDetailPresenter<MeasurementDetailMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    SensorAdapter provideSensorAdapter() {
        return new SensorAdapter(new ArrayList<>());
    }

    @Provides
    @PerActivity
    NotificationAdapter provideNotificationAdapter() {
        return new NotificationAdapter(new ArrayList<>());
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
