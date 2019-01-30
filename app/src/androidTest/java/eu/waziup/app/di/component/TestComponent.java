package eu.waziup.app.di.component;

import javax.inject.Singleton;

import dagger.Component;
import eu.waziup.app.di.module.ApplicationTestModule;

@Singleton
@Component(modules = ApplicationTestModule.class)
public interface TestComponent extends ApplicationComponent {
}
