package eu.waziup.waziup_da_app.data.network;


import java.util.List;

import eu.waziup.waziup_da_app.data.network.model.LoginRequest;
import eu.waziup.waziup_da_app.data.network.model.sensor.Measurement;
import eu.waziup.waziup_da_app.data.network.model.sensor.RegisterSensorResponse;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.data.network.model.user.User;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * Created by KidusMT.
 */

public interface ApiHelper {

    ApiHeader getApiHeader();

    void setApiHeader(ApiHeader apiHeader);

    Observable<String> serverLogin(LoginRequest.ServerLoginRequest loginRequest);

    Observable<List<Sensor>> fetchSensors();

    Observable<ResponseBody> deleteMeasurement(String sensor_id, String measurement_id);

    Observable<List<Measurement>> getMeasurements(String sensor_id);

    Observable<RegisterSensorResponse> registerSensor(Sensor sensor);

    Observable<List<User>> getUsers();
}
