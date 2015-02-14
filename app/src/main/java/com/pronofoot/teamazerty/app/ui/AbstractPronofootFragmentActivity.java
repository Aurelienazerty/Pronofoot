

package com.pronofoot.teamazerty.app.ui;

import android.accounts.OperationCanceledException;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.actionbarsherlock.view.Window;
import com.pronofoot.teamazerty.app.BootstrapServiceProvider;
import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.core.BootstrapService;
import com.pronofoot.teamazerty.app.util.SafeAsyncTask;
import com.viewpagerindicator.TitlePageIndicator;

import net.simonvt.menudrawer.MenuDrawer;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;


/**
 * Activity to view the carousel and view pager indicator with fragments.
 */
abstract class AbstractPronofootFragmentActivity extends PronofootFragmentActivity {

    @InjectView(R.id.tpi_header) TitlePageIndicator indicator;
    @InjectView(R.id.vp_pages) ViewPager pager;
    //@InjectView(R.id.vp_ads) Fragment adBloc;


    @Inject BootstrapServiceProvider serviceProvider;

    protected boolean userHasAuthenticated = false;

    protected MenuDrawer menuDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        // Set up navigation drawer
        menuDrawer = MenuDrawer.attach(this);
        menuDrawer.setMenuView(R.layout.navigation_drawer);
        menuDrawer.setContentView(R.layout.carousel_view);
        menuDrawer.setSlideDrawable(R.drawable.ic_drawer);
        menuDrawer.setDrawerIndicatorEnabled(true);

        Views.inject(this);

        checkAuth();

    }

    protected void initScreen() {
        if(userHasAuthenticated) {
            pager.setAdapter(new ResultatPagerAdapter(getResources(), getSupportFragmentManager()));

            indicator.setViewPager(pager);
            pager.setCurrentItem(1);

        }

        setNavListeners();
    }

    protected void checkAuth() {
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                    final BootstrapService svc = serviceProvider.getService(AbstractPronofootFragmentActivity.this);
                    return svc != null;

            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
                if(e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                    finish();
                }
            }

            @Override
            protected void onSuccess(Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                userHasAuthenticated = true;
                initScreen();
            }
        }.execute();
    }

    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);

        Views.inject(this);
    }


    protected abstract void setNavListeners();

}
