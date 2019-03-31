package eu.waziup.app.ui.measurementedit;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseDialog;
import eu.waziup.app.ui.base.MvpPresenter;
import eu.waziup.app.ui.map.MapMvpPresenter;
import eu.waziup.app.ui.map.MapMvpView;
import eu.waziup.app.ui.sensordetail.DetailSensorFragment;

import static eu.waziup.app.utils.AppConstants.DETAIL_SENSOR_KEY;
import static eu.waziup.app.utils.AppConstants.MEASUREMENT_SENSOR_KEY;

public class EditMeasurementDialog extends BaseDialog implements EditMeasurementMvpView {

    @Inject
    EditMeasurementMvpPresenter<EditMeasurementMvpView> mPresenter;

    public Activity c;
    private EditText mId, mName, mSensor, mQuantityKind, mUnit;
    private TextView btnCancel, btnSubmit, title;

    private Measurement measurement;

    public static EditMeasurementDialog newInstance(Measurement msrmnt) {
        EditMeasurementDialog fragment = new EditMeasurementDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MEASUREMENT_SENSOR_KEY, msrmnt);
        fragment.setArguments(bundle);
        fragment.setArguments(bundle);
        return fragment;
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

        if (getArguments() != null)
            measurement = (Measurement) getArguments().getSerializable(MEASUREMENT_SENSOR_KEY);

        setUp(view);

        return view;
    }

    @OnClick(R.id.btn_dialog_cancel)
    void onCancelClicked() {
        mPresenter.onCancelClicked();

    }

    @Override
    protected void setUp(View view) {
        if (measurement != null) {
            if (!TextUtils.isEmpty(measurement.getId()))
                mId.setText(measurement.getId());

            if (!TextUtils.isEmpty(measurement.getName()))
                mName.setText(measurement.getName());

//            if (!TextUtils.isEmpty(measurement.getSensingDevice()))
//                mSensor.setText(measurement.getSensingDevice());

            if (!TextUtils.isEmpty(measurement.getQuantityKind()))
                mQuantityKind.setText(measurement.getQuantityKind());

            if (!TextUtils.isEmpty(measurement.getUnit()))
                mUnit.setText(measurement.getUnit());

        } else {
            Log.e("--->measurement Dialog","null");
            title.setText(R.string.dialog_add_measurement);
        }
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
