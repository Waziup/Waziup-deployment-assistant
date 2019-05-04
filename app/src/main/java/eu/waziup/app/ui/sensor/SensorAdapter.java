package eu.waziup.app.ui.sensor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
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
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.base.BaseViewHolder;

public class SensorAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<Sensor> sensors;
    private Callback mCallback;
    private MeasurementCallback mMeasurementCallback;

    public SensorAdapter(List<Sensor> sensors) {
        this.sensors = sensors;
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

    public void addItems(List<Sensor> sensors) {
        this.sensors.clear();
        this.sensors.addAll(sensors);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setMeasurementCallback(MeasurementCallback callback) {
        mMeasurementCallback = callback;
    }

    public interface Callback {
        void onItemClicked(Sensor sensor);
    }

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

        @BindView(R.id.sensor_measurements_title)
        TextView measurementsTitle;

        Sensor sensor;

        public SensorViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);

            if (sensors != null) {

                sensor = sensors.get(position);

                // id
                mSensorId.setText((TextUtils.isEmpty(sensor.getId())) ? sensor.getName() : sensor.getId());

                // TODO check visibility first for every view before setting visibility
                // date
                if (!TextUtils.isEmpty(sensor.getDateCreated())) {
                    mSensorDate.setVisibility(View.VISIBLE);
                    mSensorDate.setText(String.valueOf(DateTimeUtils.formatWithStyle(sensor.getDateCreated(),
                            DateTimeStyle.MEDIUM)));
                } else {
                    mSensorDate.setVisibility(View.GONE);
                }

                // owner
                if (!TextUtils.isEmpty(sensor.getOwner())) {
                    ownerIcon.setVisibility(View.VISIBLE);
                    mSensorOwner.setVisibility(View.VISIBLE);
                    mSensorOwner.setText(String.valueOf(sensor.getOwner()));
                } else {
                    ownerIcon.setVisibility(View.GONE);
                    mSensorOwner.setVisibility(View.GONE);
                }

                measurementContainer.removeAllViews();

                if (sensor.getMeasurements() != null) {

                    if (sensor.getMeasurements().size() > 0) {
                        measurementsTitle.setVisibility(View.VISIBLE);
                    } else {
                        measurementsTitle.setVisibility(View.GONE);
                    }

                    for (Measurement measurement : sensor.getMeasurements()) {
                        TextView measurementValue = new TextView(itemView.getContext());
                        measurementValue.setTextColor(itemView.getResources().getColor(R.color.white));
                        measurementValue.setGravity(Gravity.CENTER);
                        measurementValue.setBackground(itemView.getResources().getDrawable(R.drawable.bg_curved_accent_color));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 5, 3, 5);
                        measurementValue.setLayoutParams(params);
                        measurementValue.setWidth(120);
                        measurementValue.setHeight(70);
                        // for limiting the number of character that can be displayed
                        measurementValue.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                        measurementValue.setMaxLines(1);
                        measurementValue.setEllipsize(TextUtils.TruncateAt.END);

                        if (measurementValue.getParent() != null)
                            ((ViewGroup) measurementValue.getParent()).removeView(measurementValue);
                        measurementValue.setText(measurement.getId());
                            Log.e("-->measurement", String.valueOf(measurement.getId()));
                        measurementValue.setOnClickListener(view -> mMeasurementCallback.onItemClicked(measurement));
                        measurementContainer.addView(measurementValue);
                    }
                }
            } else {
                // todo find a better way of handling this condition
            }

            itemView.setOnClickListener(v -> mCallback.onItemClicked(sensors.get(getAdapterPosition())));

        }

        @Override
        protected void clear() {
            mSensorId.setText("");
            mSensorOwner.setText("");
            mSensorDate.setText("");
        }
    }
}
