package com.xtel.vparking.view.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.xtel.vparking.R;

public class VerhicleActivity extends BasicActivity {
    public static final int RESULT_ADD_VERHICLE = 66, REQUEST_ADD_VERHICLE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verhicle);

        initToolbar(R.id.verhicle_toolbar, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.verhicle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_add_verhicle) {
            startActivityForResult(AddVerhicleActivity.class, REQUEST_ADD_VERHICLE);
        } else if (id == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
