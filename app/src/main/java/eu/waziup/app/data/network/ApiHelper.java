package eu.waziup.app.data.network;


import java.util.List;

import eu.waziup.app.data.network.model.LoginRequest;
import eu.waziup.app.data.network.model.notification.NotificationResponse;
import eu.waziup.app.data.network.model.sensor.Device;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.data.network.model.sensor.RegisterSensorResponse;
import eu.waziup.app.data.network.model.user.User;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by KidusMT.
 */

public interface ApiHelper {

    ApiHeader getApiHeader();

    void setApiHeader(ApiHeader apiHeader);

    Single<String> serverLogin(LoginRequest.ServerLoginRequest loginRequest);

    Single<List<eu.waziup.app.data.network.model.devices.Device>> fetchSensors(String username);

    Single<List<eu.waziup.app.data.network.model.devices.Device>> fetchSensors(int limit, int offset);

    Single<ResponseBody> deleteMeasurement(String sensor_id, String measurement_id);

    Single<List<Sensor>> getMeasurements(String sensor_id);

    Single<RegisterSensorResponse> registerSensor(Device device);

    Single<List<User>> getUsers();

    Single<List<NotificationResponse>> getNotifications();

}
