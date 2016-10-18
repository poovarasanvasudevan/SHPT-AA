package com.poovarasan.miu.fragments;

import android.Manifest;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.poovarasan.miu.R;
import com.poovarasan.miu.databinding.FragmentContactsBinding;
import com.poovarasan.miu.util.PermissionUtil;

import org.jokar.permissiondispatcher.annotation.NeedsPermission;
import org.jokar.permissiondispatcher.annotation.OnNeverAskAgain;
import org.jokar.permissiondispatcher.annotation.OnPermissionDenied;
import org.jokar.permissiondispatcher.annotation.RuntimePermissions;

/**
 * Created by poovarasanv on 14/10/16.
 */

@RuntimePermissions
public class Contacts extends Fragment {
    FragmentContactsBinding fragmentContactsBinding;
    int permisssionDeniedType = 0;

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
                PermissionUtil.startInstalledAppDetailsActivity(getActivity());

            }
        });
        return fragmentContactsBinding.getRoot();
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
