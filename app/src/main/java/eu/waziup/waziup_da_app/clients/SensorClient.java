package eu.waziup.waziup_da_app.clients;

import java.util.List;

import eu.waziup.waziup_da_app.models.sensor.Sensor;
import eu.waziup.waziup_da_app.network.EndPoints;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SensorClient {

    @GET(EndPoints.SENSOR)
    Call<List<Sensor>> getSensors();
}
