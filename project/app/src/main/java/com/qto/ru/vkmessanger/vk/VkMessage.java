package com.qto.ru.vkmessanger.vk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Используется для хранения информации о сообщении
 */
public class VkMessage {
    /** Строка хранящая в себе текст сообщения */
    private String mBody;
    /** Флаг информирующий о том, что сообщения прочитано */
    private long mRead;
    /** Дата отправки сообщения */
    private long mDate;
    /** Флаг информирующий о том, что сообщение исходящее */
    private long mOut;

    /**
     * Конструктор сообщения
     * @param body
     * Строка хранящая в себе текст сообщения
     * @param read
     * Флаг прочитанного сообщения
     * @param date
     * Дата отправки сообщения
     * @param out
     * Флаг исходящего сообщения
     */
    public VkMessage(String body, long read, long date, long out){
        mBody = body;
        mRead = read;
        mDate = date;
        mOut = out;
    }
    /**
     * Создает объект сообщения на основе Json объекта
     * @param object
     * Json объект
     * @throws JSONException
     * Ошибка работы с Json объектом
     */
    public VkMessage(JSONObject object) throws JSONException {
        mBody = object.getString("body");
        mRead = object.getLong("read_state");
        mDate = object.getLong("date");
        mOut = object.getLong("out");
    }

    /**
     * Возвращает текст сообщения
     * @return
     * Строка хранящая текст сообщения
     */
    public String getBody() {
        return mBody;
    }

    /**
     * Устанавливает текст сообщения
     * @param body
     * Строка хранящая текст сообщения
     */
    public void setBody(String body){
        if (body.equals("")){
            mBody = "[ Вложение ]";
        } else {
            mBody = body;
        }
    }

    /**
     * Возвращает дату отправки сообщения
     * @return
     * Дата отправки сообщения в формате unix time
     */
    public long getDate() {
        return mDate;
    }

    /**
     * Устанавливает дату отправки сообщения
     * @param date
     * Дата отправки сообщения в формате unix time
     */
    public void setDate(long date) {
        mDate = date;
    }

    /**
     * Возвращает флаг исходящего сообщения
     * @return
     * Флаг исходящего сообщения
     */
    public long getOut() {
        return mOut;
    }

    /**
     * Устанавливает флаг исходяего сообщения
     * @param out
     * Флаг исходящего сообщения
     */
    public void setOut(long out) {
        mOut = out;
    }

    /**
     * Возвращает флаг прочитанного сообщения
     * @return
     * Флаг прочитанного сообщения
     */
    public long getRead() {
        return mRead;
    }

    /**
     * Устанавливает флаг прочитанного сообщения
     * @param read
     * Флаг прочитанного сообщения
     */
    public void setRead(long read) {
        mRead = read;
    }

}
