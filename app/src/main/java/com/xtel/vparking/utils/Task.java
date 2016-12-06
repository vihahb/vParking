package com.xtel.vparking.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.callback.RequestWithStringListener;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.view.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import gun0912.tedbottompicker.TedBottomPicker;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lê Công Long Vũ on 12/1/2016.
 */

public class Task {
    public static void TakeBigPicture(final Context context, FragmentManager fragmentManager, final boolean isBigImage, final RequestWithStringListener requestWithStringListener) {
        TedBottomPicker bottomSheetDialogFragment = new TedBottomPicker.Builder(context)
                .setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                    @Override
                    public void onImageSelected(final Uri uri) {
                        Log.e("tb_uri", "uri: " + uri);
                        Log.e("tb_path", "uri.getPath(): " + uri.getPath());

                        File file = new File(uri.getPath());
                        Log.e("tb_path_file", "path: " + file.getPath());

                        final Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new ConvertImage(context, isBigImage, requestWithStringListener).execute(bitmap);
                            }
                        }, 500);
                    }
                })
                .setPeekHeight(context.getResources().getDisplayMetrics().heightPixels / 2)
                .create();

        bottomSheetDialogFragment.show(fragmentManager);
    }

    public static class ConvertImage extends AsyncTask<Bitmap, Void, File> {
        //        private DialogProgressBar dialogProgressBar;
        private Context context;
        private boolean isBigImage;
        private RequestWithStringListener requestWithStringListener;

        public ConvertImage(Context context, boolean isBigImage, RequestWithStringListener requestWithStringListener) {
            this.context = context;
            this.isBigImage = isBigImage;
            this.requestWithStringListener = requestWithStringListener;
        }

        @Override
        public File doInBackground(Bitmap... params) {
            try {
                Log.e("tb_uri", "null k " + params[0].getWidth());

                try {
                    Bitmap newBitmap;

                    if (isBigImage)
                        newBitmap = getBitmapBigSize(params[0]);
                    else
                        newBitmap = getBitmapSmallSize(params[0]);

                    if (newBitmap != null) {
                        return saveImageFile(newBitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("tb_image_error", e.toString());
            }
            return null;
        }

        @Override
        public void onPostExecute(File file) {
            super.onPostExecute(file);

            if (file != null) {
                postImageToServer(file, context, requestWithStringListener);
            } else {
//                dialogProgressBar.hideProgressBar();
                requestWithStringListener.onError();
            }
        }

        private Bitmap getBitmapSmallSize(Bitmap bitmap) {
            try {
                double width = bitmap.getWidth(), height = bitmap.getHeight();
                Log.e("tb_bitmap_old", width + "        " + height);

                if (width > 300 || height > 300) {
                    int new_width, new_height;
                    while (width > 300 || height > 300) {
                        width = width * 0.8;
                        height = height * 0.8;
                        Log.e("tb_bitmap_run", width + "       " + height);
                    }
                    new_width = (int) width;
                    new_height = (int) height;

                    Log.e("tb_bitmap_new", new_width + "         " + new_height);
                    return Bitmap.createScaledBitmap(bitmap, new_width, new_height, false);
                }

                return bitmap;
            } catch (Exception e) {
                Log.e("tb_error_image", e.toString());
                e.printStackTrace();
            }
            return null;
        }

        private Bitmap getBitmapBigSize(Bitmap bitmap) {
            try {
                double width = bitmap.getWidth(), height = bitmap.getHeight();
                Log.e("tb_bitmap_old", width + "        " + height);

                if (width > 1280 || height > 1280) {
                    int new_width, new_height;
                    while (width > 1280 || height > 1280) {
                        width = width * 0.8;
                        height = height * 0.8;
                        Log.e("tb_bitmap_run", width + "       " + height);
                    }
                    new_width = (int) width;
                    new_height = (int) height;

                    Log.e("tb_bitmap_new", new_width + "         " + new_height);
                    return Bitmap.createScaledBitmap(bitmap, new_width, new_height, false);
                }

                return bitmap;
            } catch (Exception e) {
                Log.e("tb_error_image", e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        private File saveImageFile(Bitmap bitmap) {
            try {
                String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/vParking";
                File dir = new File(file_path);

                if (!dir.exists())
                    dir.mkdirs();

                File file = new File(dir, System.currentTimeMillis() + ".png");
                FileOutputStream fOut = new FileOutputStream(file);

                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                }

                fOut.flush();
                fOut.close();

                return file;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static void postImageToServer(final File file, final Context context, final RequestWithStringListener requestWithStringListener) {
        Log.e("tb_up_upload", "dang up: " + file.getPath());
        Ion.with(context)
                .load(Constants.SERVER_UPLOAD)
                .setMultipartFile("fileUpload", file)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Log.e("tb_up_error", e.toString());
//                            dialogProgressBar.hideProgressBar();
                            Toast.makeText(context, context.getString(R.string.error_server_request), Toast.LENGTH_SHORT).show();
                        } else {
//                            dialogProgressBar.hideProgressBar();
                            Log.e("tb_up_result", result);
                            Error error = JsonParse.checkError(result);

                            if (error != null) {
                                JsonParse.getCodeError(context, null, error.getCode(), context.getString(R.string.loi_addparking));
                                requestWithStringListener.onError();
                            } else {
                                requestWithStringListener.onSuccess(result);
                            }

                            try {
                                boolean delete = file.delete();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
}