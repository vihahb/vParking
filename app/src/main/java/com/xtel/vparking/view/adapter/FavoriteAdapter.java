package com.xtel.vparking.view.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.xtel.vparking.R;
import com.xtel.vparking.callback.RequestNoResultListener;
import com.xtel.vparking.commons.Constants;
import com.xtel.vparking.commons.GetNewSession;
import com.xtel.vparking.model.entity.Error;
import com.xtel.vparking.model.entity.Favotire;
import com.xtel.vparking.utils.JsonHelper;
import com.xtel.vparking.utils.JsonParse;
import com.xtel.vparking.utils.SharedPreferencesUtils;
import com.xtel.vparking.view.activity.inf.FavoriteView;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lê Công Long Vũ on 12/5/2016.
 */

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Favotire> arrayList;
    private FavoriteView view;
    private ProgressDialog progressDialog;

    public FavoriteAdapter(Context context, ArrayList<Favotire> arrayList, FavoriteView view) {
        this.context = context;
        this.arrayList = arrayList;
        this.view = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Favotire favotire = arrayList.get(position);

        holder.txt_name.setText(favotire.getParking_name());
        holder.txt_address.setText(favotire.getAddress());
        holder.txt_money.setText((favotire.getPrice() + " K"));
        holder.txt_time.setText(Constants.getTime(favotire.getBegin(), favotire.getEnd()));

        holder.img_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemoveFromFavorite().execute(favotire.getId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageButton img_favorite;
        private TextView txt_name, txt_time, txt_address, txt_money;

        ViewHolder(View itemView) {
            super(itemView);

            img_favorite = (ImageButton) itemView.findViewById(R.id.item_img_favorite_favorite);
            txt_name = (TextView) itemView.findViewById(R.id.item_txt_favorite_name);
            txt_time = (TextView) itemView.findViewById(R.id.item_txt_favorite_time);
            txt_address = (TextView) itemView.findViewById(R.id.item_txt_favorite_address);
            txt_money = (TextView) itemView.findViewById(R.id.item_txt_favorite_money);
        }
    }

    private class RemoveFromFavorite extends AsyncTask<Integer, Void, String> {
        private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private int position, id;

        @Override
        protected void onPreExecute() {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(context.getString(R.string.doing));
            }
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
        }

        @Override
        protected String doInBackground(Integer... params) {
            this.id = params[0];
            this.position = params[1];
            try {
                OkHttpClient client = new OkHttpClient();

                String session = SharedPreferencesUtils.getInstance().getStringValue(Constants.USER_SESSION);
                String url = Constants.SERVER_PARKING + Constants.PARKING_FAVORITE;

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(Constants.JSON_PARKING_ID, params[0]);

                Log.e("pk_fa_url", url);
                Log.e("pk_fa_session", session);
                Log.e("pk_fa_json", jsonObject.toString());

                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .header(Constants.JSON_SESSION, session)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.e("pk_fa_loi_request", e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
            Log.e("pk_fa_result", "null k: " + s);

            if (s == null || s.isEmpty()) {
                arrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());

            } else {
                Error error = JsonHelper.getObjectNoException(s, Error.class);

                if (error != null)
                    if (error.getCode() == 2)
                        getNewSessionAddToFavorite(id);
                    else {
                        progressDialog.dismiss();
                        JsonParse.getCodeError(context, null, error.getCode(), "Không thể thêm vào danh sách yêu thích");
                    }
                else {
                    progressDialog.dismiss();

                    arrayList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, getItemCount());
                }
            }

            if (arrayList.size() == 0)
                view.onGetParkingFavoriteSuccess(arrayList);
        }
    }

    private void getNewSessionAddToFavorite(final int id) {
        GetNewSession.getNewSession(context, new RequestNoResultListener() {
            @Override
            public void onSuccess() {
                new RemoveFromFavorite().execute(id);
            }

            @Override
            public void onError() {
                progressDialog.dismiss();
                Toast.makeText(context, "Không thể thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
    }
}