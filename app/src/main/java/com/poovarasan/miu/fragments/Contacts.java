package com.poovarasan.miu.fragments;

import android.Manifest;
import android.app.SearchManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.poovarasan.miu.R;
import com.poovarasan.miu.adapter.ContactAdapter;
import com.poovarasan.miu.databinding.FragmentContactsBinding;
import com.poovarasan.miu.sync.Sync;
import com.poovarasan.miu.util.PermissionUtil;

import org.jokar.permissiondispatcher.annotation.NeedsPermission;
import org.jokar.permissiondispatcher.annotation.OnNeverAskAgain;
import org.jokar.permissiondispatcher.annotation.OnPermissionDenied;
import org.jokar.permissiondispatcher.annotation.RuntimePermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.Context.SEARCH_SERVICE;

/**
 * Created by poovarasanv on 14/10/16.
 */

@RuntimePermissions
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
        ContactsPermissionsDispatcher.showContactsWithCheck(this);

        fragmentContactsBinding.givePermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Permission Types", permisssionDeniedType + "-->");
                Toast.makeText(getContext(), "Permission Needed to Continue", Toast.LENGTH_SHORT).show();
                ContactsPermissionsDispatcher.showContactsWithCheck(Contacts.this);

                PermissionUtil.startInstalledAppDetailsActivity(getActivity());

            }
        });


        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        fragmentContactsBinding.allcontacts.setLayoutManager(llm);

        load();


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

                    TextDrawable drawable2 = TextDrawable.builder()
                            .buildRound(parseObject.getString("NAME").trim().substring(0, 1), generateRandomColor());
                    contactAdapters.add(new ContactAdapter(
                            drawable2,
                            parseObject.getString("NAME"),
                            parseObject.getString("STATUS")
                    ));
                }
                fastAdapter.add(contactAdapters);
                fastAdapter.notifyAdapterDataSetChanged();
            }
        });

    }

    public int generateRandomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);


        red = (red + 255) / 2;
        green = (green + 255) / 2;
        blue = (blue + 255) / 2;

        return Color.rgb(red, green, blue);

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
                        return !item.getName().toLowerCase().contains(String.valueOf(constraint).toLowerCase());
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

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    public void showContacts() {
        fragmentContactsBinding.contactPermission.setVisibility(View.GONE);
    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    public void contactPermissionDenied() {
        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        fragmentContactsBinding.contactPermission.setVisibility(View.VISIBLE);
        permisssionDeniedType = 1;
    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    public void contactPermissionNeverAsked() {
        //PermissionUtil.openAppSettings((Activity) getActivity());
        fragmentContactsBinding.contactPermission.setVisibility(View.VISIBLE);
        permisssionDeniedType = 2;
    }
}
