package com.xtel.vparking.callback;

import android.os.AsyncTask;
import android.util.Log;

import com.xtel.vparking.commons.Constants;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lê Công Long Vũ on 12/2/2016.
 */

public class RequestServer {

    public RequestServer() {
    }

    public void postApi(String url, String jsonObject, String session, ResponseHandle responseHandle) {
        new PostToServer(responseHandle).execute(url, jsonObject, session);
    }

    public void getApi(String url, String session, ResponseHandle responseHandle) {
        new GetToServer(responseHandle).execute(url, session);
    }

    private class PostToServer extends AsyncTask<String, Integer, String> {
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private ResponseHandle responseHandle;

        PostToServer(ResponseHandle responseHandle) {
            this.responseHandle = responseHandle;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();

                Request.Builder builder = new Request.Builder();
                builder.url(params[0]);

                if (params[1] != null) {
                    RequestBody body = RequestBody.create(JSON, params[1]);
                    builder.post(body);
                }

                if (params[2] != null)
                    builder.header(Constants.JSON_SESSION, params[2]);

                Request request = builder.build();
                Log.e(this.getClass().getSimpleName(), "Request: " + request.toString());

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
//                responseHandle.onError(e);
                Log.e(this.getClass().getSimpleName(), "Error: " + e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(this.getClass().getSimpleName(), "Success: " + s);
            responseHandle.onSuccess(s);
        }
    }

    private class GetToServer extends AsyncTask<String, Integer, String> {
        private ResponseHandle responseHandle;

        GetToServer(ResponseHandle responseHandle) {
            this.responseHandle = responseHandle;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();

                Request.Builder builder = new Request.Builder();
                builder.url(params[0]);

                if (params[1] != null)
                    builder.header(Constants.JSON_SESSION, params[1]);

                Request request = builder.build();
                Log.e(this.getClass().getSimpleName(), "Request: " + request.toString());

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
//                responseHandle.onError(e);
                Log.e(this.getClass().getSimpleName(), "Error: " + e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(this.getClass().getSimpleName(), "Success: " + s);
            responseHandle.onSuccess(s);
        }
    }
}
