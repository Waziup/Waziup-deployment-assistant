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

package eu.waziup.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.net.SocketTimeoutException;

import eu.waziup.app.DaApp;
import eu.waziup.app.R;
import eu.waziup.app.data.network.model.ApiError;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.HttpException;

import static eu.waziup.app.utils.AppConstants.EXTRA_CLIENT_SECRET;

/**
 * Created by KidusMT.
 */

public final class CommonUtils {

    private static final String TAG = "CommonUtils";

    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        if (context != null) {
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.show();
            if (progressDialog.getWindow() != null) {
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setIndeterminate(true);
//        progressDialog.setCancelable(false);
//        progressDialog.setCanceledOnTouchOutside(false);
            return progressDialog;
        } else {
            return null;
        }
    }

    public static String getClientSecretFromIntent(Intent intent) {
        if (!intent.hasExtra(EXTRA_CLIENT_SECRET)) {
            return null;
        }
        return intent.getStringExtra(EXTRA_CLIENT_SECRET);
    }

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static void toast(String msg) {
        Toast.makeText(DaApp.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        } else if (throwable instanceof com.jakewharton.retrofit2.adapter.rxjava2.HttpException) {
            int code = ((com.jakewharton.retrofit2.adapter.rxjava2.HttpException) throwable).response().code();
            if (code >= 400 && code < 404) { //4xx Client Errors
                return DaApp.getContext().getString(R.string.error_invalid_credential);
            } else if (code == 404) { //4xx Client Errors
                return DaApp.getContext().getString(R.string.error_file_does_not_exist);
            } else if (code == 500) {
                return DaApp.getContext().getString(R.string.error_server_error);
            } else if (code == 503) {
                return DaApp.getContext().getString(R.string.error_server_unreachable);
            } else {

                return DaApp.getContext().getString(R.string.error_something_wrong_happend);
            }
        }
        return "";
    }
}
