package eu.waziup.app.ui.sensor;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import eu.waziup.app.R;
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.ui.detail.DetailSensorMvpPresenter;
import eu.waziup.app.ui.detail.DetailSensorMvpView;

public class MeasurementDetailDialog extends Dialog {

    public Activity c;
    private Measurement measurement;
    private TextView btnCancel;


    TextView mMeasurementValue;
    ImageView mIcon;
    TextView mMeasurementName;
    TextView mMeasurementKind;

    public MeasurementDetailDialog(@NonNull Activity activity, Measurement measurement) {
        super(activity);
        this.c = activity;
        this.measurement = measurement;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_measurement_detail);

        mMeasurementValue = findViewById(R.id.card_measurement_value);
        mIcon = findViewById(R.id.card_measurement_icon);
        mMeasurementName = findViewById(R.id.card_measurement_name);
        mMeasurementKind = findViewById(R.id.card_measurement_kind);
        btnCancel = findViewById(R.id.btn_dialog_cancel);

        if (measurement != null) {

            // for setting the image
            if (measurement.getSensingDevice() != null)
                if (measurement.getSensingDevice().toLowerCase().equals("thermometer"))
                    mIcon.setImageDrawable(c.getResources().getDrawable(R.drawable.ic_mesurement_temp));

            // todo get back here and resolve this later on : measurementName being given different value on the same view
            if (!TextUtils.isEmpty(measurement.getId())) {
                if (mMeasurementName.getVisibility() != View.VISIBLE) {
                    mMeasurementName.setVisibility(View.VISIBLE);
                    mMeasurementName.setText(measurement.getId());
                }
            } else if (!TextUtils.isEmpty(measurement.getName())) {
                if (mMeasurementName.getVisibility() != View.VISIBLE) {
                    mMeasurementName.setVisibility(View.VISIBLE);
                    mMeasurementName.setText(measurement.getName());
                }
            } else {
                mMeasurementName.setVisibility(View.GONE);
            }

//            mMeasurementName.setText((TextUtils.isEmpty(measurement.getId())) ? measurement.getName() : measurement.getId());

            if (!TextUtils.isEmpty(measurement.getQuantityKind())) {
                if (mMeasurementKind.getVisibility() != View.VISIBLE) {
                    mMeasurementKind.setVisibility(View.VISIBLE);
                    mMeasurementKind.setText(measurement.getQuantityKind());
                }
            } else {
                mMeasurementKind.setVisibility(View.GONE);
            }

            // for the value
            if (measurement.getLastValue() != null)
                if (!TextUtils.isEmpty(measurement.getLastValue().getValue())) {
                    if (mMeasurementValue.getVisibility() != View.VISIBLE) {
                        mMeasurementValue.setVisibility(View.VISIBLE);
                        mMeasurementValue.setText(measurement.getLastValue().getValue());
                    }
                } else {
                    mMeasurementValue.setVisibility(View.GONE);
                }
        }

        btnCancel.setOnClickListener(v -> dismiss());
    }
}
