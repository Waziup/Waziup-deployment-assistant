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

    public static final String MEASUREMENT_DETAIL_KEY = "MEASUREMENT_DETAIL_KEY";

    //556484818818-lmptgg0pseli19di9h4h7k4t941oifdd.apps.googleusercontent.com - new
    //556484818818-4am2rqla0pdrked873u9rq4ohrvdhpoh.apps.googleusercontent.com - old
//    public static final String AUTH_CLIENT_ID = "556484818818-lmptgg0pseli19di9h4h7k4t941oifdd.apps.googleusercontent.com";
//    public static final String AUTH_REDIRECT_URI = APP_ID + ":/oauth2callback";//:/urn:ietf:wg:oauth:2.0:oob

    public static final String AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/auth";
    public static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";

    public static final String AUTH_CLIENT_ID = "dashboard";
    public static final String AUTH_REDIRECT_URI = "net.openid.appauthdemo:/oauth2redirect";
    public static final String AUTH_SCOPE = "openid email profile";
    public static final String AUTH_DISCOVERY_URI = "https://keycloak.waziup.io/auth/realms/waziup/protocol/openid-connect/auth";
    public static final String AUTH_AUTHORIZATION_ENDPOINT = "https://keycloak.waziup.io/auth/realms/waziup/protocol/openid-connect/auth";
    public static final String AUTH_TOKEN_ENDPOINT = "https://keycloak.waziup.io/auth/realms/waziup/protocol/openid-connect/token";
    public static final String AUTH_REGISTRATION_ENDPOINT = "https://keycloak.waziup.io/auth/realms/waziup/clients-registrations/openid-connect";
    public static final String AUTH_USER_INFO_ENDPOINT = "https://keycloak.waziup.io/auth/realms/waziup/protocol/openid-connect/userinfo";
    public static final boolean AUTH_HTTPS_REQUIRED = true;

    private AppConstants() {
        // This utility class is not publicly instantiable
    }
}
