package eu.waziup.waziup_da_app.di.component;

import dagger.Component;
import eu.waziup.waziup_da_app.di.PerActivity;
import eu.waziup.waziup_da_app.di.module.ActivityModule;
import eu.waziup.waziup_da_app.ui.detail.DetailSensorFragment;
import eu.waziup.waziup_da_app.ui.detail.MeasurementAdapter;
import eu.waziup.waziup_da_app.ui.login.LoginActivity;
import eu.waziup.waziup_da_app.ui.main.MainActivity;
import eu.waziup.waziup_da_app.ui.map.MapFragment;
import eu.waziup.waziup_da_app.ui.qrscan.QRScanFragment;
import eu.waziup.waziup_da_app.ui.register.RegisterSensorFragment;
import eu.waziup.waziup_da_app.ui.sensor.SensorAdapter;
import eu.waziup.waziup_da_app.ui.sensor.SensorFragment;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(LoginActivity activity);

    void inject(MainActivity activity);

    void inject(SensorAdapter adapter);

    void inject(MeasurementAdapter adapter);

    void inject(SensorFragment fragment);

    void inject(RegisterSensorFragment fragment);

    void inject(DetailSensorFragment fragment);

    void inject(MapFragment fragment);

    void inject(QRScanFragment fragment);

}
