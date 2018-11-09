package eu.waziup.waziup_da_app.data.network;

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
    public Response intercept(Chain chain) throws IOException {
        final Request request = chain.request();
        final Request.Builder builder = request.newBuilder();
        String apiAuthType = request.header(ApiHeader.API_AUTH_TYPE);
        if (apiAuthType == null) {
            apiAuthType = ApiHeader.PROTECTED_API;
        }

        switch (apiAuthType) {
            case ApiHeader.PROTECTED_API:
                builder.addHeader(ApiHeader.HEADER_PARAM_ACCESS_TOKEN, mApiHeader.getAccessToken());
//                builder.addHeader(ApiHeader.HEADER_PARAM_USER_ID, String.valueOf(mApiHeader.getUserId()));
            case ApiHeader.PUBLIC_API:
//            default:
//                builder.addHeader(ApiHeader.HEADER_PARAM_API_KEY, mApiHeader.getApiKey());
        }

        return chain.proceed(builder.build());
    }
}
