package eu.waziup.app.ui.notification;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.notification.NotificationResponse;
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.data.network.model.sensor.Sensor;
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
                .inflate(R.layout.card_sensor, viewGroup, false);
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

        @BindView(R.id.sensor_date)
        TextView mSensorDate;

        @BindView(R.id.sensor_id)
        TextView mSensorId;

        @BindView(R.id.sensor_owner)
        TextView mSensorOwner;

        @BindView(R.id.sensor_domain)
        TextView mSensorDomain;

        @BindView(R.id.measurement_container)
        LinearLayout measurementContainer;

        @BindView(R.id.icon_sensor_owner)
        ImageView ownerIcon;

        @BindView(R.id.sensor_measurements_title)
        TextView measurementsTitle;

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

            } else {
                // todo find a better way of handling this condition
            }

            itemView.setOnClickListener(v -> mCallback.onItemClicked(notificationResponse.get(getAdapterPosition())));

        }

        @Override
        protected void clear() {
            mSensorId.setText("");
            mSensorDomain.setText("");
            mSensorOwner.setText("");
            mSensorDate.setText("");
        }
    }
}
