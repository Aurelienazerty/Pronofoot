package com.pronofoot.teamazerty.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;
import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.core.Constants;
import com.viewpagerindicator.TitlePageIndicator;

import butterknife.InjectView;

/**
 * Activity to view the carousel and view pager indicator with fragments.
 */
public class CarouselActivity extends AbstractPronofootFragmentActivity {

    @InjectView(R.id.tpi_header) TitlePageIndicator indicator;
    @InjectView(R.id.vp_pages) ViewPager pager;

    protected void initScreen() {
        if(userHasAuthenticated) {
            pager.removeAllViews();
            pager.setAdapter(new ResultatPagerAdapter(getResources(), getSupportFragmentManager()));
            indicator.setViewPager(pager);
            pager.setCurrentItem(1);

        }
        setNavListeners();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isLunched = getIntent().getBooleanExtra(Constants.Indent.LUNCHED, false);
        if (!isLunched) {
            SharedPreferences preferences = getSharedPreferences(Constants.Pref.PREF_NAME, 0);
            boolean startOnResult = preferences.getString(Constants.Param.PARAM_HOME_PAGE_IS_RESULT, "n").equalsIgnoreCase("y");
            if (!startOnResult) {
                navigateToEspaceProno();
            }
        }
    }


    protected void setNavListeners() {
        menuDrawer.findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDrawer.toggleMenu();
            }
        });

        menuDrawer.findViewById(R.id.espace_prono).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDrawer.toggleMenu();
                navigateToEspaceProno();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                menuDrawer.toggleMenu();
                return true;
            case R.id.espace_prono:
                navigateToEspaceProno();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void navigateToEspaceProno() {
        final Intent i = new Intent(this, EspacepronoFragmentActivity.class);
        i.putExtra(Constants.Indent.LUNCHED, true);
        startActivity(i);
    }
}
