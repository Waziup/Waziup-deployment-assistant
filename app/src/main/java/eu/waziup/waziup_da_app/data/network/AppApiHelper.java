package eu.waziup.waziup_da_app.data.network;


import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by KidusMT.
 */

@Singleton
public class AppApiHelper implements ApiHelper {

    private ApiHeader mApiHeader;
    private ApiCall mApiCall;

    @Inject
    public AppApiHelper(ApiHeader apiHeader, ApiCall apiCall) {
        mApiHeader = apiHeader;
        mApiCall = apiCall;
    }

    @Override
    public ApiHeader getApiHeader() {
        return mApiHeader;
    }

    @Override
    public void setApiHeader(ApiHeader apiHeader) {
        if (apiHeader != null) {
            mApiHeader.setApiKey(apiHeader.getApiKey());
            mApiHeader.setUserId(apiHeader.getUserId());
            mApiHeader.setAccessToken(apiHeader.getAccessToken());
        }
    }

}

