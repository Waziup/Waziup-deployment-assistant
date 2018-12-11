package eu.waziup.app.ui.notification;

import java.util.List;

import eu.waziup.app.data.network.model.notification.NotificationResponse;
import eu.waziup.app.ui.base.MvpView;

public interface NotificationMvpView extends MvpView {

    void showNotifications(List<NotificationResponse> notificationResponses);
}
