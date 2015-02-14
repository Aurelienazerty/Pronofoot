

package com.pronofoot.teamazerty.app.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.core.Constants;

/**
 * Pager adapter
 */
public class ResultatPagerAdapter extends FragmentPagerAdapter {

    private final Resources resources;

    /**
     * Create pager adapter
     *
     * @param resources
     * @param fragmentManager
     */
    public ResultatPagerAdapter(Resources resources, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.resources = resources;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
        case 0:
            ResultatClassementListFragment classementFragment1 = new ResultatClassementListFragment(Constants.Types.CLASSEMENT_POURCENT);
            classementFragment1.setArguments(bundle);
            return classementFragment1;
        case 1:
            ResultatClassementListFragment classementFragment2 = new ResultatClassementListFragment(Constants.Types.CLASSEMENT_POINT);
            classementFragment2.setArguments(bundle);
            return classementFragment2;
        case 2:
            ResultatGrilleListFragment ResGrilleFragment = new ResultatGrilleListFragment();
            ResGrilleFragment.setArguments(bundle);
            return ResGrilleFragment;
        default:
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
        case 0:
            return resources.getString(R.string.page_classement_pourcent);
        case 1:
            return resources.getString(R.string.page_classement_point);
        case 2:
            return resources.getString(R.string.page_derniere_grille);
        default:
            return null;
        }
    }
}
