package eu.waziup.app.ui.sensordetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseDialog;

import static eu.waziup.app.utils.AppConstants.MEASUREMENT_DETAIL_KEY;

public class SensorDetailDialog extends BaseDialog implements SensorDetailMvpView {

    @Inject
    SensorDetailMvpPresenter<SensorDetailMvpView> mPresenter;

    private Sensor sensor;

    @BindView(R.id.card_measurement_value)
    TextView mMeasurementValue;

    @BindView(R.id.card_measurement_icon)
    ImageView mIcon;

    @BindView(R.id.dialog_measurement_detail_name)
    TextView mMeasurementName;

    @BindView(R.id.card_measurement_kind)
    TextView mMeasurementKind;

    public static SensorDetailDialog newInstance(Sensor msrmnt) {
        SensorDetailDialog fragment = new SensorDetailDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MEASUREMENT_DETAIL_KEY, msrmnt);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_measurement_detail, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {

            component.inject(this);

            setUnBinder(ButterKnife.bind(this, view));

            mPresenter.onAttach(this);
        }

        if (getArguments() != null)
            sensor = (Sensor) getArguments().getSerializable(MEASUREMENT_DETAIL_KEY);

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
            // for setting the image
            if (sensor.getSensingDevice() != null)
                if (sensor.getSensingDevice().toLowerCase().equals("thermometer"))
                    mIcon.setImageDrawable(getBaseActivity().getResources().getDrawable(R.drawable.ic_mesurement_temp));

            if (!TextUtils.isEmpty(sensor.getId())) {// && mMeasurementName.getVisibility() != View.VISIBLE) {
                mMeasurementName.setVisibility(View.VISIBLE);
                mMeasurementName.setText(sensor.getId());
            } else if (!TextUtils.isEmpty(sensor.getName())) {// && mMeasurementName.getVisibility() != View.VISIBLE) {
                mMeasurementName.setVisibility(View.VISIBLE);
                mMeasurementName.setText(sensor.getName());
            } else {
                mMeasurementName.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(sensor.getQuantityKind())) {//mMeasurementKind.getVisibility() != View.VISIBLE
                mMeasurementKind.setVisibility(View.VISIBLE);
                mMeasurementKind.setText(sensor.getQuantityKind());
            } else {
                mMeasurementKind.setVisibility(View.GONE);
            }

            // for the value
            if (sensor.getLastValue() != null && !TextUtils.isEmpty(sensor.getLastValue().getValue())) {
                mMeasurementValue.setVisibility(View.VISIBLE);
                mMeasurementValue.setText(sensor.getLastValue().getValue());
            } else {
                mMeasurementValue.setText(R.string.dialog_no_record);
            }
        }
    }

    @Override
    public void closeDialog() {
        dismiss();
    }
}
