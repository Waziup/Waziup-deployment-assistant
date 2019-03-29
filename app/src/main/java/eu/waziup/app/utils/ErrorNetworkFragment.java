package eu.waziup.app.utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.waziup.app.R;
import eu.waziup.app.ui.base.BaseFragment;

public class ErrorNetworkFragment extends BaseFragment {

    public static final String TAG = "ErrorNetworkFragment";

    public static ErrorNetworkFragment newInstance() {
        Bundle args = new Bundle();
        ErrorNetworkFragment fragment = new ErrorNetworkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error_net, container, false);
        return view;
    }

    @Override
    protected void setUp(View view) {

    }

    @Override
    public void onBackPressed() {

    }
}
