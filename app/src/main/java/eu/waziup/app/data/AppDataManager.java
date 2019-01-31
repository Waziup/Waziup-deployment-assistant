package eu.waziup.app.data;


import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import eu.waziup.app.data.network.ApiHeader;
import eu.waziup.app.data.network.ApiHelper;
import eu.waziup.app.data.network.model.LoginRequest;
import eu.waziup.app.data.network.model.notification.NotificationResponse;
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.data.network.model.sensor.RegisterSensorResponse;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.data.network.model.user.User;
import eu.waziup.app.data.prefs.PreferencesHelper;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

@Singleton
public class AppDataManager implements DataManager {

    private static final String TAG = "AppDataManager";

    private final PreferencesHelper mPreferencesHelper;
    private final ApiHelper mApiHelper;

    @Inject
    public AppDataManager(PreferencesHelper preferencesHelper, ApiHelper apiHelper) {
        mPreferencesHelper = preferencesHelper;
        mApiHelper = apiHelper;
    }

    @Override
    public ApiHeader getApiHeader() {
        return mApiHelper.getApiHeader();
    }

    @Override
    public void setApiHeader(ApiHeader apiHeader) {

    }

    @Override
    public Single<String> serverLogin(LoginRequest.ServerLoginRequest request) {
        return mApiHelper.serverLogin(request);
    }

    @Override
    public Single<List<Sensor>> fetchSensors() {
        return mApiHelper.fetchSensors();
    }

    @Override
    public Single<ResponseBody> deleteMeasurement(String sensorId, String measurementId) {
        return mApiHelper.deleteMeasurement(sensorId, measurementId);
    }

    @Override
    public Single<List<Measurement>> getMeasurements(String sensor_id) {
        return mApiHelper.getMeasurements(sensor_id);
    }

    @Override
    public Single<RegisterSensorResponse> registerSensor(Sensor sensor) {
        return mApiHelper.registerSensor(sensor);
    }

    @Override
    public Single<List<User>> getUsers() {
        return mApiHelper.getUsers();
    }

    @Override
    public Single<List<NotificationResponse>> getNotifications() {
        return mApiHelper.getNotifications();
    }

    @Override
    public String getAccessToken() {
        return mPreferencesHelper.getAccessToken();
    }

    @Override
    public void setAccessToken(String accessToken) {
        mPreferencesHelper.setAccessToken(accessToken);
        mApiHelper.getApiHeader().setAccessToken(accessToken);
    }

    @Override
    public void updateUserToken(String accessToken) {
        setAccessToken(accessToken);

    }

    @Override
    public int getCurrentUserLoggedInMode() {
        return mPreferencesHelper.getCurrentUserLoggedInMode();
    }

    @Override
    public void setCurrentUserLoggedInMode(LoggedInMode mode) {
        mPreferencesHelper.setCurrentUserLoggedInMode(mode);
    }

    @Override
    public Long getCurrentUserId() {
        return mPreferencesHelper.getCurrentUserId();
    }

    @Override
    public void setCurrentUserId(Long userId) {
        mPreferencesHelper.setCurrentUserId(userId);
    }

    @Override
    public String getCurrentUserName() {
        return mPreferencesHelper.getCurrentUserName();
    }

    @Override
    public void setCurrentUserName(String userName) {
        mPreferencesHelper.setCurrentUserName(userName);
    }

    @Override
    public String getCurrentUserEmail() {
        return mPreferencesHelper.getCurrentUserEmail();
    }

    @Override
    public void setCurrentUserEmail(String email) {
        mPreferencesHelper.setCurrentUserEmail(email);
    }

    @Override
    public String getCurrentUserProfilePicUrl() {
        return mPreferencesHelper.getCurrentUserProfilePicUrl();
    }

    @Override
    public void setCurrentUserProfilePicUrl(String profilePicUrl) {
        mPreferencesHelper.setCurrentUserProfilePicUrl(profilePicUrl);
    }

    @Override
    public void updateUserInfo(String accessToken, LoggedInMode loggedInMode) {
        setAccessToken(accessToken);
        updateApiHeader(accessToken);
        setCurrentUserLoggedInMode(loggedInMode);
    }

    @Override
    public void updateUserInfo(String accessToken, String name, String picture, LoggedInMode loggedInMode) {
        setAccessToken(accessToken);
        setCurrentUserName(name);
        setCurrentUserProfilePicUrl(picture);
        updateApiHeader(accessToken);
        setCurrentUserLoggedInMode(loggedInMode);
    }

    @Override
    public void updateApiHeader(String accessToken) {
        mApiHelper.getApiHeader().setAccessToken(accessToken);
    }

    @Override
    public void setUserAsLoggedOut() {
        updateUserInfo(null, DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT);
    }
}
