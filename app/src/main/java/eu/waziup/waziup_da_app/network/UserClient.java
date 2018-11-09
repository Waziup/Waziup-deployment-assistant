package eu.waziup.waziup_da_app.network;

import eu.waziup.waziup_da_app.data.network.model.User;
import eu.waziup.waziup_da_app.network.EndPoints;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserClient {

    @POST(EndPoints.LOGIN)
    Call<String> login(@Body User user);
}
