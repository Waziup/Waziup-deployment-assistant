package eu.waziup.app.data.network;


public final class ApiEndPoint {

    public final static String LOGIN = "auth/token";
    public final static String SENSOR = "sensors";
    public final static String USERS = "users";

    public final static String MEASUREMENT_DELETE = "sensors/{sensor_id}/measurements/{measurement_id}";
    public final static String MEASUREMENT_LIST = "sensors/{sensor_id}/measurements";

    private ApiEndPoint() {
        // This class is not publicly instantiable
    }
}
