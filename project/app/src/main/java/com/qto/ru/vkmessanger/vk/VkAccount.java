package com.qto.ru.vkmessanger.vk;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.qto.ru.vkmessanger.R;

/**
 * Используется для хранения информации
 * об аккаунте пользователя
 */
public class VkAccount {
    /** Информация об аккаунте пользователя */
    private static VkAccount sSingletone;
    /** Строка хранящая ключ доступа */
    private String mToken;
    /** Строка хранящая id пользователя */
    private String mUid;

    /** Конструктор информации об аккаунте */
    private VkAccount(){}

    /**
     * Возвращает информацию о пользователе <code>VkAccount</code>
     * @return
     * Информация о пользователе
     */
    public static VkAccount getInstance(){
        if (sSingletone == null){
            sSingletone = new VkAccount();
        }
        return sSingletone;
    }

    /**
     * Возвращает ключ доступа
     * @return
     * Строка хранящая ключ доступа
     */
    public String getToken(){
        return mToken;
    }

    /**
     * Устанавливает ключ доступа
     * @param token
     * Строка хранящая ключ доступа
     */
    public void setToken(String token){
        this.mToken = token;
    }

    /**
     * Возвращает id пользователя
     * @return
     * Строка хранящая id пользователя
     */
    public String getUid(){
        return mUid;
    }

    /**
     * Устанавливает id пользователя
     * @param uid
     * Строка хранящая id пользователя
     */
    public void setUid(String uid){
        this.mUid = uid;
    }

    /**
     * Сбрасывает информацию о пользователе
     * и записывает ее в локальном хранилище
     * приложения
     * @param context
     * Контекст приложения
     */
    public void reset(Context context){
        mToken = null;
        mUid = null;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spEditor = sp.edit();

        spEditor.putString(context.getString(R.string.sp_token), mToken);
        spEditor.putString(context.getString(R.string.sp_uid), mUid);

        spEditor.apply();
    }

    /**
     * Сохраняет информацию о пользователе
     * путем сохранения ее в локальном хранилище
     * приложения
     * @param context
     * Контекст приложения
     */
    public void save(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spEditor = sp.edit();

        spEditor.putString(context.getString(R.string.sp_token), mToken);
        spEditor.putString(context.getString(R.string.sp_uid), mUid);

        spEditor.apply();
    }

    /**
     * Восстанавливает информацию о пользователе
     * из локального хранилища приложения
     * @param context
     * Контекст приложения
     */
    public void restore(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        mToken = sp.getString(context.getString(R.string.sp_token), null);
        mUid = sp.getString(context.getString(R.string.sp_uid), null);
    }

}
