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
import com.qto.ru.vkmessanger.adapters.VkUserAdapter;
import com.qto.ru.vkmessanger.vk.VkRest;
import com.qto.ru.vkmessanger.vk.VkUser;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class OnlineFriendsFragment extends Fragment implements AdapterView.OnItemClickListener {

    /** Объект работы с REST API */
    private VkRest mRest;

    /** Список друзей, находящихся в сети */
    private ListView mFriendList;
    /** Адаптер для отображения списка друзей */
    private ArrayAdapter mFriendListAdapter;

    /** Список информации о друзьях */
    private List<VkUser> mUserList;

    /** Поток загрузки списка друзей */
    private Thread mLoadThread;
    /** Объект загрузки фотографий списка пользователей **/
    private PhotoLoadAsyncTask mPhotoAsyncTask;
    /** Таймер обновления данных */
    private Timer mUpdateTimer;


    public OnlineFriendsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        mRest = VkRest.getInstance();

        mFriendList = (ListView)view.findViewById(R.id.friends);

        mFriendList.setOnItemClickListener(this);

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

    /**
     * Обновляет список друзей, находящихся в сети
     */
    private void update(){
        if (mLoadThread == null || !mLoadThread.isAlive()) {
            mLoadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mUserList = mRest.getOnlineFriends();
                    Collections.sort(mUserList);
                    mFriendList.post(new Runnable() {
                        @Override
                        public void run() {
                            mFriendListAdapter = new VkUserAdapter(getActivity(), R.layout.item_user, mUserList);

                            int firstItem = mFriendList.getFirstVisiblePosition();
                            mFriendList.setAdapter(mFriendListAdapter);
                            if (mFriendList.getCount() < firstItem) {
                                firstItem = mFriendList.getCount() - 1;
                            }
                            mFriendList.setSelection(firstItem);

                            mPhotoAsyncTask = new PhotoLoadAsyncTask();
                            mPhotoAsyncTask.adapter = mFriendListAdapter;
                            mPhotoAsyncTask.execute(mUserList.toArray(new VkUser[mUserList.size()]));
                        }
                    });
                }
            });
            mLoadThread.start();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        intent.putExtra("uid", mUserList.get(i).getUid());
        intent.putExtra("name", mUserList.get(i).getFullName());
        startActivity(intent);
    }

}
