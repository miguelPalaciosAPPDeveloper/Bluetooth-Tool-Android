package com.ad.miguelpalacios.bluetoothtool.Activitys;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ad.miguelpalacios.bluetoothtool.R;
import com.ad.miguelpalacios.bluetoothtool.Fragments.FragmentSettings;

import java.util.List;

/**
 * Created by miguelpalacios on 24/04/16.
 */
public class ActivitySettings extends AppCompatPreferenceActivity {
    private static final String TAG = ActivitySettings.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();

        Toolbar toolBar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        setSupportActionBar(toolBar);

        if(Build.VERSION.SDK_INT < 19)
        {
            root.addView(toolBar, 0);
        }else{
            FrameLayout  frame = (FrameLayout)LayoutInflater.from(this).inflate(R.layout.settings_frame, root, false);
            root.addView(frame, 0);

            root.addView(toolBar, 1);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        super.onBuildHeaders(target);
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        Log.e(TAG, "isValidFragment");
        return FragmentSettings.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){onBackPressed();}
        return super.onOptionsItemSelected(item);
    }
}
