package com.poovarasan.miu.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.poovarasan.miu.R;
import com.poovarasan.miu.activity.MessageActivity;
import com.poovarasan.miu.adapter.ContactAdapter;
import com.poovarasan.miu.application.App;
import com.poovarasan.miu.databinding.FragmentMessageBinding;
import com.poovarasan.miu.model.MessageModelEntityManager;
import com.poovarasan.miu.model.RecentMessages;
import com.poovarasan.miu.model.RecentMessagesColumns;
import com.poovarasan.miu.model.RecentMessagesEntityManager;
import com.poovarasan.miu.model.UserModel;
import com.poovarasan.miu.model.UserModelEntityManager;

import java.util.List;

/**
 * Created by poovarasanv on 14/10/16.
 */

public class Message extends Fragment {

    MessageModelEntityManager messageModelEntityManager;
    RecentMessagesEntityManager recentMessagesEntityManager;
    FastItemAdapter<ContactAdapter> fastAdapter;
    UserModelEntityManager userModelEntityManager;
    FragmentMessageBinding fragmentMessageBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentMessageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);

        messageModelEntityManager = new MessageModelEntityManager();
        recentMessagesEntityManager = new RecentMessagesEntityManager();
        userModelEntityManager = new UserModelEntityManager();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        fragmentMessageBinding.allMessageUser.setLayoutManager(llm);


        AsyncTaskCompat.executeParallel(new AllMessageUserLoader(), null);

        fragmentMessageBinding.messageRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fastAdapter.clear();
                AsyncTaskCompat.executeParallel(new AllMessageUserLoader(), null);
            }
        });

        return fragmentMessageBinding.getRoot();


    }

    class AllMessageUserLoader extends AsyncTask<Void, Void, List<RecentMessages>> {
        @Override
        protected List<RecentMessages> doInBackground(Void... voids) {
            List<RecentMessages> recentMessages = recentMessagesEntityManager
                    .select()
                    .sortDesc(RecentMessagesColumns.updatedTime)
                    .asList();


            Log.d("Messaged", recentMessages.size() + "--");
            return recentMessages;
        }

        @Override
        protected void onPostExecute(List<RecentMessages> aVoid) {
            super.onPostExecute(aVoid);

            fastAdapter = new FastItemAdapter<>();
            fragmentMessageBinding.allMessageUser.setAdapter(fastAdapter);
            for (final RecentMessages recentMessages1 : aVoid) {
                UserModel userModel = userModelEntityManager.select().number().equalsTo(recentMessages1.getNumbers()).first();
                if (userModel != null) {
                    fastAdapter.add(new ContactAdapter(
                            userModel.getImage(),
                            userModel.getName(),
                            recentMessages1.getRecentMessage(),
                            userModel.getNumber(),
                            getActivity().getApplicationContext()
                    ));
                } else {
                    if (App.isOnline(getActivity().getApplicationContext())) {
                        ParseQuery.getQuery(ParseUser.class)
                                .whereContains("username", recentMessages1.getNumbers())
                                .getFirstInBackground(new GetCallback<ParseUser>() {
                                    @Override
                                    public void done(ParseUser object, ParseException e) {
                                        if (e == null) {
                                            String s = App.saveImage(recentMessages1.getNumbers(), object.getBytes("image"), getActivity().getApplicationContext());
                                            fastAdapter.add(new ContactAdapter(
                                                    s,
                                                    recentMessages1.getNumbers(),
                                                    recentMessages1.getRecentMessage(),
                                                    recentMessages1.getNumbers(),
                                                    getActivity().getApplicationContext()
                                            ));
                                        }
                                    }
                                });


                    } else {
                        fastAdapter.add(new ContactAdapter(
                                App.getDefaultImagePath(getActivity().getApplicationContext()),
                                recentMessages1.getNumbers(),
                                recentMessages1.getRecentMessage(),
                                recentMessages1.getNumbers(),
                                getActivity().getApplicationContext()
                        ));
                    }
                }
            }
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

            fragmentMessageBinding.messageRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuInflater inflater = getActivity().getMenuInflater();
        menu.clear();
        inflater.inflate(R.menu.message_menu, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        AsyncTaskCompat.executeParallel(new AllMessageUserLoader(), null);
    }
}
