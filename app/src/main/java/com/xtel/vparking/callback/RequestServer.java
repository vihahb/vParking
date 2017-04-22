package com.xtel.vparking.callback;

import android.os.AsyncTask;

import com.xtel.vparking.commons.Constants;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lê Công Long Vũ on 12/2/2016
 */

public class RequestServer {

    protected final String DEVICE_TYPE = "device-type";
    protected final String VERSION_NUMBER = "version-number";

    protected final String DV_TYPE = "1";
    protected final String VR_NUMBER = "1";

    public RequestServer() {
    }

    public void postApi(String url, String jsonObject, String session, ResponseHandle responseHandle) {
        new PostToServer(responseHandle).execute(url, jsonObject, session);
    }

    public void getApi(String url, String session, ResponseHandle responseHandle) {
        new GetToServer(responseHandle).execute(url, session);
    }

    public void putApi(String url, String jsonObject, String session, ResponseHandle responseHandle) {
        new PutToServer(responseHandle).execute(url, jsonObject, session);
    }

    public void deleteApi(String url, String delete, String session, ResponseHandle responseHandle) {
        new DeleteToServer(responseHandle).execute(url, delete, session);
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

                builder.header(DEVICE_TYPE, DV_TYPE);
                builder.header(VERSION_NUMBER, VR_NUMBER);

                Request request = builder.build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
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

                builder.header(DEVICE_TYPE, DV_TYPE);
                builder.header(VERSION_NUMBER, VR_NUMBER);

                Request request = builder.build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            responseHandle.onSuccess(s);
        }
    }

    private class PutToServer extends AsyncTask<String, Integer, String> {
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private ResponseHandle responseHandle;

        PutToServer(ResponseHandle responseHandle) {
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
                    builder.put(body);
                }

                if (params[2] != null)
                    builder.header(Constants.JSON_SESSION, params[2]);

                builder.header(DEVICE_TYPE, DV_TYPE);
                builder.header(VERSION_NUMBER, VR_NUMBER);

                Request request = builder.build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            responseHandle.onSuccess(s);
        }
    }

    private class DeleteToServer extends AsyncTask<String, Integer, String> {
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private ResponseHandle responseHandle;

        DeleteToServer(ResponseHandle responseHandle) {
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
                    builder.delete(body);
                }

                if (params[2] != null)
                    builder.header(Constants.JSON_SESSION, params[2]);

                builder.header(DEVICE_TYPE, DV_TYPE);
                builder.header(VERSION_NUMBER, VR_NUMBER);

                Request request = builder.build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            responseHandle.onSuccess(s);
        }
    }
}
