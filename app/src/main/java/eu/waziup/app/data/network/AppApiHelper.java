package eu.waziup.app.data.network;


import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import eu.waziup.app.data.network.model.LoginRequest;
import eu.waziup.app.data.network.model.notification.NotificationResponse;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.data.network.model.sensor.RegisterSensorResponse;
import eu.waziup.app.data.network.model.sensor.Device;
import eu.waziup.app.data.network.model.user.User;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by KidusMT.
 */

@Singleton
public class AppApiHelper implements ApiHelper {

    private ApiHeader mApiHeader;
    private ApiCall mApiCall;

    @Inject
    public AppApiHelper(ApiHeader apiHeader, ApiCall apiCall) {
        mApiHeader = apiHeader;
        mApiCall = apiCall;
    }

    @Override
    public ApiHeader getApiHeader() {
        return mApiHeader;
    }

    @Override
    public void setApiHeader(ApiHeader apiHeader) {
        if (apiHeader != null) {
            mApiHeader.setAccessToken(apiHeader.getAccessToken());
        }
    }

    @Override
    public Single<String> serverLogin(LoginRequest.ServerLoginRequest request) {
        return mApiCall.login(request);
    }

    @Override
    public Single<List<eu.waziup.app.data.network.model.devices.Device>> fetchSensors(String username) {
        return mApiCall.getSensors(String.format("owner==%s",username));
    }

    @Override
    public Single<List<eu.waziup.app.data.network.model.devices.Device>> fetchSensors(int limit, int offset) {
        return mApiCall.getSensors(limit, offset);
    }

    @Override
    public Single<ResponseBody> deleteMeasurement(String sensorId, String measurementId) {
        return mApiCall.deleteMeasurement(sensorId, measurementId);
    }

    @Override
    public Single<List<Sensor>> getMeasurements(String sensor_id) {
        return mApiCall.getMeasurement(sensor_id);
    }

    @Override
    public Single<RegisterSensorResponse> registerSensor(Device device) {
        return mApiCall.createSensor(device);
    }

    @Override
    public Single<List<User>> getUsers() {
        return mApiCall.getUsers();
    }

    @Override
    public Single<List<NotificationResponse>> getNotifications() {
        return mApiCall.getNotifications();
    }

}

