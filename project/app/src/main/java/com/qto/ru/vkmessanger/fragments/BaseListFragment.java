package com.qto.ru.vkmessanger.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.qto.ru.vkmessanger.MessageActivity;
import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.vk.VkAccount;
import com.qto.ru.vkmessanger.vk.VkRest;
import com.qto.ru.vkmessanger.vk.VkUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Шаблонный фрагмент использующийся
 * для отображения и обновления списков
 * @param <E>
 * Тип списка
 */
public abstract class BaseListFragment<E> extends Fragment implements
        AdapterView.OnItemClickListener {

    /** Объект работы с REST API */
    protected VkRest mRest;

    /** Список элементов */
    protected ListView mItemListView;
    /** Адаптер для отображения список элементов */
    protected BaseAdapter mListAdapter;
    /** Список информации об элементаъ */
    protected List<E> mItemList;

    /** Обработчик загрузки списка элементов */
    protected Handler mUpdateHandler;
    /** Интервал обновления списка элементов */
    protected int mUpdateTime;
    /** Поток загрузки списка элементов */
    protected Thread mUpdateThread;
    /** Флаг автообновления списка */
    protected boolean mUpdateAuto;

    /** Флаг перемещения в конец списка */
    protected boolean mUpdateEnd;
    /** Объект загрзуки списка элементов */
    protected Runnable mUpdateInfo = new Runnable() {
        @Override
        public void run() {
            update();
            if (mUpdateAuto) {
                mUpdateHandler.postDelayed(this, mUpdateTime);
            }
        }
    };
    /** Прогресс загрзуки списка элементов */
    protected ProgressBar mUpdateProgress;
    /** Флаг показа обновления */
    protected boolean mUpdateFlag;
    /** Фильтр намерений */
    private IntentFilter mIntentFilter;

    /**
     * Получает фильтр намерений
     * @return
     * Объект фильтра намерений
     */
    public IntentFilter getIntentFilter() {
        return mIntentFilter == null ? new IntentFilter() : mIntentFilter;
    }

    /**
     * Устанавливает фильтр намерений
     * @param mIntentFilter
     * Объект фильтра намерений
     */
    public void setIntentFilter(IntentFilter mIntentFilter) {
        this.mIntentFilter = mIntentFilter;
    }

    @Override
    public void onPause() {
        super.onPause();
        mUpdateHandler.removeCallbacks(mUpdateInfo);
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUpdateHandler.post(mUpdateInfo);
        getActivity().registerReceiver(mBroadcastReceiver, getIntentFilter());
    }

    /**
     * Создает представление для отображения
     * @param inflater
     * Объект служащий для добавления
     * представлений
     * @param container
     * Контейнер представлений
     * @param id
     * id фрагмента
     * @return
     * Представление
     */
    public View createView(LayoutInflater inflater, ViewGroup container,
                           int id) {
        View view = inflater.inflate(id, container, false);

        mRest = VkRest.getInstance();
        mUpdateTime = 10 * 1000;
        mUpdateFlag = true;

        mItemListView = (ListView)view.findViewById(R.id.items);
        mUpdateProgress = (ProgressBar)view.findViewById(R.id.progress);

        mUpdateProgress.setVisibility(View.GONE);

        mItemListView.setOnItemClickListener(this);

        mItemList = new ArrayList<>();

        mUpdateAuto = true;
        mUpdateHandler = new Handler();

        return view;
    }

    /**
     * Обновляет информацию списка
     */
    protected void update(){
        if (VkAccount.getInstance() == null || !VkAccount.getInstance().isActive()){
            return;
        }
        if (mUpdateFlag) {
            mUpdateProgress.setVisibility(View.VISIBLE);
        }
        if (mUpdateThread == null || !mUpdateThread.isAlive()) {
            mUpdateThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<E> itemList = getRestItemList();
                    mItemListView.post(new Runnable() {
                        @Override
                        public void run() {
                            mItemList.clear();
                            mItemList.addAll(itemList);
                            prepareItemList();
                            mListAdapter.notifyDataSetChanged();
                            postItemList();
                            mUpdateProgress.setVisibility(View.GONE);
                            mUpdateFlag = false;

                            if (mUpdateEnd){
                                mItemListView.setSelection(mListAdapter.getCount());
                                mUpdateEnd = false;
                            }
                        }
                    });
                }
            });
            mUpdateThread.start();
        }
    }

    /**
     * Вызывает диалог с указанным пользователем
     * @param user
     * Пользователь
     */
    protected void invokeDialog(VkUser user){
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        intent.putExtra("uid", user.getUid());
        intent.putExtra("name", user.getFullName());
        startActivity(intent);
    }

    /**
     * Получает список с помощью обращения
     * к REST API
     * @return
     * Список элементов
     */
    protected abstract List<E> getRestItemList();

    /**
     * Подготавливает список элементов
     * для отображения
     */
    protected abstract void prepareItemList();

    /**
     * Производит заданные действия
     * после обновления списка
     */
    protected void postItemList() {}

    /**
     * Обрабатывает полученное намерение
     */
    protected void processBroadcast(Context context, Intent intent) {}

    /**
     * Объект получения сервисных сообщений
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            processBroadcast(context, intent);
        }
    };
}
