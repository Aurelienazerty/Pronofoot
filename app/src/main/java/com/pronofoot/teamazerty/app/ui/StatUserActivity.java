package com.pronofoot.teamazerty.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.pronofoot.teamazerty.app.BootstrapServiceProvider;
import com.pronofoot.teamazerty.app.Injector;
import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.core.Constants;

import javax.inject.Inject;

import butterknife.Views;

/**
 * Created by PBVZ9205 on 23/05/2014.
 */
public class StatUserActivity extends PronofootFragmentActivity {

    @Inject protected BootstrapServiceProvider serviceProvider;

    private StatUserListFragment stat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statuser_view);
        //stat = new StatUserListFragment();
        //initFragment(stat);
        Injector.inject(this);
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        Views.inject(this);
    }

    protected void initFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_stat, fragment);
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }

    public void goToOnlineStat(View button) {
        final int user_id = getIntent().getIntExtra(Constants.Extra.USER, 5);
        final String url = Constants.Http.BASE_STAT_USER + user_id + Constants.Http.END_STAT_USER;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }


}
