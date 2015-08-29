package com.qto.ru.vkmessanger.vk;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Используется для хранения информации о пользователе
 */
public class VkUser implements Comparable, Serializable {
    /** Имя пользователя */
    private String mFirstName;
    /** Фамилия пользователя */
    private String mLastName;
    /** Ссылка к фото пользователя */
    private String mPhoto50Source;
    /** Флаг информирующий о том, что пользователь в сети */
    private boolean mOnline;
    /** id пользователя */
    private long mUid;
    /** Возраст пользователя */
    private int mAge;
    /** Пол пользователя */
    private int mSex;

    /**
     * Конструктор пользователя
     * @param firstName
     * Имя пользователя
     * @param lastName
     * Фамилия пользователя
     * @param photo100Source
     * Ссылка на фото пользователя
     * @param online
     * Флаг нахождения пользователя в сети
     * @param uid
     * id пользователя
     */
    public VkUser(String firstName, String lastName, String photo100Source, boolean online, long uid){
        mFirstName = firstName;
        mLastName = lastName;
        mPhoto50Source = photo100Source;
        mOnline = online;
        mUid = uid;
    }
    /**
     * Создает объект пользователя на основе Json объекта
     * @param object
     * Json объект
     * @throws JSONException
     * Ошибка работы с Json объектом
     */
    public VkUser(JSONObject object) throws JSONException {
        mFirstName = object.getString("first_name");
        mLastName = object.getString("last_name");
        mPhoto50Source = object.getString("photo_100");
        mOnline = object.getInt("online") != 0;
        mUid = object.getLong("uid");
    }

    @Override
    public String toString() {
        return mFirstName;
    }

    @Override
    public int compareTo(Object o) {
        return mFirstName.compareTo(o.toString());
    }

    /**
     * Возвращает имя пользователя
     * @return
     * имя пользователя
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Устанавливает имя пользователя
     * @param firstName
     * Имя пользователя
     */
    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    /**
     * Возвращает фамилию пользователя
     * @return
     * Фамилия пользователя
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Устанавливает фамилию пользователя
     * @param lastName
     * Фамилия пользователя
     */
    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    /**
     * Возвращает флаг информирующий о том, что пользователь в сети
     * @return
     * Флаг информирующий о том, что пользователь в сети
     */
    public boolean getOnline() {
        return mOnline;
    }

    /**
     * Устанавливает флаг информирующий о том, что пользователь в сети
     * @param online
     * Флаг информирующий о том, что пользователь в сети
     */
    public void setOnline(boolean online) {
        mOnline = online;
    }

    /**
     * Возвращает ссылку на фото пользователя
     * @return
     * Ссылка на фото пользователя
     */
    public String getPhoto50Source() {
        return mPhoto50Source;
    }

    /**
     * Устанавливает ссылку на фото пользователя
     * @param photo100Source
     * Ссылка на фото пользователя
     */
    public void setPhoto100Source(String photo100Source) {
        this.mPhoto50Source = photo100Source;
    }

    /**
     * Получает id пользователя
     * @return
     * id пользователя
     */
    public long getUid() {
        return mUid;
    }

    /**
     * Устанавливает id пользователя
     * @param uid
     * id пользователя
     */
    public void setUid(long uid) {
        mUid = uid;
    }

    /**
     * Получает имя и фамилию пользователя
     * @return
     * Имя и фамилия пользователя
     */
    public String getFullName(){
        return mFirstName + " " + mLastName;
    }

    /**
     * Возвращает возраст пользователя
     * @return
     * Возраст пользователя
     */
    public int getAge() {
        return mAge;
    }

    /**
     * Устанавливает возраст пользователя
     * @param bdate
     * Дата рождения в формате unix time
     */
    public void setAge(String bdate) {
        String date[] = bdate.split("\\.");
        if (date != null && date.length == 3){
            Calendar calendar = Calendar.getInstance();
            mAge = calendar.getTime().getYear() + 1900 - Integer.parseInt(date[2]);
        }
    }

    /**
     * Возвращает пол пользователя
     * @return
     * Пол пользователя
     */
    public long getSex() {
        return mSex;
    }

    /**
     * Устанавливает пол пользователя
     * @param sex
     * Пол пользователя
     */
    public void setSex(int sex) {
        mSex = sex;
    }
}
