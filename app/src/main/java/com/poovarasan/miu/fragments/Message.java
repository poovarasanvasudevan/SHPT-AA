package com.poovarasan.miu.fragments;

import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.FragmentMessageBinding;
import com.poovarasan.miu.model.MessageModel;
import com.poovarasan.miu.model.MessageModelColumns;
import com.poovarasan.miu.model.MessageModelEntityManager;

import java.util.List;

import fr.xebia.android.freezer.QueryLogger;

/**
 * Created by poovarasanv on 14/10/16.
 */

public class Message extends Fragment {

    MessageModelEntityManager messageModelEntityManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentMessageBinding fragmentMessageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);

        messageModelEntityManager = new MessageModelEntityManager();

        AsyncTaskCompat.executeParallel(new AllMessageUserLoader(), null);
        return fragmentMessageBinding.getRoot();
    }

    class AllMessageUserLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            messageModelEntityManager.logQueries(new QueryLogger() {
                @Override
                public void onQuery(String query, String[] datas) {
                    Log.d("MESSAGELOG", query);
                }
            });
            List<MessageModel> messageModels = messageModelEntityManager.select().fields(MessageModelColumns.fromUser).asList();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        MenuInflater inflater = getActivity().getMenuInflater();
        menu.clear();
        inflater.inflate(R.menu.message_menu, menu);
        super.onPrepareOptionsMenu(menu);
    }
}
