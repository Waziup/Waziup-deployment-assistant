package eu.waziup.app.ui.measurementdetail;

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
import eu.waziup.app.data.network.model.sensor.Measurement;
import eu.waziup.app.di.component.ActivityComponent;
import eu.waziup.app.ui.base.BaseDialog;

import static eu.waziup.app.utils.AppConstants.MEASUREMENT_DETAIL_KEY;
import static eu.waziup.app.utils.AppConstants.MEASUREMENT_SENSOR_KEY;

public class MeasurementDetailDialog extends BaseDialog implements MeasurementDetailMvpView {

    @Inject
    MeasurementDetailMvpPresenter<MeasurementDetailMvpView> mPresenter;

    private Measurement measurement;

    @BindView(R.id.card_measurement_value)
    TextView mMeasurementValue;

    @BindView(R.id.card_measurement_icon)
    ImageView mIcon;

    @BindView(R.id.dialog_measurement_detail_name)
    TextView mMeasurementName;

    @BindView(R.id.card_measurement_kind)
    TextView mMeasurementKind;

    public static MeasurementDetailDialog newInstance(Measurement msrmnt) {
        MeasurementDetailDialog fragment = new MeasurementDetailDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MEASUREMENT_DETAIL_KEY, msrmnt);
        fragment.setArguments(bundle);
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
            // for setting the image
            if (measurement.getSensingDevice() != null)
                if (measurement.getSensingDevice().toLowerCase().equals("thermometer"))
                    mIcon.setImageDrawable(getBaseActivity().getResources().getDrawable(R.drawable.ic_mesurement_temp));

            if (!TextUtils.isEmpty(measurement.getId())) {// && mMeasurementName.getVisibility() != View.VISIBLE) {
                mMeasurementName.setVisibility(View.VISIBLE);
                mMeasurementName.setText(measurement.getId());
            } else if (!TextUtils.isEmpty(measurement.getName())) {// && mMeasurementName.getVisibility() != View.VISIBLE) {
                mMeasurementName.setVisibility(View.VISIBLE);
                mMeasurementName.setText(measurement.getName());
            } else {
                mMeasurementName.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(measurement.getQuantityKind())) {//mMeasurementKind.getVisibility() != View.VISIBLE
                mMeasurementKind.setVisibility(View.VISIBLE);
                mMeasurementKind.setText(measurement.getQuantityKind());
            } else {
                mMeasurementKind.setVisibility(View.GONE);
            }

            // for the value
            if (measurement.getLastValue() != null && !TextUtils.isEmpty(measurement.getLastValue().getValue())) {
                mMeasurementValue.setVisibility(View.VISIBLE);
                mMeasurementValue.setText(measurement.getLastValue().getValue());
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
