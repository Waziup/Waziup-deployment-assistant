package eu.waziup.app.data.network;



import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import eu.waziup.app.data.network.model.LoginRequest;
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.data.network.model.sensor.RegisterSensorResponse;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.data.network.model.user.User;
import io.reactivex.Observable;
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
//            mApiHeader.setApiKey(apiHeader.getApiKey());
//            mApiHeader.setUserId(apiHeader.getUserId());
            mApiHeader.setAccessToken(apiHeader.getAccessToken());
        }
    }

    @Override
    public Observable<String> serverLogin(LoginRequest.ServerLoginRequest request) {
        return mApiCall.login(request);
    }

    @Override
    public Observable<List<Sensor>> fetchSensors() {
        return mApiCall.getSensors();
    }

    @Override
    public Observable<ResponseBody> deleteMeasurement(String sensorId, String measurementId) {
        return mApiCall.deleteMeasurement(sensorId, measurementId);
    }

    @Override
    public Observable<List<Measurement>> getMeasurements(String sensor_id) {
        return mApiCall.getMeasurement(sensor_id);
    }

    @Override
    public Observable<RegisterSensorResponse> registerSensor(Sensor sensor) {
        return mApiCall.createSensor(sensor);
    }

    @Override
    public Observable<List<User>> getUsers() {
        return mApiCall.getUsers();
    }

}
