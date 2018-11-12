package eu.waziup.waziup_da_app.ui.map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.ButterKnife;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.di.component.ActivityComponent;
import eu.waziup.waziup_da_app.ui.base.BaseFragment;

public class MapFragment extends BaseFragment implements MapMvpView {

//    @Inject
//    MapMvpPresenter<MapMvpView> mPresenter;

    public static final String TAG = "MapFragment";

    public static MapFragment newInstance() {
        Bundle args = new Bundle();
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
//            mPresenter.onAttach(this);
        }

        return view;
    }

    @Override
    protected void setUp(View view) {

    }
}
