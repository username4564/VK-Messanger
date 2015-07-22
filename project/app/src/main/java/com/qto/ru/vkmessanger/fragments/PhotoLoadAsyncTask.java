package com.qto.ru.vkmessanger.fragments;

import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.qto.ru.vkmessanger.vk.VkUser;

/**
 * Используется для загрузки фотографий для списка пользователей
 */
class PhotoLoadAsyncTask extends AsyncTask<VkUser, Void, Void> {
    public ArrayAdapter adapter;

    /**
     * Выполняет загрузку фотографий пользователей
     * и сообщает о новой загрузке
     * @param lists
     * Список пользователей
     * @return
     * null
     */
    @Override
    protected Void doInBackground(VkUser... lists) {
        for (int i = 0; i < lists.length; i++){
            lists[i].loadPhoto();
            publishProgress();
        }
        return null;
    }

    /**
     * Обновляет отображение списка пользователей
     * @param values
     * null
     */
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

}
