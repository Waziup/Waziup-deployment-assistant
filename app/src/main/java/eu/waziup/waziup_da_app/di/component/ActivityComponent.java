package eu.waziup.waziup_da_app.di.component;

import dagger.Component;
import eu.waziup.waziup_da_app.di.PerActivity;
import eu.waziup.waziup_da_app.di.module.ActivityModule;
import eu.waziup.waziup_da_app.ui.login.LoginActivity;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

//    void inject(MainActivity activity);

    void inject(LoginActivity activity);

//    void inject(SplashActivity activity);

//    void inject(FeedActivity activity);

//    void inject(AboutFragment fragment);

//    void inject(OpenSourceFragment fragment);
//
//    void inject(BlogFragment fragment);
//
//    void inject(RateUsDialog dialog);
//
}
