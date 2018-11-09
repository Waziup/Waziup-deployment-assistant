package eu.waziup.waziup_da_app.data.network;

import javax.inject.Singleton;

@Singleton
public class ApiHeader {

    public static final String API_AUTH_TYPE = "API_AUTH_TYPE";
    public static final String PUBLIC_API = "PUBLIC_API";
    public static final String PROTECTED_API = "PROTECTED_API";

    public static final String HEADER_PARAM_API_KEY = "api_key";
    public static final String HEADER_PARAM_ACCESS_TOKEN = "Authorization";
    public static final String HEADER_PARAM_USER_ID = "user_id";

    private String mApiKey;
    private Long mUserId;
    private String mAccessToken;

    public ApiHeader(String mApiKey, Long mUserId, String mAccessToken) {
        this.mApiKey = mApiKey;
        this.mUserId = mUserId;
        this.mAccessToken = mAccessToken;
    }

    public String getApiKey() {
        return mApiKey;
    }

    public void setApiKey(String apiKey) {
        mApiKey = apiKey;
    }

    public Long getUserId() {
        return mUserId;
    }

    public void setUserId(Long userId) {
        mUserId = userId;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

}
