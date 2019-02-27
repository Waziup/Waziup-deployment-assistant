package eu.waziup.app.di.component;

import dagger.Component;
import eu.waziup.app.di.PerService;
import eu.waziup.app.di.module.ServiceModule;
import eu.waziup.app.service.SyncService;

@PerService
@Component(dependencies = ApplicationComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {

    void inject(SyncService service);

}
