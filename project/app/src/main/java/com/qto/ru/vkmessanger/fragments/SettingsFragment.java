package com.qto.ru.vkmessanger.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.qto.ru.vkmessanger.NavDrawerActivity;
import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.util.CircleTransform;
import com.qto.ru.vkmessanger.vk.VkUser;
import com.squareup.picasso.Picasso;


public class SettingsFragment extends Fragment implements View.OnClickListener {


    public SettingsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ImageView photo = (ImageView)view.findViewById(R.id.photo);
        TextView name = (TextView)view.findViewById(R.id.name);

        if (getArguments() != null){
            VkUser user = (VkUser)getArguments().getSerializable("user");
            if (user != null) {
                Picasso.with(getActivity())
                        .load(user.getPhoto50Source())
                        .transform(new CircleTransform())
                        .into(photo);
                name.setText(user.getFullName());
            }
        }

        Button exit = (Button)view.findViewById(R.id.exit);
        exit.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.exit){
            ((NavDrawerActivity)getActivity()).resetAuth();
        }
    }
}
