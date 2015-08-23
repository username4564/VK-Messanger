package com.qto.ru.vkmessanger.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.adapters.VkDialogAdapter;
import com.qto.ru.vkmessanger.services.PollingService;
import com.qto.ru.vkmessanger.vk.VkAccount;
import com.qto.ru.vkmessanger.vk.VkDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Используется для отображения диалогов пользователя
 */
public class DialogsFragment extends AbstractFragment<VkDialog> implements AdapterView.OnItemClickListener {


    /** Количество загружаемых диалогов */
    private int mListCount;
    /** Флаг загрузки диалогов */
    private boolean mInfoLoading;


    public DialogsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = createView(inflater, container, R.layout.fragment_dialogs);

        mItemList = new ArrayList<>();
        mListAdapter = new VkDialogAdapter(getActivity(), R.layout.item_dialog, mItemList);
        mItemListView.setAdapter(mListAdapter);

        mListCount = 20;
        mInfoLoading = true;

        mItemListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int lastTotalCount;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
                boolean loadMore = firstVisible + visibleCount != 0 &
                        firstVisible + visibleCount >= totalCount;

                if (loadMore && mInfoLoading) {
                    if (lastTotalCount == totalCount) {
                        mListCount = totalCount;
                        return;
                    }
                    mInfoLoading = false;
                    mUpdateFlag = true;
                    mListCount = mListCount + 20;
                    update();
                    lastTotalCount = totalCount;
                }
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PollingService.ACTION_MESSAGE_ADD);
        intentFilter.addAction(PollingService.ACTION_MESSAGE_READ);
        intentFilter.addAction(PollingService.ACTION_MESSAGE_READ_OUT);
        setIntentFilter(intentFilter);

        mUpdateAuto = false;

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        invokeDialog(mItemList.get(i).getUser());
    }

    /**
     * Обновляет список диалогов пользователя
     */
    protected void update(){
        if (VkAccount.getInstance().getToken() == null){
            return;
        }
        super.update();
    }

    @Override
    protected List<VkDialog> getRestItemList() {
        return mRest.getDialogs(mListCount);
    }

    @Override
    protected void prepareItemList() {}

    @Override
    protected void postItemList(){
        mInfoLoading = true;
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
