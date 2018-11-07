package eu.waziup.waziup_da_app.base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import eu.waziup.waziup_da_app.R;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected abstract void setUp();
}
