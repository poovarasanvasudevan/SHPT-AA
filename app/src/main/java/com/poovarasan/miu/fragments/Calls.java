package com.poovarasan.miu.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.FragmentCallsBinding;

/**
 * Created by poovarasanv on 14/10/16.
 */

public class Calls extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentCallsBinding fragmentCallsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_calls, container, false);

        return fragmentCallsBinding.getRoot();
    }
}
