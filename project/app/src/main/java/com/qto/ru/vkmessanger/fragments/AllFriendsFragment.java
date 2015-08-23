package com.qto.ru.vkmessanger.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.adapters.VkUserAdapter;
import com.qto.ru.vkmessanger.dialogs.FilterDialog;
import com.qto.ru.vkmessanger.services.PollingService;
import com.qto.ru.vkmessanger.vk.VkUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Используется для отображения списка всех друзей
 */
public class AllFriendsFragment extends AbstractFragment<VkUser> implements View.OnClickListener,
        SearchView.OnQueryTextListener {

    /** Строка поиска */
    private SearchView mSearch;
    /** Кнопка вызова фильтра поиска */
    private ImageButton mFilter;

    private List<VkUser> mAllUserList;

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


    public AllFriendsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = createView(inflater, container, R.layout.fragment_all_friends);

        mSearchString = "";
        mSearchSex = FilterDialog.SEX_ALL;
        mSearchAgeFrom = 0;
        mSearchAgeTo = 0;

        mSearch = (SearchView)view.findViewById(R.id.search);
        mFilter = (ImageButton)view.findViewById(R.id.filter);

        mSearch.setOnQueryTextListener(this);

        mFilter.setOnClickListener(this);


        mAllUserList = new ArrayList<>();
        mListAdapter = new VkUserAdapter(getActivity(), R.layout.item_user, mItemList);
        mItemListView.setAdapter(mListAdapter);

        mSearchStart = false;

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PollingService.ACTION_USER_ONLINE);
        intentFilter.addAction(PollingService.ACTION_USER_OFFLINE);
        setIntentFilter(intentFilter);

        mUpdateAuto = false;

        return view;
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
        invokeDialog(mItemList.get(i));
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

        mItemList.clear();

        for (VkUser user : mAllUserList){
            if (user.getFullName().toLowerCase().contains(mSearchString.toLowerCase())){
                mItemList.add(user);
            }
        }

        if (mSearchSex != FilterDialog.SEX_ALL){
            for (int i = 0; i < mItemList.size(); i++){
                if (mItemList.get(i).getSex() != mSearchSex){
                    mItemList.remove(i);
                    i--;
                }
            }
        }

        int ageFrom = mSearchAgeFrom + FilterDialog.MINIMAL_AGE;
        int ageTo = mSearchAgeTo + FilterDialog.MINIMAL_AGE;
        if (ageFrom > FilterDialog.MINIMAL_AGE && ageTo > FilterDialog.MINIMAL_AGE){
            for (int i = 0; i < mItemList.size(); i++){
                if (!(mItemList.get(i).getAge() >= ageFrom &&
                        mItemList.get(i).getAge() <= ageTo)){
                    mItemList.remove(i);
                    i--;
                }
            }
        }
        mListAdapter.notifyDataSetChanged();
    }


    @Override
    protected List<VkUser> getRestItemList() {
        return mRest.getAllFriends();
    }

    @Override
    protected void prepareItemList() {
        mAllUserList.clear();
        mAllUserList.addAll(mItemList);
        if (mSearchStart){
            search();
        }
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
