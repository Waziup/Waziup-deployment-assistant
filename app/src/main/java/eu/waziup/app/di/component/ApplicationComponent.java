package eu.waziup.app.di.component;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import eu.waziup.app.DaApp;
import eu.waziup.app.data.DataManager;
import eu.waziup.app.di.ApplicationContext;
import eu.waziup.app.di.module.ApplicationModule;
import eu.waziup.app.service.SyncService;


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