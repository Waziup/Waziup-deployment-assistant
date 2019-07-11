package eu.waziup.app.di.module;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import net.openid.appauth.AuthorizationService;

import java.util.ArrayList;

import dagger.Module;
import dagger.Provides;
import eu.waziup.app.di.ActivityContext;
import eu.waziup.app.di.PerActivity;
import eu.waziup.app.ui.device.DevicesAdapter;
import eu.waziup.app.ui.device.DevicesMvpPresenter;
import eu.waziup.app.ui.device.DevicesPresenter;
import eu.waziup.app.ui.devicesdetail.DetailDevicesPresenter;
import eu.waziup.app.ui.login.LoginMvpPresenter;
import eu.waziup.app.ui.login.LoginMvpView;
import eu.waziup.app.ui.login.LoginPresenter;
import eu.waziup.app.ui.main.MainMvpPresenter;
import eu.waziup.app.ui.main.MainMvpView;
import eu.waziup.app.ui.main.MainPresenter;
import eu.waziup.app.ui.map.MapMvpPresenter;
import eu.waziup.app.ui.map.MapMvpView;
import eu.waziup.app.ui.map.MapPresenter;
import eu.waziup.app.ui.sensordetail.SensorDetailMvpPresenter;
import eu.waziup.app.ui.sensordetail.SensorDetailMvpView;
import eu.waziup.app.ui.sensordetail.SensorDetailPresenter;
import eu.waziup.app.ui.sensoredit.EditSensorMvpPresenter;
import eu.waziup.app.ui.sensoredit.EditSensorMvpView;
import eu.waziup.app.ui.sensoredit.EditSensorPresenter;
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
import eu.waziup.app.ui.device.DevicesMvpView;
import eu.waziup.app.ui.devicesdetail.DetailDevicesMvpPresenter;
import eu.waziup.app.ui.devicesdetail.DetailSensorMvpView;
import eu.waziup.app.ui.devicesdetail.MeasurementAdapter;
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
    LoginMvpPresenter<LoginMvpView> provideLoginPresenter(
            LoginPresenter<LoginMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    DevicesMvpPresenter<DevicesMvpView> provideSensorPresenter(
            DevicesPresenter<DevicesMvpView> presenter) {
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
    DetailDevicesMvpPresenter<DetailSensorMvpView> provideDetailSensorPresenter(
            DetailDevicesPresenter<DetailSensorMvpView> presenter) {
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
    EditSensorMvpPresenter<EditSensorMvpView> provideEditMeasurementPresenter(
            EditSensorPresenter<EditSensorMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    SensorDetailMvpPresenter<SensorDetailMvpView> provideMeasurementDetailPresenter(
            SensorDetailPresenter<SensorDetailMvpView> presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    DevicesAdapter provideSensorAdapter() {
        return new DevicesAdapter(new ArrayList<>());
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
    @PerActivity
    AuthorizationService provideAuthorizationService(Context context) {
        return new AuthorizationService(context);
    }

    @Provides
    LinearLayoutManager provideLinearLayoutManager(AppCompatActivity activity) {
        return new LinearLayoutManager(activity);
    }
}
