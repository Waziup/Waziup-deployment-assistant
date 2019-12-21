package eu.waziup.app.ui.devicesdetail;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.CommonUtils;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class DetailDevicesPresenter<V extends DetailSensorMvpView> extends BasePresenter<V>
        implements DetailDevicesMvpPresenter<V> {

    private static final String TAG = "QRScanPresenter";

    @Inject
    public DetailDevicesPresenter(DataManager dataManager,
                                  SchedulerProvider schedulerProvider,
                                  CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    /**
     * method for editing a specific sensor when the edit button is clicked on the list
     * todo implementing the feature
     * @param sensor the sensor object to be edited and send to API for update
     */
    @Override
    public void onEditSensorClicked(Sensor sensor) {

    }

    /**
     * method for deleting the sensor from remote with an API call
     * @param deviceId with deviceId for identifying the sensor
     * @param sensorId with sensorId for identifying th sensor
     */
    @Override
    public void onDeleteSensorClicked(String deviceId, String sensorId) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().deleteMeasurement(deviceId, sensorId)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui()).subscribe(responseBody -> {
                            if (!isViewAttached())
                                return;

                            loadSensors(deviceId);
                        }, throwable -> {
                            if (!isViewAttached())
                                return;

                            getMvpView().hideLoading();
                            getMvpView().onError(CommonUtils.getErrorMessage(throwable));
                        }
                ));
    }

    /**
     * method for deploying the device with the button click on the screen
     * todo implement the feature
     */
    @Override
    public void onDeployDevicesClicked() {

    }

    /**
     * method for unDeploying the device with a button click
     * todo implement the feature
     */
    @Override
    public void onUnDeployDevicesClicked() {

    }

    /**
     * method handling the add sensor click for opening a dialog where a sensor can be added
     */
    @Override
    public void onAddSensorsClicked() {
        getMvpView().showCreateSensorsDialog();
    }

    /**
     * method for fetching the sensors from the API to be displayed
     * @param deviceId for filtering sensors with the deviceID
     */
    @Override
    public void loadSensors(String deviceId) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().getMeasurements(deviceId)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(measurements -> {
                    if (!isViewAttached())
                        return;

                    getMvpView().showSensors(deviceId, measurements);
                }, throwable -> {
                    if (!isViewAttached())
                        return;

                    getMvpView().hideLoading();
                    getMvpView().onError(CommonUtils.getErrorMessage(throwable));
                }));
    }
}
