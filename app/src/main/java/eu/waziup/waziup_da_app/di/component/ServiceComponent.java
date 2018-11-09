package eu.waziup.waziup_da_app.di.component;

import dagger.Component;
import eu.waziup.waziup_da_app.di.PerService;
import eu.waziup.waziup_da_app.di.module.ServiceModule;
import eu.waziup.waziup_da_app.service.SyncService;

@PerService
@Component(dependencies = ApplicationComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {

    void inject(SyncService service);

}
