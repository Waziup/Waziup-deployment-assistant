package eu.waziup.waziup_da_app.clients;

import eu.waziup.waziup_da_app.models.User;
import eu.waziup.waziup_da_app.network.EndPoints;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserClient {

    @POST(EndPoints.LOGIN)
    Call<String> login(@Body User user);
}
