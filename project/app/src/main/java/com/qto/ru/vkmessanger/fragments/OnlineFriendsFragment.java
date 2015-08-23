package com.qto.ru.vkmessanger.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.adapters.VkUserAdapter;
import com.qto.ru.vkmessanger.services.PollingService;
import com.qto.ru.vkmessanger.vk.VkAccount;
import com.qto.ru.vkmessanger.vk.VkUser;

import java.util.Collections;
import java.util.List;


public class OnlineFriendsFragment extends AbstractFragment<VkUser> {


    public OnlineFriendsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = createView(inflater, container, R.layout.fragment_friends);

        mListAdapter = new VkUserAdapter(getActivity(), R.layout.item_user, mItemList);
        mItemListView.setAdapter(mListAdapter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PollingService.ACTION_USER_ONLINE);
        intentFilter.addAction(PollingService.ACTION_USER_OFFLINE);
        setIntentFilter(intentFilter);

        mUpdateAuto = false;

        return view;
    }

    @Override
    protected void update() {
        if (VkAccount.getInstance().getToken() == null){
            return;
        }
        super.update();
    }

    @Override
    protected List<VkUser> getRestItemList() {
        return mRest.getOnlineFriends();
    }

    @Override
    protected void prepareItemList() {
        Collections.sort(mItemList);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        invokeDialog(mItemList.get(i));
    }

    @Override
    protected void processBroadcast(Context context, Intent intent) {
        super.processBroadcast(context, intent);
        int fromUid = intent.getIntExtra(PollingService.EXTRA_USER_ID, -1);
        if (fromUid != -1) {
            update();
        }
    }

}
