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

    public static final String AUTH_CLIENT_ID = "556484818818-4am2rqla0pdrked873u9rq4ohrvdhpoh.apps.googleusercontent.com";
    public static final String AUTH_REDIRECT_URI = APP_ID + ":/oauth2callback";

    public static final String AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String TOKEN_ENDPOINT = "https://www.googleapis.com/oauth2/v4/token";

    private AppConstants() {
        // This utility class is not publicly instantiable
    }
}
