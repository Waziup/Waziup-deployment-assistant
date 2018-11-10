package eu.waziup.waziup_da_app.ui.sensor;

import android.util.Log;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.ui.base.BasePresenter;
import eu.waziup.waziup_da_app.utils.CommonUtils;
import eu.waziup.waziup_da_app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class SensorPresenter<V extends SensorMvpView> extends BasePresenter<V>
        implements SensorMvpPresenter<V> {

    private static final String TAG = "RegisterSensorPresenter";

    @Inject
    public SensorPresenter(DataManager dataManager,
                           SchedulerProvider schedulerProvider,
                           CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void onLogOutClicked() {
        // removing the token when the user logout
        getDataManager().setUserAsLoggedOut();
        getMvpView().openLoginActivity();

        //todo find out if there is anything left in this part
//        sharedpreferences.edit().putString(Constants.token, "nothing").apply();
//        startActivity(new Intent(SensorActivity.this, LoginActivity.class));
//        finish();
    }

    @Override
    public void loadSensors() {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().fetchSensors()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(sensors -> {
                    if (!isViewAttached())
                        return;
                    Log.e("--->loadSensor","working");


                    getMvpView().showSensors(sensors);

                }, throwable -> {

                    Log.e("--->loadSensor","failed");

                    if (!isViewAttached())
                        return;

                    getMvpView().hideLoading();
                    getMvpView().onError(CommonUtils.getErrorMessage(throwable));

                }));
    }

    @Override
    public void onRegisterFabClicked() {
        getMvpView().hideLoading();
        getMvpView().openRegisterSensorActivity();
    }

    @Override
    public void onSensorItemClicked(Sensor sensor) {
        getMvpView().openDetailSensorActivity(sensor);
    }
}
