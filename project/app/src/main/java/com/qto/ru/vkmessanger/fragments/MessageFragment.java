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
import android.widget.Button;
import android.widget.EditText;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.adapters.VkMessageAdapter;
import com.qto.ru.vkmessanger.services.PollingService;
import com.qto.ru.vkmessanger.vk.VkMessage;
import com.qto.ru.vkmessanger.vk.VkRest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MessageFragment extends BaseListFragment<VkMessage> implements View.OnClickListener  {

    /** id пользователя */
    private long mUid;

    /** Текст для сообщения */
    private EditText mText;
    /** Кнопка отправки сообщения */
    private Button mSend;

    /** Флаг информирующий о том, что происходит
     * первое обновление данных */
    private boolean mFirstUpdate;
    /** Количество загружаемых сообщений */
    private int mListCount;
    /** Флаг загрузки сообщений */
    private boolean mInfoLoading;


    public MessageFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = createView(inflater, container, R.layout.fragment_message);

        mFirstUpdate = true;

        mSend = (Button)view.findViewById(R.id.send);
        mText = (EditText)view.findViewById(R.id.text);

        mSend.setOnClickListener(this);

        mItemList = new ArrayList<>();
        mListAdapter = new VkMessageAdapter(mItemList);
        mItemListView.setAdapter(mListAdapter);

        mListCount = 40;
        mInfoLoading = true;

        mItemListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int lastTotalCount;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
                boolean loadMore = firstVisible == 0;

                if (loadMore && mInfoLoading) {
                    if (lastTotalCount == totalCount) {
                        return;
                    }
                    mInfoLoading = false;
                    mListCount = mListCount + 40;
                    update();
                    lastTotalCount = totalCount;

                    mItemListView.setSelection(40 - 1);
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
    protected List<VkMessage> getRestItemList() {
        return mRest.getMessageHistory(mUid, mListCount);
    }

    @Override
    protected void prepareItemList() {
        Collections.reverse(mItemList);
        if (mFirstUpdate) {
            mItemListView.setSelection(40 - 1);
            mFirstUpdate = false;
        }
    }

    @Override
    protected void postItemList(){
        mInfoLoading = true;
    }

    /**
     * Устанавливает id пользователя
     * @param uid
     * id пользователя
     */
    public void setUid(long uid){
        mUid = uid;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {}

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send){
            if (!mText.getText().toString().equals("")) {
                mRest = VkRest.getInstance();
                mRest.sendMessage(mUid, mText.getText().toString());
                mText.setText(null);
                mUpdateEnd = true;
                update();
            }
        }
    }

    @Override
    protected void processBroadcast(Context context, Intent intent) {
        super.processBroadcast(context, intent);
        int fromUid = intent.getIntExtra(PollingService.EXTRA_USER_ID, -1);
        if (mUid == fromUid){
            update();
        }
    }
}
