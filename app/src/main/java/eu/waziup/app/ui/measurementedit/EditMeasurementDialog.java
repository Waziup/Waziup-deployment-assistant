package eu.waziup.app.ui.measurementedit;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseDialog;
import eu.waziup.app.ui.base.MvpPresenter;
import eu.waziup.app.ui.map.MapMvpPresenter;
import eu.waziup.app.ui.map.MapMvpView;

public class EditMeasurementDialog extends BaseDialog implements EditMeasurementMvpView {

    public Activity c;
    private EditText mId, mName, mSensor, mQuantityKind, mUnit;
    private Measurement measurement;
    private TextView btnCancel, btnSubmit, title;

    @Inject
    EditMeasurementMvpPresenter<EditMeasurementMvpView> mPresenter;
//    DetailSensorMvpPresenter<EditMeasurementMvpView> mPresenter;

//    public EditMeasurementDialog(@NonNull Activity activity, Measurement measurement, EditMeasurementMvpPresenter<EditMeasurementMvpView> mPresenter) {
//        super(activity);
//        this.c = activity;
//        this.measurement = measurement;
//        this.mPresenter = mPresenter;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.dialog_edit_measurement);
//
//        mId = findViewById(R.id.dialog_measurement_id);
//        mName = findViewById(R.id.dialog_measurement_name);
////        mSensor = findViewById(R.id.dialog_measurement_sensor);
//        mQuantityKind = findViewById(R.id.dialog_measurement_quantity_kind);
//        mUnit = findViewById(R.id.dialog_measurement_unit);
//        title = findViewById(R.id.dialog_measurement_title);
//        btnCancel = findViewById(R.id.btn_dialog_cancel);
//        btnSubmit = findViewById(R.id.btn_dialog_submit);
//
//        if (this.getWindow() != null)
//            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//        if (measurement != null) {
//            if (!TextUtils.isEmpty(measurement.getId()))
//                mId.setText(measurement.getId());
//
//            if (!TextUtils.isEmpty(measurement.getName()))
//                mName.setText(measurement.getName());
//
////            if (!TextUtils.isEmpty(measurement.getSensingDevice()))
////                mSensor.setText(measurement.getSensingDevice());
//
//            if (!TextUtils.isEmpty(measurement.getQuantityKind()))
//                mQuantityKind.setText(measurement.getQuantityKind());
//
//            if (!TextUtils.isEmpty(measurement.getUnit()))
//                mUnit.setText(measurement.getUnit());
//
//        } else {
//            Log.e("--->measurement Dialog","null");
//            title.setText(R.string.dialog_add_measurement);
//        }
//
//        btnCancel.setOnClickListener(v -> dismiss());
//        btnSubmit.setOnClickListener(view -> {
//            // submit the form to the api
//        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_measurement, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {

            component.inject(this);

            setUnBinder(ButterKnife.bind(this, view));

            mPresenter.onAttach(this);
        }

        setUp(view);

        return view;
    }

    @OnClick(R.id.btn_dialog_cancel)
    void onCancelClicked() {

        mPresenter.onCancelClicked();

    }

    @Override
    protected void setUp(View view) {

    }

    @Override
    public void closeDialog() {
        dismiss();
    }

    @Override
    public void onDestroyView() {
        mPresenter.onDetach();
        super.onDestroyView();
    }
}
