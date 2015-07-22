package com.qto.ru.vkmessanger.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.qto.ru.vkmessanger.MessageActivity;
import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.adapters.VkDialogAdapter;
import com.qto.ru.vkmessanger.vk.VkAccount;
import com.qto.ru.vkmessanger.vk.VkDialog;
import com.qto.ru.vkmessanger.vk.VkRest;
import com.qto.ru.vkmessanger.vk.VkUser;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Используется для отображения диалогов пользователя
 */
public class DialogsFragment extends Fragment implements AdapterView.OnItemClickListener {

    /** Объект работы с REST API */
    private VkRest mRest;

    /** Список диалогов */
    private ListView mDialogList;
    /** Адаптер для отображения диалогов пользователя */
    private ArrayAdapter mDialogListAdapter;

    /** Список информации о диалогах */
    private List<VkDialog> mDialogInfoList;

    /** Поток загрузки списка диалогов */
    private Thread mLoadThread;
    /** Объект загрузки фотографий списка пользователей **/
    private PhotoLoadAsyncTask mPhotoAsyncTask;
    /** Таймер обновления данных */
    private Timer mUpdateTimer;

    public DialogsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialogs, container, false);

        mRest = VkRest.getInstance();

        mDialogList = (ListView)view.findViewById(R.id.dialogs);

        mDialogList.setOnItemClickListener(this);

        mUpdateTimer = new Timer();
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                update();
            }
        };
        mUpdateTimer.schedule(updateTask, 0, 10 * 1000);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUpdateTimer != null){
            mUpdateTimer.cancel();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        intent.putExtra("uid", mDialogInfoList.get(i).getUser().getUid());
        intent.putExtra("name", mDialogInfoList.get(i).getUser().getFullName());
        startActivity(intent);
    }

    /**
     * Обновляет список диалогов пользователя
     */
    private void update(){
        if (VkAccount.getInstance().getToken() == null){
            return;
        }
        if (mLoadThread == null || !mLoadThread.isAlive()) {
            mLoadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mDialogInfoList = mRest.getDialogs(0);
                    final VkUser users[] = new VkUser[mDialogInfoList.size()];
                    if (mDialogInfoList.size() > 0 && mDialogInfoList.get(0) != null) {
                        for (int i = 0; i < mDialogInfoList.size(); i++) {
                            users[i] = mDialogInfoList.get(i).getUser();
                        }
                    }
                    mDialogList.post(new Runnable() {
                        @Override
                        public void run() {
                            mDialogListAdapter = new VkDialogAdapter(getActivity(), R.layout.item_dialog, mDialogInfoList);

                            int firstItem = mDialogList.getFirstVisiblePosition();
                            mDialogList.setAdapter(mDialogListAdapter);
                            if (mDialogList.getCount() < firstItem) {
                                firstItem = mDialogList.getCount() - 1;
                            }
                            mDialogList.setSelection(firstItem);

                            mPhotoAsyncTask = new PhotoLoadAsyncTask();
                            mPhotoAsyncTask.adapter = mDialogListAdapter;
                            mPhotoAsyncTask.execute(users);
                        }
                    });
                }
            });
            mLoadThread.start();
        }
    }

}
