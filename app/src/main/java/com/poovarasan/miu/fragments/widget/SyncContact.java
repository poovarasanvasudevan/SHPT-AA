package com.poovarasan.miu.fragments.widget;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.FragmentSyncContactBinding;

/**
 * Created by poovarasanv on 25/10/16.
 */

public class SyncContact extends Fragment {

    FragmentSyncContactBinding fragmentSyncContactBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentSyncContactBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sync_contact, container, false);
        return fragmentSyncContactBinding.getRoot();
    }
}
