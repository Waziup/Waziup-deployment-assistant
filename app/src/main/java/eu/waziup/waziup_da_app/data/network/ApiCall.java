package eu.waziup.waziup_da_app.data.network;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import eu.waziup.waziup_da_app.BuildConfig;
import eu.waziup.waziup_da_app.DaApp;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by KidusMT.
 */

public interface ApiCall {

    String HEADER_PARAM_SEPARATOR = ":";

//    @POST(ENDPOINT_SERVER_LOGIN)
//    @Headers(ApiHeader.API_AUTH_TYPE + HEADER_PARAM_SEPARATOR + ApiHeader.PUBLIC_API)
//    Observable<LoginResponse> doServerLogin(@Body LoginRequest.ServerLoginRequest request);
//
//    @POST(ENDPOINT_SERVER_FORGET_PASSWORD)
//    @Headers(ApiHeader.API_AUTH_TYPE + HEADER_PARAM_SEPARATOR + ApiHeader.PUBLIC_API)
//    Observable<ForgetPasswordResponse> doForgetPasswordApiCall(@Body ForgetPasswordRequest request);
//
//    @POST(ENDPOINT_SERVER_CHANGE_PASSWORD)
//    @Headers(ApiHeader.API_AUTH_TYPE + HEADER_PARAM_SEPARATOR + ApiHeader.PROTECTED_API)
//    Observable<ChangePasswordResponse> doChangePasswordApiCall(@Body ChangePasswordRequest request);

    class Factory {

        private static final int NETWORK_CALL_TIMEOUT = 60;

        public static ApiCall create(ApiInterceptor apiInterceptor) {

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(apiInterceptor);
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
            builder.addInterceptor(logging);
            builder.readTimeout(NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS);
            builder.writeTimeout(NETWORK_CALL_TIMEOUT, TimeUnit.SECONDS);

            OkHttpClient httpClient = builder.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            return retrofit.create(ApiCall.class);


        }
    }
}
