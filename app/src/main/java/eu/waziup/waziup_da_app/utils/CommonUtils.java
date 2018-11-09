/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package eu.waziup.waziup_da_app.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;

import eu.waziup.waziup_da_app.DaApp;
import eu.waziup.waziup_da_app.R;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.HttpException;

/**
 * Created by KidusMT.
 */

public final class CommonUtils {

    private static final String TAG = "CommonUtils";

    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
//        progressDialog.setCancelable(false);//todo cancel this when needed
//        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void toast(String msg) {
        Toast.makeText(DaApp.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static String getUniqueIMEIId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            String imei = telephonyManager.getDeviceId();
            Log.e("imei", "=" + imei);
            if (imei != null && !imei.isEmpty()) {
                return imei;
            } else {
                return android.os.Build.SERIAL;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not_found";
    }

    public static String getFileName(Uri uri, Context context) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static RequestBody toRequestBody(String request) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), request);
    }

    public static String getErrorMessage(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException) {
            return "Please try again.";
        } else if (throwable instanceof IOException) {
            return "Please connect to the internet.";
        } else if (throwable instanceof HttpException) {
            int code = ((HttpException) throwable).response().code();
            if (code >= 400 && code < 404) {
                return DaApp.getContext().getString(R.string.error_invalid_credential);
            } else if (code == 404) {
                return DaApp.getContext().getString(R.string.error_file_does_not_exist);
            } else if (code == 500) {
                return DaApp.getContext().getString(R.string.error_server_error);
            } else if (code == 503) {
                return DaApp.getContext().getString(R.string.error_server_unreachable);
            } else {

                return DaApp.getContext().getString(R.string.error_something_wrong_happend);
//                ResponseBody responseBody = ((HttpException) throwable).response().errorBody();
//                try {//should display the correct error message form the http protocol
//                    if (responseBody != null) {
//                        JSONObject jObjError = new JSONObject(responseBody.toString());
//                        return jObjError.toString();
//                    }
//                } catch (JSONException e1) {
//                    e1.printStackTrace();
//                }
            }
        }
        //todo find out if this is the right way of handling this condition
//        else {
//            return throwable.getMessage();
//        }
        return "";
    }
}
