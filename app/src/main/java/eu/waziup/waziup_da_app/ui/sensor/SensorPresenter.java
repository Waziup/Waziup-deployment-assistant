package eu.waziup.waziup_da_app.ui.sensor;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.data.network.model.ApiError;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.ui.base.BasePresenter;
import eu.waziup.waziup_da_app.utils.CommonUtils;
import eu.waziup.waziup_da_app.utils.ErrorUtils;
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
//        getMvpView().openLoginActivity();
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

                    getMvpView().showSensors(sensors);

                }, throwable -> {

                    if (!isViewAttached())
                        return;

                    getMvpView().hideLoading();
                    ApiError apiError = ErrorUtils.parseError(((HttpException)throwable).response());
                    getMvpView().onError(apiError.getMessage());
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
