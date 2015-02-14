

package com.pronofoot.teamazerty.app.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pronofoot.teamazerty.app.R;

/**
 * Pager adapter
 */
public class EspacepronoPagerAdapter extends FragmentPagerAdapter {

    private final Resources resources;
    private EspacepronoPronoListFragment pronoListFragment;
    private EspacepronoResultatListFragment resultatListFragment;

    /**
     * Create pager adapter
     *
     * @param resources
     * @param fragmentManager
     */
    public EspacepronoPagerAdapter(Resources resources, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.resources = resources;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                pronoListFragment = new EspacepronoPronoListFragment();
                pronoListFragment.setArguments(bundle);
                return pronoListFragment;
            case 1:
                resultatListFragment = new EspacepronoResultatListFragment();
                resultatListFragment.setArguments(bundle);
                return resultatListFragment;
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return resources.getString(R.string.page_grille);
            case 1:
                return resources.getString(R.string.page_resultat);
            default:
                return null;
        }
    }
}
