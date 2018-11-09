package eu.waziup.waziup_da_app.di.component;

import dagger.Component;
import eu.waziup.waziup_da_app.di.PerActivity;
import eu.waziup.waziup_da_app.di.module.ActivityModule;
import eu.waziup.waziup_da_app.ui.detail.DetailSensorActivity;
import eu.waziup.waziup_da_app.ui.login.LoginActivity;
import eu.waziup.waziup_da_app.ui.register.RegisterSensorActivity;
import eu.waziup.waziup_da_app.ui.sensor.SensorActivity;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(LoginActivity activity);

    void inject(SensorActivity activity);

    void inject(RegisterSensorActivity activity);

    void inject(DetailSensorActivity activity);
}
