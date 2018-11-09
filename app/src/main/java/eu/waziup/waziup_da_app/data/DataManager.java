package eu.waziup.waziup_da_app.data;


import eu.waziup.waziup_da_app.data.network.ApiHelper;
import eu.waziup.waziup_da_app.data.prefs.PreferencesHelper;
import io.reactivex.Observable;

public interface DataManager extends PreferencesHelper, ApiHelper {

    void updateApiHeader(String accessToken);

    void setUserAsLoggedOut();

    void updateUserInfo(
            String accessToken,
            Long userId,
            LoggedInMode loggedInMode,
            String userName,
            String email,
            String profilePicPath);

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
