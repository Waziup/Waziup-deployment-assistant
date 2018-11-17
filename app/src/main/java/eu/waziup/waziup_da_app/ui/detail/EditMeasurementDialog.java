package eu.waziup.waziup_da_app.ui.detail;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.data.network.model.sensor.Measurement;

public class EditMeasurementDialog extends Dialog {

    public Activity c;
    private EditText mId, mName, mSensor, mQuantityKind, mUnit;
    private Measurement measurement;
    private TextView btnCancel, btnSubmit, title;
    DetailSensorMvpPresenter<DetailSensorMvpView> mPresenter;

    public EditMeasurementDialog(@NonNull Activity activity, Measurement measurement, DetailSensorMvpPresenter<DetailSensorMvpView> mPresenter) {
        super(activity);
        this.c = activity;
        this.measurement = measurement;
        this.mPresenter = mPresenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_measurement);

        mId = findViewById(R.id.dialog_measurement_id);
        mName = findViewById(R.id.dialog_measurement_name);
        mSensor = findViewById(R.id.dialog_measurement_sensor);
        mQuantityKind = findViewById(R.id.dialog_measurement_quantity_kind);
        mUnit = findViewById(R.id.dialog_measurement_unit);
        title = findViewById(R.id.dialog_measurement_title);
        btnCancel = findViewById(R.id.btn_dialog_cancel);
        btnSubmit = findViewById(R.id.btn_dialog_submit);

        if (this.getWindow() != null)
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (measurement != null) {
            if (!TextUtils.isEmpty(measurement.getId()))
                mId.setText(measurement.getId());

            if (!TextUtils.isEmpty(measurement.getName()))
                mName.setText(measurement.getName());

            if (!TextUtils.isEmpty(measurement.getSensingDevice()))
                mSensor.setText(measurement.getSensingDevice());

            if (!TextUtils.isEmpty(measurement.getQuantityKind()))
                mQuantityKind.setText(measurement.getQuantityKind());

            if (!TextUtils.isEmpty(measurement.getUnit()))
                mUnit.setText(measurement.getUnit());

        } else {
            Log.e("--->measurement Dialog","null");
            title.setText(R.string.dialog_add_measurement);
        }

        btnCancel.setOnClickListener(v -> dismiss());
        btnSubmit.setOnClickListener(view -> {
            // submit the form to the api
        });
    }
}
