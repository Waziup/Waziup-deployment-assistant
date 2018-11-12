package eu.waziup.waziup_da_app.ui.sensor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.data.network.model.sensor.Measurement;
import eu.waziup.waziup_da_app.data.network.model.sensor.Sensor;
import eu.waziup.waziup_da_app.ui.base.BaseViewHolder;

public class SensorAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<Sensor> sensors;
    private Callback mCallback;

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

    public interface Callback {
        void onItemClicked(Sensor sensor);
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

        Sensor sensor;

        public SensorViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(v -> mCallback.onItemClicked(sensors.get(getAdapterPosition())));
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            sensor = sensors.get(position);

            // id
            if (!TextUtils.isEmpty(sensor.getId()) || !TextUtils.isEmpty(sensor.getName()))
                mSensorId.setText((TextUtils.isEmpty(sensor.getId())) ? sensor.getName() : sensor.getId());

            // date
            if (!TextUtils.isEmpty(sensor.getDateCreated()))
                mSensorDate.setText(String.valueOf(DateTimeUtils.formatWithStyle(sensor.getDateCreated(),
                        DateTimeStyle.MEDIUM)));

            // owner
            if (!TextUtils.isEmpty(sensor.getOwner()))
                mSensorOwner.setText(String.valueOf(sensor.getOwner()));

            // domain
            if (!TextUtils.isEmpty(sensor.getDomain()))
                mSensorDomain.setText(String.valueOf(sensor.getDomain()));

            measurementContainer.removeAllViews();
            for (Measurement measurement : sensor.getMeasurements()) {
                TextView measurementValue = new TextView(itemView.getContext());
                measurementValue.setTextColor(itemView.getResources().getColor(R.color.white));
                measurementValue.setGravity(Gravity.CENTER);
                measurementValue.setBackground(itemView.getResources().getDrawable(R.drawable.bg_curved_primary_color));
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
                measurementContainer.addView(measurementValue);
            }
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
