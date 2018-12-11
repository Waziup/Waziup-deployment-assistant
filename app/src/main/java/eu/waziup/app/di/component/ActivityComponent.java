package eu.waziup.app.di.component;

import dagger.Component;
import eu.waziup.app.di.PerActivity;
import eu.waziup.app.di.module.ActivityModule;
import eu.waziup.app.ui.detail.DetailSensorFragment;
import eu.waziup.app.ui.detail.MeasurementAdapter;
import eu.waziup.app.ui.login.LoginActivity;
import eu.waziup.app.ui.main.MainActivity;
import eu.waziup.app.ui.map.MapFragment;
import eu.waziup.app.ui.notification.NotificationAdapter;
import eu.waziup.app.ui.notification.NotificationFragment;
import eu.waziup.app.ui.register.RegisterSensorFragment;
import eu.waziup.app.ui.sensor.SensorAdapter;
import eu.waziup.app.ui.sensor.SensorFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(LoginActivity activity);

    void inject(MainActivity activity);

    void inject(SensorAdapter adapter);

    void inject(NotificationAdapter adapter);

    void inject(MeasurementAdapter adapter);

    void inject(SensorFragment fragment);

    void inject(RegisterSensorFragment fragment);

    void inject(DetailSensorFragment fragment);

    void inject(MapFragment fragment);

    void inject(NotificationFragment fragment);

}
