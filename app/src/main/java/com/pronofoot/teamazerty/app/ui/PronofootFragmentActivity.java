package com.pronofoot.teamazerty.app.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.pronofoot.teamazerty.app.Injector;
import com.pronofoot.teamazerty.app.core.Constants;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Views;

/**
 * Base class for all Bootstrap Activities that need fragments.
 */
public class PronofootFragmentActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);

        Views.inject(this);
    }

}
