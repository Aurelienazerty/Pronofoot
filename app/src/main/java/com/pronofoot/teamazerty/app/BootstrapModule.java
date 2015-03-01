package com.pronofoot.teamazerty.app;

import android.accounts.AccountManager;
import android.content.Context;

import com.pronofoot.teamazerty.app.authenticator.ApiKeyProvider;
import com.pronofoot.teamazerty.app.authenticator.BootstrapAuthenticatorActivity;
import com.pronofoot.teamazerty.app.authenticator.LogoutService;
import com.pronofoot.teamazerty.app.ui.AdFragment;
import com.pronofoot.teamazerty.app.ui.CarouselActivity;
import com.pronofoot.teamazerty.app.ui.EspacepronoFragmentActivity;
import com.pronofoot.teamazerty.app.ui.EspacepronoPronoListFragment;
import com.pronofoot.teamazerty.app.ui.EspacepronoResultatListFragment;
import com.pronofoot.teamazerty.app.ui.ProfilActivity;
import com.pronofoot.teamazerty.app.ui.RegisterActivity;
import com.pronofoot.teamazerty.app.ui.ResultatClassementListFragment;
import com.pronofoot.teamazerty.app.ui.ResultatGrilleListFragment;
import com.pronofoot.teamazerty.app.ui.ResultatUserActivity;
import com.pronofoot.teamazerty.app.ui.StatUserActivity;
import com.pronofoot.teamazerty.app.ui.StatUserListAdapter;
import com.pronofoot.teamazerty.app.ui.StatUserListFragment;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module
(
        complete = false,

        injects = {
                BootstrapApplication.class,
                BootstrapAuthenticatorActivity.class,
                CarouselActivity.class,
                ResultatClassementListFragment.class,
                ResultatGrilleListFragment.class,
                EspacepronoFragmentActivity.class,
                EspacepronoPronoListFragment.class,
                EspacepronoResultatListFragment.class,
                ProfilActivity.class,
                RegisterActivity.class,
                StatUserActivity.class,
                StatUserListFragment.class,
                StatUserListAdapter.class,
                ResultatUserActivity.class,
                AdFragment.class
        }

)
public class BootstrapModule  {

    @Singleton
    Bus provideOttoBus() {
        return new Bus();
    }

    @Provides
    @Singleton
    LogoutService provideLogoutService(final Context context, final AccountManager accountManager) {
        return new LogoutService(context, accountManager);
    }

}
