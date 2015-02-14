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
import com.pronofoot.teamazerty.app.model.Classement;
import com.pronofoot.teamazerty.app.model.Resultat;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class ResultatGrilleListFragment extends ItemListFragment<Resultat> {

    @Inject protected BootstrapServiceProvider serviceProvider;
    @Inject protected LogoutService logoutService;

    protected View header;
    protected Classement response;
    protected String nomGrille;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(R.string.no_classement);

    }

    @Override
    protected void configureList(Activity activity, ListView listView) {
        super.configureList(activity, listView);

        listView.setFastScrollEnabled(true);
        listView.setDividerHeight(0);

        header = activity.getLayoutInflater()
                .inflate(R.layout.resultat_header, null);
        getListAdapter()
                .addHeader(header);
    }

    @Override
    public LogoutService getLogoutService() {
        return logoutService;
    }

    @Override
    public void onDestroyView() {
        setListAdapter(null);

        super.onDestroyView();
    }

    @Override
    public Loader<List<Resultat>> onCreateLoader(int id, Bundle args) {
        final List<Resultat> initialItems = items;
        return new ThrowableLoader<List<Resultat>>(getActivity(), items) {

            @Override
            public void deliverResult(List<Resultat> data) {
                super.deliverResult(data);
                if (data != null && data.size() > 0) {
                    ((TextView)header.findViewById(R.id.resultat_nom_grille)).setText(nomGrille);
                } else {
                    ((TextView)header.findViewById(R.id.resultat_nom_grille)).setText("");
                }
            }

            @Override
            public List<Resultat> loadData() throws Exception {
                try {
                    if(getActivity() != null) {
                        response = serviceProvider.getService(getActivity()).getResultatGrille();
                        nomGrille = response.getNom();
                        //header.get
                        if (response != null && response.resultats != null) {
                            return response.resultats;
                        }
                        return Collections.emptyList();
                    } else {
                        return Collections.emptyList();
                    }

                /*} catch (OperationCanceledException e) {
                    Activity activity = getActivity();
                    if (activity != null)
                        activity.finish();
                    return initialItems;
                }*/
                } catch (Exception e) {
                    e.printStackTrace();
                    return initialItems;
                }
            }
        };
    }

    @Override
    protected SingleTypeAdapter<Resultat> createAdapter(List<Resultat> items) {
        return new ResultatGrilleListAdapter(getActivity().getLayoutInflater(), items);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
       int id_user = ((Resultat) l.getItemAtPosition(position)).getPronostiqueur().getId();
        startActivity(new Intent(getActivity(), StatUserActivity.class).putExtra(Constants.Extra.USER, id_user));
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_resultat;
    }
}
