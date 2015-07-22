package com.qto.ru.vkmessanger.vk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Используется для конвертирования Json
 * объектов в различные структуры данных
 * программы
 */
public class VkConvert {
    /**
     * Конвертирует Json объект в объект
     * сообщения
     * @param object
     * Json объект
     * @return
     * Сообщение полученное из Json объекта
     * @throws JSONException
     * Ошибка работы с Json объектом
     */
    public VkMessage convertMessage(JSONObject object) throws JSONException {
        String body = object.getString("body");
        long read = object.getLong("read_state");
        long date = object.getLong("date");
        long out = object.getLong("out");

        return new VkMessage(body, read, date, out);
    }

    /**
     * Конвертирует Json объект в объект
     * пользователя
     * @param object
     * Json объект
     * @return
     * Пользователь полученный из Json объекта
     * @throws JSONException
     * Ошибка работы с Json объектом
     */
    public VkUser convertUser(JSONObject object) throws JSONException {
        String firstName = object.getString("first_name");
        String lastName = object.getString("last_name");
        String photo100Source = object.getString("photo_50");
        int online = object.getInt("online");
        long uid = object.getLong("uid");

        return new VkUser(firstName, lastName, photo100Source, online, uid);
    }
}
