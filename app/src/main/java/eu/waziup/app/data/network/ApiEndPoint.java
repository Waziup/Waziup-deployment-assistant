package eu.waziup.app.data.network;


public final class ApiEndPoint {

    // AUTH
    public final static String LOGIN = "auth/token";

    // DEVICES
    public final static String DEVICES = "devices";
    public static final String DEVICES_DELETE = "devices/{device_id}";

    // NOTIFICATION
    public final static String NOTIFICATION = "notifications";

    // USER
    public final static String USERS = "users";

    // SENSOR
    public final static String SENSOR_LIST = "devices/{device_id}/sensors";
    public final static String SENSOR_DELETE = "devices/{device_id}/sensors/{sensor_id}";
    public static final String SENSOR_UNDER_DEVICES = "devices/{device_id}/sensors";

    private ApiEndPoint() {
        // This class is not publicly instantiable
    }
}
