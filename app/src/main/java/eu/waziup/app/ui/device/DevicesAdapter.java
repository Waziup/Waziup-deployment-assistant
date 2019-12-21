package eu.waziup.app.ui.device;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.devices.Device;
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.ui.base.BaseViewHolder;

public class DevicesAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<Device> devices;
    private Callback mCallback;
    private MeasurementCallback mMeasurementCallback;

    public DevicesAdapter(List<Device> devices) {
        this.devices = devices;
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

    /**
     * handled setting devices list to the adapter
     * @param devices devices list to be set on the adapter
     */
    public void addItems(List<Device> devices) {
        this.devices.clear();
        this.devices.addAll(devices);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setMeasurementCallback(MeasurementCallback callback) {
        mMeasurementCallback = callback;
    }

    // callback method interface for capturing the item click on the devices list
    public interface Callback {
        void onItemClicked(Device device);
    }

    // callback method interface for capturing the item click on the measurement list
    public interface MeasurementCallback {
        void onItemClicked(Measurement measurement);
    }

    public class SensorViewHolder extends BaseViewHolder {

        @BindView(R.id.sensor_date)
        TextView mSensorDate;

        @BindView(R.id.sensor_id)
        TextView mSensorId;

        @BindView(R.id.sensor_owner)
        TextView mSensorOwner;

        @BindView(R.id.measurement_container)
        LinearLayout measurementContainer;

        @BindView(R.id.icon_sensor_owner)
        ImageView ownerIcon;

        Device device;

        public SensorViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            if (devices != null) {

                device = devices.get(position);

                // id
                mSensorId.setText((TextUtils.isEmpty(device.getId())) ? device.getName() : device.getId());

                // date
//                if (!TextUtils.isEmpty(device.getDateCreated())) {
//                    mSensorDate.setVisibility(View.VISIBLE);
//                    mSensorDate.setText(String.valueOf(DateTimeUtils.formatWithStyle(device.getDateCreated(),
//                            DateTimeStyle.MEDIUM)));
//                } else {
//                    mSensorDate.setVisibility(View.GONE);
//                }

                // owner
//                if (!TextUtils.isEmpty(device.getOwner())) {
//                    ownerIcon.setVisibility(View.VISIBLE);
//                    mSensorOwner.setVisibility(View.VISIBLE);
//                    mSensorOwner.setText(String.valueOf(device.getOwner()));
//                } else {
////                    ownerIcon.setVisibility(View.GONE);
////                    mSensorOwner.setVisibility(View.GONE);
//                    ownerIcon.setVisibility(View.VISIBLE);
//                    mSensorOwner.setVisibility(View.VISIBLE);
//                    mSensorOwner.setText("Guest user");
//
//                }

                measurementContainer.removeAllViews();

//                if (device.getMeasurements() != null) {
//
//                    for (Measurement measurement : device.getMeasurements()) {
//                        TextView measurementValue = new TextView(itemView.getContext());
//                        measurementValue.setTextColor(itemView.getResources().getColor(R.color.white));
//                        measurementValue.setGravity(Gravity.CENTER);
//                        measurementValue.setBackground(itemView.getResources().getDrawable(R.drawable.bg_curved_accent_color));
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                                ViewGroup.LayoutParams.WRAP_CONTENT,
//                                ViewGroup.LayoutParams.WRAP_CONTENT
//                        );
//                        params.setMargins(0, 5, 3, 5);
//                        measurementValue.setLayoutParams(params);
//                        measurementValue.setWidth(120);
//                        measurementValue.setHeight(70);
//                        // for limiting the number of character that can be displayed
//                        measurementValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
//                        measurementValue.setMaxLines(1);
//                        measurementValue.setEllipsize(TextUtils.TruncateAt.END);
//
//                        if (measurementValue.getParent() != null)
//                            ((ViewGroup) measurementValue.getParent()).removeView(measurementValue);
//                        measurementValue.setText(measurement.getId());
//                        measurementValue.setOnClickListener(view -> mMeasurementCallback.onItemClicked(measurement));
//                        measurementContainer.addView(measurementValue);
//                    }
//                }
            }

            itemView.setOnClickListener(v -> mCallback.onItemClicked(devices.get(getAdapterPosition())));

        }

        /**
         * clear the views when there is no data found for the particular item or when clearing is required
         */
        @Override
        protected void clear() {
            mSensorId.setText("");
            mSensorOwner.setText("");
            mSensorDate.setText("");
        }
    }
}
