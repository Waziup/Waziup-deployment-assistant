package eu.waziup.app.data;


import eu.waziup.app.data.network.ApiHelper;
import eu.waziup.app.data.prefs.PreferencesHelper;

public interface DataManager extends PreferencesHelper, ApiHelper {

    void updateApiHeader(String accessToken);

    void setUserAsLoggedOut();

    void setLoggedInMode();

    void updateUserInfo(String accessToken, String name, String picture, LoggedInMode loggedInMode);

    void updateUserToken(String accessToken);

    enum LoggedInMode {

        LOGGED_IN_MODE_LOGGED_OUT(0),
        LOGGED_IN_MODE_GOOGLE(1),
        LOGGED_IN_MODE_FB(2),
        LOGGED_IN_MODE_SERVER(3);

        private final int mType;

        LoggedInMode(int type) {
            mType = type;
        }

        public int getType() {
            return mType;
        }
    }
}
