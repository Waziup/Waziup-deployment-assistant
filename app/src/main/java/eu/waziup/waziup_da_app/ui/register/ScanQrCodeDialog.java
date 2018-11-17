package eu.waziup.waziup_da_app.ui.register;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.utils.CommonUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


public class ScanQrCodeDialog extends Dialog {

    public Activity c;

    public ScanQrCodeDialog(Activity activity) {
        super(activity);
        this.c = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_scan_qr_code);

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(v -> {
            CommonUtils.toast("working pro");
        });

    }
}
