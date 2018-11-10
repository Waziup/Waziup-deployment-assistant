package eu.waziup.waziup_da_app.ui.sensor;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.waziup.waziup_da_app.R;
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

            // date - todo date formatter has to be inserted in here
            if (!TextUtils.isEmpty(sensor.getDateCreated()))
                mSensorDate.setText(String.valueOf(sensor.getDateCreated()));

            // owner
            if (!TextUtils.isEmpty(sensor.getOwner()))
                mSensorOwner.setText(String.valueOf(sensor.getOwner()));

            // domain
            if (!TextUtils.isEmpty(sensor.getDomain()))
                mSensorDomain.setText(String.valueOf(sensor.getDomain()));

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
