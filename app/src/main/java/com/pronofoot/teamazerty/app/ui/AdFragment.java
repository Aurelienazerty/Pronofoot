package com.pronofoot.teamazerty.app.ui;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pronofoot.teamazerty.app.R;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pronofoot.teamazerty.app.core.Constants;

/**
 * Created by PBVZ9205 on 01/02/2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AdFragment extends Fragment {
    boolean afficherPub;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences = inflater.getContext().getSharedPreferences(Constants.Pref.PREF_NAME, 0);
        afficherPub = preferences.getBoolean(Constants.Pref.PREF_PUB, true);
        return inflater.inflate(R.layout.fragment_ad, container, false);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        AdView mAdView = (AdView) getView().findViewById(R.id.adView);
        if (afficherPub) {
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.INVISIBLE);
        }
    }
}
