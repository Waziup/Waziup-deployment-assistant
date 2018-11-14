package eu.waziup.waziup_da_app.ui.detail;

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
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.data.network.model.sensor.Measurement;
import eu.waziup.waziup_da_app.ui.base.BaseViewHolder;

public class MeasurementAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    List<Measurement> measurements;

    Callback mCallback;
    EditCallback mEditCallback;
    DeleteCallback mDeleteCallback;

    public MeasurementAdapter(List<Measurement> measurements) {
        this.measurements = measurements;
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

    public void addItems(List<Measurement> measurements) {
        this.measurements.clear();
        this.measurements.addAll(measurements);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return measurements.size();
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
        void onItemClicked(Measurement measurement);
    }

    public interface EditCallback {
        void onItemEditClicked(Measurement measurement);
    }

    public interface DeleteCallback {
        void onItemDeleteClicked(Measurement measurement);
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

        Measurement measurement;

        public MeasurementViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mEdit.setOnClickListener(view -> mEditCallback.onItemEditClicked(measurements.get(getAdapterPosition())));
            mDelete.setOnClickListener(view -> mDeleteCallback.onItemDeleteClicked(measurements.get(getAdapterPosition())));
            // todo find out how is it possible to separate the whole view onClickListener with just only some part of the view clickListener
            itemView.setOnClickListener(view -> mCallback.onItemClicked(measurements.get(getAdapterPosition())));

        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            if (measurements != null) {
                measurement = measurements.get(position);

                // for setting the image
                if (measurement.getSensingDevice() != null)
                    if (measurement.getSensingDevice().toLowerCase().equals("thermometer"))
                        mIcon.setImageDrawable(itemView.getResources().getDrawable(R.drawable.ic_mesurement_temp));

                if (!TextUtils.isEmpty(measurement.getId())) {
                    mMeasurementName.setVisibility(View.VISIBLE);
                    mMeasurementName.setText(measurement.getId());
                } else if (!TextUtils.isEmpty(measurement.getName())) {
                    mMeasurementName.setVisibility(View.VISIBLE);
                    mMeasurementName.setText(measurement.getName());
                } else {
                    mMeasurementName.setVisibility(View.GONE);
                }

                mMeasurementName.setText((TextUtils.isEmpty(measurement.getId())) ? measurement.getName() : measurement.getId());


                if (!TextUtils.isEmpty(measurement.getQuantityKind())) {
                    mMeasurementKind.setVisibility(View.VISIBLE);
                    mMeasurementKind.setText(measurement.getQuantityKind());
                } else {
                    mMeasurementKind.setVisibility(View.GONE);
                }


                // for the value
                if (measurement.getLastValue() != null)
                    if (!TextUtils.isEmpty(measurement.getLastValue().getValue())) {
                        mMeasurementValue.setVisibility(View.VISIBLE);
                        mMeasurementValue.setText(measurement.getLastValue().getValue());
                    } else {
                        mMeasurementValue.setVisibility(View.GONE);
                    }

            }

        }

        @Override
        protected void clear() {
            mMeasurementValue.setText("");
            mMeasurementKind.setText("");
            mMeasurementName.setText("");
        }
    }
}
