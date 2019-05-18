package eu.waziup.app.di.component;

import dagger.Component;
import eu.waziup.app.di.PerActivity;
import eu.waziup.app.di.module.ActivityModule;
import eu.waziup.app.ui.main.MainActivity;
import eu.waziup.app.ui.map.MapFragment;
import eu.waziup.app.ui.measurementdetail.MeasurementDetailDialog;
import eu.waziup.app.ui.measurementedit.EditMeasurementDialog;
import eu.waziup.app.ui.neterror.ErrorNetworkFragment;
import eu.waziup.app.ui.notification.NotificationAdapter;
import eu.waziup.app.ui.notification.NotificationFragment;
import eu.waziup.app.ui.notificationdetail.NotificationDetailFragment;
import eu.waziup.app.ui.register.RegisterSensorFragment;
import eu.waziup.app.ui.sensor.SensorAdapter;
import eu.waziup.app.ui.sensor.SensorFragment;
import eu.waziup.app.ui.sensordetail.DetailSensorFragment;
import eu.waziup.app.ui.sensordetail.MeasurementAdapter;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);

    void inject(SensorAdapter adapter);

    void inject(NotificationAdapter adapter);

    void inject(MeasurementAdapter adapter);

    void inject(SensorFragment fragment);

    void inject(RegisterSensorFragment fragment);

    void inject(DetailSensorFragment fragment);

    void inject(MapFragment fragment);

    void inject(NotificationFragment fragment);

    void inject(NotificationDetailFragment fragment);

    void inject(ErrorNetworkFragment fragment);

    void inject(EditMeasurementDialog dialog);

    void inject(MeasurementDetailDialog dialog);
}
