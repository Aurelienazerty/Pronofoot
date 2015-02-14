package com.pronofoot.teamazerty.app.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.pronofoot.teamazerty.app.BootstrapServiceProvider;
import com.pronofoot.teamazerty.app.Injector;
import com.pronofoot.teamazerty.app.R;
import com.pronofoot.teamazerty.app.authenticator.LogoutService;
import com.pronofoot.teamazerty.app.core.Constants;
import com.pronofoot.teamazerty.app.model.Resultat;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class ResultatClassementListFragment extends ItemListFragment<Resultat> {

    @Inject protected BootstrapServiceProvider serviceProvider;
    @Inject protected LogoutService logoutService;

    protected String type;

    public ResultatClassementListFragment() {
        super();
        this.type = Constants.Types.CLASSEMENT_POINT;
    }

    @SuppressLint("ValidFragment")
    public ResultatClassementListFragment(String type) {
        super();
        this.type = type;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }

    @Override
    public LogoutService getLogoutService() {
        return logoutService;
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
    }

    @Override
    public void onDestroyView() {
        setListAdapter(null);

        super.onDestroyView();
    }

    @Override
    public Loader<List<Resultat>> onCreateLoader(final int id, final Bundle args) {
        final List<Resultat> initialItems = items;
        //Log.i("TA", "onCreateLoader");
        ThrowableLoader<List<Resultat>> res;
        try {

            res = new ThrowableLoader<List<Resultat>>(getActivity(), items) {

                @Override
                public List<Resultat> loadData() throws Exception {
                    try {
                        if (getActivity() != null) {
                            if (type.equalsIgnoreCase(Constants.Types.CLASSEMENT_POINT)) {
                                return serviceProvider.getService(getActivity()).getClassementPoint();
                            } else {
                                return serviceProvider.getService(getActivity()).getClassementPourcent();
                            }
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

        } catch (Exception e) {
            e.printStackTrace();
            res = null;
        }

        return res;
    }

    @Override
    protected SingleTypeAdapter<Resultat> createAdapter(List<Resultat> items) {
        return new ResultatClassementListAdapter(getActivity().getLayoutInflater(), items);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        int id_user = ((Resultat) l.getItemAtPosition(position)).getPronostiqueur().getId();
        startActivity(new Intent(getActivity(), StatUserActivity.class).putExtra(Constants.Extra.USER, id_user));
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        //Log.i("TA", "getErrorMessage : " + exception.getMessage());
        //exception.printStackTrace();
        return R.string.error_loading_classement;
    }
}
