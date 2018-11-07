package eu.waziup.waziup_da_app.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.models.sensor.Sensor;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    private List<Sensor> sensors;
    private Context context;

    public SensorAdapter(List<Sensor> sensors, Context context) {
        this.sensors = sensors;
        this.context = context;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_sensor, viewGroup, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder sensorViewHolder, int i) {
        Sensor sensor = sensors.get(i);
        sensorViewHolder.mSensorTitle.setText(String.valueOf(sensor.getName()));
        sensorViewHolder.mSensorName.setText(String.valueOf(sensor.getId()));
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

    public class SensorViewHolder extends RecyclerView.ViewHolder {

        TextView mSensorTitle, mSensorName;

        public SensorViewHolder(View view) {
            super(view);
            mSensorName = view.findViewById(R.id.sensor_name);
            mSensorTitle = view.findViewById(R.id.sensor_title);
        }
    }
}
