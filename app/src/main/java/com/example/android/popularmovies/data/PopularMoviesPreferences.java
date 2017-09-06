package com.example.android.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.android.popularmovies.R;

/**
 * Created by lsitec207.neto on 06/09/17.
 */

public class PopularMoviesPreferences {

    public static String getSortByPreferenceValue(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keySortBy = context.getString(R.string.sort_by_key);
        String defaultSort = context.getString(R.string.pref_top_rated);
        return sharedPreferences.getString(keySortBy,defaultSort);
    }

}
