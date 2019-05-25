package eu.waziup.app.data.network;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by KidusMT.
 */

@Singleton
public class ApiInterceptor implements Interceptor {

    private static final String TAG = "ApiInterceptor";

    private ApiHeader mApiHeader;

    @Inject
    public ApiInterceptor(final ApiHeader header) {
        mApiHeader = header;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        final Request request = chain.request();
        final Request.Builder builder = request.newBuilder();
        String apiAuthType = request.header(ApiHeader.API_AUTH_TYPE);
        if (apiAuthType == null) {
            apiAuthType = ApiHeader.PROTECTED_API;
        }

        switch (apiAuthType) {
            case ApiHeader.PROTECTED_API:
                builder.addHeader(ApiHeader.HEADER_PARAM_ACCESS_TOKEN, mApiHeader.getAccessToken());
                break;
            case ApiHeader.PUBLIC_API:
                break;
        }

        return chain.proceed(builder.build());
    }
}
