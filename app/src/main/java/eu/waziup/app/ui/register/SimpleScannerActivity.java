package eu.waziup.app.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.google.zxing.Result;

import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.app.R;
import eu.waziup.app.ui.base.BaseActivity;
import eu.waziup.app.utils.BeepManager;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SimpleScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

//    @BindView(R.id.scan_toolbar)
//    Toolbar mToolbar;

    public static final String TAG = "SimpleScannerActivity";

    private BeepManager beepManager;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_simple_scanner);

        ButterKnife.bind(this);

        setUp();

        beepManager = new BeepManager(this);

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }


    @OnClick(R.id.nav_back_btn)
    void onNavBackClick() {
        finish();
    }


    @Override
    protected void setUp() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        beepManager.close();
        mScannerView.stopCamera();
        super.onPause();
    }

    @Override
    public void handleResult(Result rawResult) {

        beepManager.playBeepSoundAndVibrate();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", rawResult.getText());
        setResult(RESULT_OK, returnIntent);
        finish();

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
//        Handler handler = new Handler();
//        handler.postDelayed(() ->
//                mScannerView.resumeCameraPreview(SimpleScannerActivity.this), 2000);
    }
}
