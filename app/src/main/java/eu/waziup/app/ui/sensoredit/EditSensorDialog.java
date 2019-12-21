package eu.waziup.app.ui.sensoredit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseDialog;

import static eu.waziup.app.utils.AppConstants.MEASUREMENT_SENSOR_KEY;

public class EditSensorDialog extends BaseDialog implements EditSensorMvpView {

    @Inject
    EditSensorMvpPresenter<EditSensorMvpView> mPresenter;

    @BindView(R.id.dialog_measurement_id)
    TextView mId;

    @BindView(R.id.dialog_measurement_name)
    TextView mName;

    @BindView(R.id.dialog_measurement_quantity_kind)
    TextView mQuantityKind;

    @BindView(R.id.dialog_measurement_unit)
    TextView mUnit;

    @BindView(R.id.dialog_measurement_title)
    TextView title;

    private Sensor sensor;

    public static EditSensorDialog newInstance(Sensor msrmnt) {
        EditSensorDialog fragment = new EditSensorDialog();
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
            sensor = (Sensor) getArguments().getSerializable(MEASUREMENT_SENSOR_KEY);

        setUp(view);

        return view;
    }

    @OnClick(R.id.btn_dialog_cancel)
    void onCancelClicked() {
        mPresenter.onCancelClicked();

    }

    @Override
    protected void setUp(View view) {
        if (sensor != null) {
            if (!TextUtils.isEmpty(sensor.getId()))
                mId.setText(sensor.getId());

            if (!TextUtils.isEmpty(sensor.getName()))
                mName.setText(sensor.getName());

//            if (!TextUtils.isEmpty(sensor.getSensingDevice()))
//                mSensor.setText(sensor.getSensingDevice());

            if (!TextUtils.isEmpty(sensor.getQuantityKind()))
                mQuantityKind.setText(sensor.getQuantityKind());

            if (!TextUtils.isEmpty(sensor.getUnit()))
                mUnit.setText(sensor.getUnit());

        } else {
            Log.e("--->sensor Dialog", "null");
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
