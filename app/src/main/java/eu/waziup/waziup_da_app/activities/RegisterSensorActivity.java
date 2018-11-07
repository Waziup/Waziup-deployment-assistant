package eu.waziup.waziup_da_app.activities;

import android.os.Bundle;
import android.view.MenuItem;

import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.base.BaseActivity;

public class RegisterSensorActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_sensor);

        setUp();
    }

    @Override
    protected void setUp() {
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Register Sensor");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
