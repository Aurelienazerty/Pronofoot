package com.pronofoot.teamazerty.app.ui;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.pronofoot.teamazerty.app.Injector;

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
