package eu.waziup.app.data.network;


import java.util.List;

import eu.waziup.app.data.network.model.LoginRequest;
import eu.waziup.app.data.network.model.notification.NotificationResponse;
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.data.network.model.sensor.RegisterSensorResponse;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.data.network.model.user.User;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by KidusMT.
 */

public interface ApiHelper {

    ApiHeader getApiHeader();

    void setApiHeader(ApiHeader apiHeader);

    Single<String> serverLogin(LoginRequest.ServerLoginRequest loginRequest);

    Single<List<Sensor>> fetchSensors();

    Single<List<Sensor>> fetchSensors(int limit, int offset);

    Single<ResponseBody> deleteMeasurement(String sensor_id, String measurement_id);

    Single<List<Measurement>> getMeasurements(String sensor_id);

    Single<RegisterSensorResponse> registerSensor(Sensor sensor);

    Single<List<User>> getUsers();

    Single<List<NotificationResponse>> getNotifications();

}
