package eu.waziup.app.ui.notification;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.notification.NotificationResponse;
import eu.waziup.app.ui.base.BaseViewHolder;

public class NotificationAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<NotificationResponse> notificationResponse;
    private Callback mCallback;

    public NotificationAdapter(List<NotificationResponse> notificationResponse) {
        this.notificationResponse = notificationResponse;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_notification, viewGroup, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder sensorViewHolder, int i) {
        sensorViewHolder.onBind(i);
    }

    public void addItems(List<NotificationResponse> notificationResponse) {
        this.notificationResponse.clear();
        this.notificationResponse.addAll(notificationResponse);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notificationResponse.size();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onItemClicked(NotificationResponse notification);
    }

    public class SensorViewHolder extends BaseViewHolder {

        @BindView(R.id.notification_sensor_name)
        TextView mNotificationSensorName;

        @BindView(R.id.ic_notification_fb)
        ImageView icFb;

        @BindView(R.id.ic_notification_sms)
        ImageView icSms;

        @BindView(R.id.ic_notification_twitter)
        ImageView icTwitter;

        @BindView(R.id.ic_notification_voice_call)
        ImageView icVoice;

        @BindView(R.id.notification_message)
        TextView mNotificationMessage;

        @BindView(R.id.notification_owner)
        TextView mNotificationOwner;

        NotificationResponse notification;

        public SensorViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            if (notificationResponse != null) {

                notification = notificationResponse.get(position);

                // sensor name
                if (notification.getCondition().getSensors().size() > 0) {
                    for (int i = 0; i < notification.getCondition().getSensors().size(); i++) {
                        mNotificationSensorName.setText(String.valueOf(
                                (notification.getCondition().getSensors().size() == 1) ? notification.getCondition().getSensors().get(i)
                                        : (notification.getCondition().getSensors().size() == i) ? notification.getCondition().getSensors().get(i)
                                        : notification.getCondition().getSensors().get(i) + ", "));
                    }
                } else {
                    mNotificationSensorName.setVisibility(View.GONE);
                }

                // measurement
//                if (notification.getCondition().getMeasurements().size() > 0) {
//                    for (int i = 0; i < notification.getCondition().getMeasurements().size(); i++) {
//                        mNotificationMeasurement.setText(String.valueOf(
//                                (notification.getCondition().getMeasurements().size() == 1) ? notification.getCondition().getMeasurements().get(i)
//                                        : (notification.getCondition().getMeasurements().size() == i) ? notification.getCondition().getMeasurements().get(i)
//                                        : notification.getCondition().getMeasurements().get(i) + ", "));
//                    }
//                } else {
//                    mNotificationMeasurement.setVisibility(View.GONE);
//                }

                // expression
//                if (!TextUtils.isEmpty(notification.getCondition().getExpression())) {
//                    mNotificationExpression.setText(String.valueOf(notification.getCondition().getExpression()));
//                } else {
//                    mNotificationExpression.setVisibility(View.GONE);
//                }

                // message
                if (!TextUtils.isEmpty(notification.getNotification().getMessage())) {
                    mNotificationMessage.setText(String.valueOf(notification.getNotification().getMessage()));
                } else {
                    mNotificationMessage.setVisibility(View.GONE);
                }

                // owner
                if (notification.getNotification().getUsernames().size() > 0) {
                    for (int i = 0; i < notification.getNotification().getUsernames().size(); i++) {
                        mNotificationOwner.setText(String.valueOf(
                                (notification.getNotification().getUsernames().size() == 1) ? notification.getNotification().getUsernames().get(i)
                                        : (notification.getNotification().getUsernames().size() == i) ? notification.getNotification().getUsernames().get(i)
                                        : notification.getNotification().getUsernames().get(i) + ", "));
                    }
                } else {
                    mNotificationOwner.setVisibility(View.GONE);
                }

                if (notification.getNotification() != null && notification.getNotification().getChannels() != null) {
                    for (String social : notification.getNotification().getChannels()) {
                        if (social.equals("twitter"))
                            icTwitter.setVisibility(View.VISIBLE);
                        else if (social.equals("facebook"))
                            icFb.setVisibility(View.VISIBLE);
                        else if (social.equals("sms"))
                            icSms.setVisibility(View.VISIBLE);
                        else if (social.equals("voice"))
                            icVoice.setVisibility(View.VISIBLE);
                        else {
                            icFb.setVisibility(View.GONE);
                            icTwitter.setVisibility(View.GONE);
                            icSms.setVisibility(View.GONE);
                            icVoice.setVisibility(View.GONE);
                        }
                    }
                }

                // shared at
//                if (notification.getCondition().getSensors().size() > 0) {
//                    for (int i = 0; i < notification.getNotification().getChannels().size(); i++) {
//                        mNotificationSharedAt.setText(String.valueOf(
//                                (notification.getNotification().getChannels().size() == 1) ?
//                                        notification.getNotification().getChannels().get(i)
//                                        : (notification.getNotification().getChannels().size() == i) ?
//                                        notification.getNotification().getChannels().get(i)
//                                        : notification.getNotification().getChannels().get(i) + ", "));
//                    }
//                } else {
//                    mNotificationSharedAt.setVisibility(View.GONE);
//                }

            } else {
                // todo find a better way of handling this condition
            }

            itemView.setOnClickListener(v -> mCallback.onItemClicked(notificationResponse.get(getAdapterPosition())));

        }

        @Override
        protected void clear() {
            mNotificationSensorName.setText("");
//            mNotificationMeasurement.setText("");
//            mNotificationExpression.setText("");
            mNotificationMessage.setText("");
            mNotificationOwner.setText("");
//            mNotificationSharedAt.setText("");
        }
    }
}
