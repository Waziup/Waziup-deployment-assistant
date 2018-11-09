package eu.waziup.waziup_da_app.di.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import eu.waziup.waziup_da_app.DaApp;
import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.di.ApplicationContext;
import eu.waziup.waziup_da_app.di.module.ApplicationModule;
import eu.waziup.waziup_da_app.service.SyncService;


@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(DaApp app);

    void inject(SyncService service);

    @ApplicationContext
    Context context();

    Application application();

    DataManager getDataManager();
}