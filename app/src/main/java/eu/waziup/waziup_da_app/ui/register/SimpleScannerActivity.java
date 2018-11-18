package eu.waziup.waziup_da_app.ui.register;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;

import com.google.zxing.Result;

import butterknife.BindView;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.ui.base.BaseActivity;
import eu.waziup.waziup_da_app.utils.BeepManager;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class SimpleScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;

    @BindView(R.id.scan_toolbar)
    Toolbar mToolbar;

    private BeepManager beepManager;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_simple_scanner);

        setUp();

        beepManager = new BeepManager(this);

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    protected void setUp() {
        Log.e("--->setup","got in");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
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
