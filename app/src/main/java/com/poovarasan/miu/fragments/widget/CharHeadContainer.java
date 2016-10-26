package com.poovarasan.miu.fragments.widget;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.FragmentChatHeadContainerBinding;

/**
 * Created by poovarasanv on 26/10/16.
 */

public class CharHeadContainer extends Fragment {

    FragmentChatHeadContainerBinding fragmentChatHeadContainerBinding;

    public static CharHeadContainer newInstance() {
        CharHeadContainer testFragment = new CharHeadContainer();
        return testFragment;
    }

    public CharHeadContainer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentChatHeadContainerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_head_container, container, false);
        return fragmentChatHeadContainerBinding.getRoot();
    }
}
