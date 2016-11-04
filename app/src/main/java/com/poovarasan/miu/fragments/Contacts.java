package com.poovarasan.miu.fragments;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.poovarasan.miu.R;
import com.poovarasan.miu.activity.MessageActivity;
import com.poovarasan.miu.adapter.ContactAdapter;
import com.poovarasan.miu.databinding.FragmentContactsBinding;
import com.poovarasan.miu.sync.Sync;

import java.util.ArrayList;
import java.util.List;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static android.content.Context.SEARCH_SERVICE;

/**
 * Created by poovarasanv on 14/10/16.
 */


public class Contacts extends Fragment {
    FragmentContactsBinding fragmentContactsBinding;
    int permisssionDeniedType = 0;
    FastItemAdapter<ContactAdapter> fastAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentContactsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts, container, false);


        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        fragmentContactsBinding.allcontacts.setLayoutManager(llm);
        Nammu.init(getActivity());

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            load();
        } else {
            Nammu.askForPermission(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, new PermissionCallback() {
                @Override
                public void permissionGranted() {

                    AsyncTaskCompat.executeParallel(new RefreshUser(), null);
                }

                @Override
                public void permissionRefused() {
                    fragmentContactsBinding.contactPermission.setVisibility(View.VISIBLE);
                }
            });
        }

        fragmentContactsBinding.givePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Nammu.askForPermission(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, new PermissionCallback() {
                    @Override
                    public void permissionGranted() {

                        AsyncTaskCompat.executeParallel(new RefreshUser(), null);
                    }

                    @Override
                    public void permissionRefused() {
                        fragmentContactsBinding.contactPermission.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        fragmentContactsBinding.contactRefresh.setColorSchemeResources(
                R.color.md_red_500,
                R.color.md_green_500,
                R.color.md_yellow_500,
                R.color.md_teal_500,
                R.color.md_orange_500,
                R.color.md_pink_500
        );

        fragmentContactsBinding.contactRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTaskCompat.executeParallel(new RefreshUser(), null);
            }
        });


        return fragmentContactsBinding.getRoot();
    }


    public void load() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("MyUsers");
        query.fromLocalDatastore();
        query.addAscendingOrder("NAME");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                fastAdapter = new FastItemAdapter<>();
                fragmentContactsBinding.allcontacts.setAdapter(fastAdapter);

                List<ContactAdapter> contactAdapters = new ArrayList<>();
                for (ParseObject parseObject : objects) {
                    Log.i("Loaded123", parseObject.getString("NUMBER"));

                    contactAdapters.add(new ContactAdapter(
                            parseObject.getString("IMAGE"),
                            parseObject.getString("NAME"),
                            parseObject.getString("STATUS"),
                            getActivity().getApplicationContext()
                    ));
                }
                fastAdapter.add(contactAdapters);
                fastAdapter.notifyAdapterDataSetChanged();
                fastAdapter.withOnClickListener(new FastAdapter.OnClickListener<ContactAdapter>() {
                    @Override
                    public boolean onClick(View v, IAdapter<ContactAdapter> adapter, ContactAdapter item, int position) {
                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                        intent.putExtra("contactDetails", item);
                        ActivityCompat.startActivity(getActivity(), intent, null);
                        return false;
                    }
                });
            }
        });

    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuInflater inflater = getActivity().getMenuInflater();
        menu.clear();

        inflater.inflate(R.menu.contact_menu, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fastAdapter.filter(newText);

                fastAdapter.withFilterPredicate(new IItemAdapter.Predicate<ContactAdapter>() {
                    @Override
                    public boolean filter(ContactAdapter item, CharSequence constraint) {
                        return !item.getName().toLowerCase().contains(String.valueOf(constraint).toLowerCase().trim());
                    }
                });
                return false;
            }
        });

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh: {

                AsyncTaskCompat.executeParallel(new RefreshUser(), null);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    class RefreshUser extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Sync sync = new Sync(getActivity().getApplicationContext());
            sync.makeSync();
            load();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            fragmentContactsBinding.contactRefresh.setRefreshing(false);
            super.onPostExecute(aVoid);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
