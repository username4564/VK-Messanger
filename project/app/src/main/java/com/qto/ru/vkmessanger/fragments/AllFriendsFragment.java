package com.qto.ru.vkmessanger.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.qto.ru.vkmessanger.MessageActivity;
import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.adapters.VkUserAdapter;
import com.qto.ru.vkmessanger.dialogs.FilterDialog;
import com.qto.ru.vkmessanger.vk.VkRest;
import com.qto.ru.vkmessanger.vk.VkUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Используется для отображения списка всех друзей
 */
public class AllFriendsFragment extends Fragment implements View.OnClickListener,
        SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    /** Объект работы с REST API */
    private VkRest mRest;

    /** Список всех друзей */
    private ListView mFriendList;
    /** Строка поиска */
    private SearchView mSearch;
    /** Кнопка вызова фильтра поиска */
    private ImageButton mFilter;
    /** Адаптер для отображения списка друзей */
    private ArrayAdapter mFriendListAdapter;
    /** Адаптер для отображения списка друзей с фильтрами */
    private ArrayAdapter mSearchListAdapter;

    /** Список пользователей */
    private List<VkUser> mUserList;
    /** Список найденных пользователей */
    private List<VkUser> mSearchList;

    /** Строка поиска */
    private String mSearchString;
    /** Фильтр пола */
    private int mSearchSex;
    /** Фильтр возрастра от */
    private int mSearchAgeFrom;
    /** Фильтр возрастра до */
    private int mSearchAgeTo;
    /** Флаг начала поиска */
    private boolean mSearchStart;

    /** Поток загрузки списка друзей */
    private Thread mLoadThread;
    /** Объект загрузки фотографий списка пользователей **/
    private PhotoLoadAsyncTask mPhotoAsyncTask;

    public AllFriendsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_friends, container, false);

        mRest = VkRest.getInstance();

        mSearchString = "";
        mSearchSex = FilterDialog.SEX_ALL;
        mSearchAgeFrom = 0;
        mSearchAgeTo = 0;

        mFriendList = (ListView)view.findViewById(R.id.friends);
        mSearch = (SearchView)view.findViewById(R.id.search);
        mFilter = (ImageButton)view.findViewById(R.id.filter);

        mSearch.setOnQueryTextListener(this);

        mFilter.setOnClickListener(this);

        mFriendList.setOnItemClickListener(this);

        update();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        mPhotoAsyncTask.cancel(true);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mSearchString = newText;
        search();
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.filter) {
            Bundle bundle = new Bundle();
            bundle.putInt("sex", mSearchSex);
            bundle.putInt("age_from", mSearchAgeFrom);
            bundle.putInt("age_to", mSearchAgeTo);
            DialogFragment dialogFragment = new FilterDialog();
            dialogFragment.setArguments(bundle);
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), "filterDialog");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        List<VkUser> visibleList = mSearchStart ? mSearchList : mUserList;
        intent.putExtra("uid", visibleList.get(i).getUid());
        intent.putExtra("name", visibleList.get(i).getFullName());
        startActivity(intent);
    }

    /**
     * Вызывает поиск по списку друзей с заданными фильтрами
     * @param sex
     * Пол
     * @param from
     * Возраст от
     * @param to
     * Возраст до
     */
    public void invokeSearch(int sex, int from, int to){
        mSearchSex = sex;
        mSearchAgeFrom = from;
        mSearchAgeTo = to;
        search();
    }

    /**
     * Осуществляет поиск по списку друзей
     */
    private void search(){
        mSearchStart = true;

        mSearchList.clear();
        for (VkUser user : mUserList){
            if (user.getFullName().toLowerCase().contains(mSearchString.toLowerCase())){
                mSearchList.add(user);
            }
        }

        if (mSearchSex != FilterDialog.SEX_ALL){
            for (int i = 0; i < mSearchList.size(); i++){
                if (mSearchList.get(i).getSex() != mSearchSex){
                    mSearchList.remove(i);
                    i--;
                }
            }
        }

        int ageFrom = mSearchAgeFrom + FilterDialog.MINIMAL_AGE;
        int ageTo = mSearchAgeTo + FilterDialog.MINIMAL_AGE;
        if (ageFrom > FilterDialog.MINIMAL_AGE && ageTo > FilterDialog.MINIMAL_AGE){
            for (int i = 0; i < mSearchList.size(); i++){
                if (!(mSearchList.get(i).getAge() >= ageFrom &&
                        mSearchList.get(i).getAge() <= ageTo)){
                    mSearchList.remove(i);
                    i--;
                }
            }
        }
        mSearchListAdapter.notifyDataSetChanged();
        mFriendList.setAdapter(mSearchListAdapter);
    }

    /**
     * Обновляет список всех друзей
     */
    private void update(){
        if (mLoadThread == null || !mLoadThread.isAlive()) {
            mLoadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mUserList = mRest.getAllFriends();
                    mSearchList = new ArrayList<>();

                    mFriendList.post(new Runnable() {
                        @Override
                        public void run() {
                            mFriendListAdapter = new VkUserAdapter(getActivity(), R.layout.item_user, mUserList);
                            mSearchListAdapter = new VkUserAdapter(getActivity(), R.layout.item_user, mSearchList);
                            mFriendList.setAdapter(mFriendListAdapter);

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

}
