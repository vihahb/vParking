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
 * Created by Mr. M.2 on 12/2/2016.
 */

public class RequestServer {

    public RequestServer() {
    }

    public void postApi(String url, String jsonObject, String session, RequestListener requestListener) {
        new PostToServer(requestListener).execute(url, jsonObject, session);
    }

    public void getApi(String url, String session, RequestListener requestListener) {
        new GetToServer(requestListener).execute(url, session);
    }

    private class PostToServer extends AsyncTask<String, Integer, String> {
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private RequestListener requestListener;

        PostToServer(RequestListener requestListener) {
            this.requestListener = requestListener;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();

                Request.Builder builder = new Request.Builder();
                builder.url(params[0]);

                if (params[1] != null && !params[1].isEmpty()) {
                    RequestBody body = RequestBody.create(JSON, params[1]);
                    builder.post(body);
                }

                if (params[2] != null && !params[2].isEmpty())
                    builder.header(Constants.JSON_SESSION, params[2]);

                Request request = builder.build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                requestListener.onError("Không thể kết nối tới server");
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            requestListener.onSuccess(s);
        }
    }

    private class GetToServer extends AsyncTask<String, Integer, String> {
        private RequestListener requestListener;

        GetToServer(RequestListener requestListener) {
            this.requestListener = requestListener;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkHttpClient client = new OkHttpClient();

                Request.Builder builder = new Request.Builder();
                builder.url(params[0]);

                if (params[1] != null && !params[1].isEmpty())
                    builder.header(Constants.JSON_SESSION, params[1]);

                Request request = builder.build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                requestListener.onError(e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            requestListener.onSuccess(s);
        }
    }
}
