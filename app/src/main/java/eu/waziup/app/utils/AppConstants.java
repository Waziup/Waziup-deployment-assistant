package eu.waziup.app.utils;

public final class AppConstants {

    public static final String APP_ID = "eu.waziup.app";

    public static final String STATUS_CODE_SUCCESS = "success";
    public static final String STATUS_CODE_FAILED = "failed";

    public static final String DETAIL_SENSOR_KEY = "sensor_key";

    public static final String BASE_URL_ONTOLOGY = "http://35.157.161.231:8081/api/v1/";

    public static final int API_STATUS_CODE_LOCAL_ERROR = 0;

    public static final String DB_NAME = "mindorks_mvp.db";
    public static final String PREF_NAME = "mindorks_pref";

    public static final String PREF_KEY_FCM_ACCESS_TOKEN_NAME = "PREF_KEY_FCM_ACCESS_TOKEN_NAME";

    public static final long NULL_INDEX = -1L;

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    public static final String MEASUREMENT_SENSOR_KEY = "MEASUREMENT_SENSOR_KEY";

    public static final String EXTRA_AUTH_SERVICE_DISCOVERY = "authServiceDiscovery";
    public static final String EXTRA_CLIENT_SECRET = "clientSecret";

    public static final String KEY_AUTH_STATE = "authState";
    public static final String KEY_USER_INFO = "userInfo";
    public static final String MEASUREMENT_DETAIL_KEY = "MEASUREMENT_DETAIL_KEY";

    public static final String AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/auth";
    public static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";


    private AppConstants() {
        // This utility class is not publicly instantiable
    }
}
