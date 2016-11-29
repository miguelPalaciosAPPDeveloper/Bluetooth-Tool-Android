package com.ad.miguelpalacios.bluetoothtool.Fragments;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.util.Log;

import com.ad.miguelpalacios.bluetoothtool.Activitys.AppCompatPreferenceActivity;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminConfiguraciones.LlavesJoystick;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminConfiguraciones.Configuraciones;
import com.ad.miguelpalacios.bluetoothtool.Admins.AdminConfiguraciones.LlavesGeneral;
import com.ad.miguelpalacios.bluetoothtool.R;

/**
 * Created by miguelpalacios on 24/04/16.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragmentSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = FragmentSettings.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatPreferenceActivity appCompatPreferenceActivity = (AppCompatPreferenceActivity) getActivity();
        ActionBar actionBar = appCompatPreferenceActivity.getSupportActionBar();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        String settings = getArguments().getString(Configuraciones.CONFIGURACIONES);
        switch (settings){
            case Configuraciones.TERMINAL:
                actionBar.setTitle("TERMINAL");
                addPreferencesFromResource(R.xml.settings_terminal);
                break;
            case Configuraciones.GENERAL:
                actionBar.setTitle("GENERAL");
                addPreferencesFromResource(R.xml.settings_general);
                String dato = sharedPreferences.getString(LlavesGeneral.EDT_DATO_ADICIONAL, "");

                if(!dato.isEmpty()){
                    Preference preference = findPreference(LlavesGeneral.CHECKBOX_DATO_INICIO);
                    preference.setEnabled(true);
                }
                break;
            case Configuraciones.JOYSTICK:
                actionBar.setTitle("JOYSTICK");
                addPreferencesFromResource(R.xml.settings_joystick);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        Log.e(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if((key.equals(LlavesGeneral.CHECKBOX_DATO_INICIO)) || (key.equals(LlavesJoystick.CHECKBOX_MULTIPLES_ENVIOS)));

        else {
            if(key.equals(LlavesGeneral.EDT_DATO_ADICIONAL)){
                String datoAdicional = sharedPreferences.getString(key,"");
                if(!datoAdicional.isEmpty()){
                    Preference preference = findPreference(LlavesGeneral.CHECKBOX_DATO_INICIO);
                    preference.setEnabled(true);
                }else{
                    Preference preference = findPreference(LlavesGeneral.CHECKBOX_DATO_INICIO);
                    preference.setEnabled(false);
                }
            }/*else{
                Preference preference = findPreference(key);
                preference.setSummary(sharedPreferences.getString(key, ""));
            }*/
        }
    }
}
