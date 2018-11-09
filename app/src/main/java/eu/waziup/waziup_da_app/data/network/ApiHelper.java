package eu.waziup.waziup_da_app.data.network;


import java.util.List;

import eu.waziup.waziup_da_app.data.network.model.LoginRequest;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import io.reactivex.Observable;

/**
 * Created by KidusMT.
 */

public interface ApiHelper {

    ApiHeader getApiHeader();

    void setApiHeader(ApiHeader apiHeader);

    Observable<String> serverLogin(LoginRequest.ServerLoginRequest loginRequest);

    Observable<List<Sensor>> fetchSensors();
}
