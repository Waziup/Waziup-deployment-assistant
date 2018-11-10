package eu.waziup.waziup_da_app.ui.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.di.component.ActivityComponent;
import eu.waziup.waziup_da_app.ui.base.BaseFragment;

public class RegisterSensorFragment extends BaseFragment implements RegisterSensorMvpView {

    @Inject
    RegisterSensorMvpPresenter<RegisterSensorMvpView> mPresenter;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, RegisterSensorFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register_sensor, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }

        return view;
    }

    @Override
    protected void setUp(View view) {
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle("Register Sensor");
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //todo get back here
//        if (item.getItemId() == android.R.id.home)
//            finish();
        return super.onOptionsItemSelected(item);
    }

}
