package eu.waziup.app.ui.devicesdetail;

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
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.base.BaseViewHolder;

public class MeasurementAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    List<Sensor> sensors;

    Callback mCallback;
    EditCallback mEditCallback;
    DeleteCallback mDeleteCallback;
    String sensorId = "";

    public MeasurementAdapter(List<Sensor> sensors) {
        this.sensors = sensors;
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_measurement, viewGroup, false);
        return new MeasurementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.onBind(i);
    }

    public void addItems(String sensorId, List<Sensor> sensors) {
        this.sensorId = sensorId;
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

    public void setEditCallback(EditCallback callback) {
        mEditCallback = callback;
    }

    public void setDeleteCallback(DeleteCallback callback) {
        mDeleteCallback = callback;
    }

    public interface Callback {
        void onItemClicked(Sensor sensor);
    }

    /**
     * callback interface for editing sensor item from the list
     */
    public interface EditCallback {
        void onItemEditClicked(Sensor sensor);
    }

    /**
     * callback interface for deleting item from the sensor list
     */
    public interface DeleteCallback {
        void onItemDeleteClicked(String sensorId, Sensor sensor);
    }

    public class MeasurementViewHolder extends BaseViewHolder {

        @BindView(R.id.card_measurement_value)
        TextView mMeasurementValue;

        @BindView(R.id.card_measurement_icon)
        ImageView mIcon;

        @BindView(R.id.card_measurement_edit)
        ImageView mEdit;

        @BindView(R.id.card_measurement_name)
        TextView mMeasurementName;

        @BindView(R.id.card_measurement_kind)
        TextView mMeasurementKind;

        @BindView(R.id.card_measurement_delete)
        ImageView mDelete;

        Sensor sensor;

        public MeasurementViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mEdit.setOnClickListener(view -> mEditCallback.onItemEditClicked(sensors.get(getAdapterPosition())));
            mDelete.setOnClickListener(view -> mDeleteCallback.onItemDeleteClicked(sensorId, sensors.get(getAdapterPosition())));
            // todo find out how is it possible to separate the whole view onClickListener with just only some part of the view clickListener
            itemView.setOnClickListener(view -> mCallback.onItemClicked(sensors.get(getAdapterPosition())));

        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            if (sensors != null) {
                sensor = sensors.get(position);

                // for setting the image
                if (sensor.getSensingDevice() != null)
                    if (sensor.getSensingDevice().toLowerCase().equals("thermometer"))
                        mIcon.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_mesurement_temp));

                if (!TextUtils.isEmpty(sensor.getId())) {
                    mMeasurementName.setVisibility(View.VISIBLE);
                    mMeasurementName.setText(sensor.getId());
                } else if (!TextUtils.isEmpty(sensor.getName())) {
                    mMeasurementName.setVisibility(View.VISIBLE);
                    mMeasurementName.setText(sensor.getName());
                } else {
                    mMeasurementName.setVisibility(View.GONE);
                }

                mMeasurementName.setText((TextUtils.isEmpty(sensor.getId())) ? sensor.getName() : sensor.getId());

                if (!TextUtils.isEmpty(sensor.getQuantityKind())) {
                    mMeasurementKind.setVisibility(View.VISIBLE);
                    mMeasurementKind.setText(sensor.getQuantityKind());
                } else {
                    mMeasurementKind.setVisibility(View.GONE);
                }


                // for the value
                if (sensor.getLastValue() != null)
                    if (!TextUtils.isEmpty(sensor.getLastValue().getValue())) {
                        mMeasurementValue.setVisibility(View.VISIBLE);
                        mMeasurementValue.setText(sensor.getLastValue().getValue());
                    } else {
                        mMeasurementValue.setVisibility(View.GONE);
                    }

            }

        }

        /**
         * method for clearing the views when clearing is required
         */
        @Override
        protected void clear() {
            mMeasurementValue.setText("");
            mMeasurementKind.setText("");
            mMeasurementName.setText("");
        }
    }
}
