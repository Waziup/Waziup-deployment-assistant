package eu.waziup.waziup_da_app.ui.qrscan;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import eu.waziup.waziup_da_app.R;
import eu.waziup.waziup_da_app.di.component.ActivityComponent;
import eu.waziup.waziup_da_app.ui.base.BaseFragment;

public class QRScanFragment extends BaseFragment implements QRScanMvpView {

    @Inject
    QRScanMvpPresenter<QRScanMvpView> mPresenter;

    public static final String TAG = "QRScanFragment";

    public static QRScanFragment newInstance() {
        Bundle args = new Bundle();
        QRScanFragment fragment = new QRScanFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qr_scan, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);
        }

        setUp(view);

        return view;
    }

    @OnClick(R.id.nav_back_btn)
    void onNavBackClick() {
        getBaseActivity().onFragmentDetached(TAG);
    }


    @Override
    protected void setUp(View view) {

    }


//    private void getQRCodeDetails(Bitmap bitmap) {
//        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
//                .setBarcodeFormats(
//                        FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
//                .build();
//        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);
//        val image = FirebaseVisionImage.fromBitmap(bitmap)
//        detector.detectInImage(image)
//                .addOnSuccessListener {
//            for (firebaseBarcode in it) {
//                codeData.text = firebaseBarcode.displayValue //Display contents inside the barcode
//                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            }
//        }
//                .addOnFailureListener {
//            it.printStackTrace();
//            Toast.makeText(getBaseActivity(), "Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
//        }
//    }

}
