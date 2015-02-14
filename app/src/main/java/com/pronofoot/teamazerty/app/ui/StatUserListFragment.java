package com.pronofoot.teamazerty.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.pronofoot.teamazerty.app.BootstrapServiceProvider;
import com.pronofoot.teamazerty.app.Injector;
import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.authenticator.LogoutService;
import com.pronofoot.teamazerty.app.core.Constants;
import com.pronofoot.teamazerty.app.model.Stat;
import com.pronofoot.teamazerty.app.model.StatUser;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class StatUserListFragment extends ItemListFragment<Stat> {

    @Inject protected BootstrapServiceProvider serviceProvider;
    @Inject protected LogoutService logoutService;

    protected View header, footer;
    protected StatUser response;
    protected String username;
    protected String saison;
    protected int user_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        try {
            Injector.inject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(R.string.no_stat);
    }

    @Override
    public void onDestroyView() {
        setListAdapter(null);
        super.onDestroyView();
    }

    @Override
    protected void configureList(Activity activity, ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);

        header = activity.getLayoutInflater()
                .inflate(R.layout.statuser_header, null);
        getListAdapter()
                .addHeader(header);

        footer = getActivity().getLayoutInflater()
                .inflate(R.layout.statuser_footer, null);
        getListAdapter()
                .addFooter(footer);
    }

    @Override
    public LogoutService getLogoutService() {
        return logoutService;
    }

    @Override
    public Loader<List<Stat>> onCreateLoader(int id, Bundle args) {
        final List<Stat> initialItems = items;
        return new ThrowableLoader<List<Stat>>(getActivity(), items) {

            @Override
            public void deliverResult(List<Stat> data) {
                super.deliverResult(data);
                ((TextView)header.findViewById(R.id.stat_user)).setText(username);
                ((TextView)header.findViewById(R.id.stat_saison)).setText(saison);
            }

            @Override
            public List<Stat> loadData() throws Exception {
                try {
                    Activity a = getActivity();
                    if(a != null) {
                        Intent i = a.getIntent();
                        user_id = i.getIntExtra(Constants.Extra.USER, 5);
                        //Log.i("TA", "serviceProvider : " + serviceProvider);
                        //Log.i("TA", "getService : " + serviceProvider.getService(a));
                        response = serviceProvider.getService(a).getStatUser(user_id);
                        saison = response.getSaison();
                        username = response.getUsername();
                        //header.get
                        if (response != null && response.getStats() != null) {
                            return response.getStats();
                        }
                        return Collections.emptyList();
                    } else {
                        return Collections.emptyList();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return initialItems;
                }
            }
        };
    }

    @Override
    protected SingleTypeAdapter<Stat> createAdapter(List<Stat> items) {
        return new StatUserListAdapter(getActivity().getLayoutInflater(), items);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        int compet_id = ((Stat) l.getItemAtPosition(position)).getCompet_id();
        startActivity(new Intent(getActivity(), ResultatUserActivity.class)
            .putExtra(Constants.Indent.COMPET_ID, compet_id)
            .putExtra(Constants.Indent.USER_ID, user_id)
            .putExtra(Constants.Indent.USERNAME, username)
            .putExtra(Constants.Indent.GRILLE_RESULTAT, -1)
            .putExtra(Constants.Indent.LAST_GRILLE_RESULTAT, -1)
            .putExtra(Constants.Indent.PAGE, EspacepronoFragmentActivity.PAGE_RESULTAT)
            .putExtra(Constants.Indent.LUNCHED, true)
        );
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_stats;
    }
}
