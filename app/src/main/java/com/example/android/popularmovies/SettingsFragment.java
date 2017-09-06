package com.example.android.popularmovies;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.example.android.popularmovies.data.PopularMoviesPreferences;

/**
 * Created by lsitec207.neto on 04/09/17.
 */

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private void setPreferenceSummary(Preference pref, String value) {
        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            int listPrefIndex = listPref.findIndexOfValue(value);
            if (listPrefIndex>=0) {
                pref.setSummary(listPref.getEntries()[listPrefIndex]);
            }
        }
        else {
            pref.setSummary(value);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_screen);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();

        int prefCount = preferenceScreen.getPreferenceCount();

        for (int x=0; x<prefCount;x++) {
            Preference pref = preferenceScreen.getPreference(x);
            if (!(pref instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(pref.getKey(),"");
                setPreferenceSummary(pref,value);
            }
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
            /*if (key.equals(getString(R.string.sort_by_key))) {
                //TODO check if there is data based on the type of preference selected.
                //TODO if it does, load this data else fetch it.
                String valueSelected = PopularMoviesPreferences.getSortByPreferenceValue(getContext());
                if (valueSelected.equals(getString(R.string.pref_top_rated)) {
                }
            }*/
    }

    @Override
    public void onStart() {
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }
}
