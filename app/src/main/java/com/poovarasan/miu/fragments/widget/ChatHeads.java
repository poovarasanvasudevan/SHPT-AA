package com.poovarasan.miu.fragments.widget;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.FragmentChatHeadBinding;

/**
 * Created by poovarasanv on 25/10/16.
 */

public class ChatHeads extends Fragment {

    FragmentChatHeadBinding fragmentChatHeadBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentChatHeadBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat_head, container, false);
        fragmentChatHeadBinding.chatHeadAllow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                } else {

                }
                Toast.makeText(getContext(), "Checked aaa", Toast.LENGTH_SHORT).show();
            }
        });

        fragmentChatHeadBinding.chatHeadAllowMultiple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                } else {

                }
                Toast.makeText(getContext(), "Checked aaa", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        return fragmentChatHeadBinding.getRoot();
    }
}
