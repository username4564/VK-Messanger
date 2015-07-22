package com.qto.ru.vkmessanger.vk;


import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Используется для работы с REST API
 */
public class VkRest {
    /** Константа хранящая адрес сайта с REST API */
    private static final String END_POINT = "https://api.vk.com/method/";
    /** Константа хранящая имя основго Json объекта запросов*/
    private static final String RESPONSE_OBJECT = "response";

    /** Константа получения списка всех друзей */
    private static final String GET_ALL_FRIENDS = "0";
    /** Константа получения списка друзей находящихся в сети */
    private static final String GET_ONLINE_FRIENDS = "1";
    /** Константа получения списка друзей по id */
    private static final String GET_USERS_BY_ID = "2";
    /** Константа получения списка диалогов*/
    private static final String GET_DIALOGS = "3";
    /** Константа получения списка сообщений */
    private static final String GET_MESSAGE_HISTORY = "4";
    /** Константа отправки сообщения */
    private static final String SEND_MESSAGE = "5";

    /** Объект работы с REST API */
    private static VkRest sSingletone;
    /** Объект осуществляющий обработку запросов к REST API */
    private API mService;
    /** Объект конвертирования Json объектов */
    private VkConvert mConvert;

    /**
     * Конструктор объекта работы с REST API
     */
    private VkRest(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .build();
        mService = restAdapter.create(API.class);

        mConvert = new VkConvert();
    }

    /**
     * Возвращает объект работы с REST API
     * @return
     * Объект работы с REST API
     */
    public static VkRest getInstance(){
        if (sSingletone == null){
            sSingletone = new VkRest();
        }
        return sSingletone;
    }

    /**
     * Возвращает список всех друзей
     * @return
     * Список всех друзей
     */
    public List<VkUser> getAllFriends() {
        List<VkUser> userList = new ArrayList<>(100);

        try {
            String uid = VkAccount.getInstance().getUid();
            String json = invokeResponse(GET_ALL_FRIENDS, uid);

            JSONObject object = new JSONObject(json);
            JSONArray userInfo = object.getJSONArray(RESPONSE_OBJECT);

            for (int i = 0; i < userInfo.length(); i++) {
                VkUser user = mConvert.convertUser(userInfo.getJSONObject(i));
                user.setSex(userInfo.getJSONObject(i).getInt("sex"));
                if (userInfo.optJSONObject(i).has("bdate")) {
                    user.setAge(userInfo.getJSONObject(i).getString("bdate"));
                }
                userList.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userList;
    }

    /**
     * Возвращает список друзей находящихся в сети
     * @return
     * Список друзей находящихся в сети
     */
    public List<VkUser> getOnlineFriends() {
        List<VkUser> userList = new ArrayList<>(50);

        try {
            String uid = VkAccount.getInstance().getUid();
            String token = VkAccount.getInstance().getToken();
            String json = invokeResponse(GET_ONLINE_FRIENDS, uid, token);

            JSONObject object = new JSONObject(json);
            JSONArray friendsIds = object.getJSONArray(RESPONSE_OBJECT);
            String usersIds = "";

            for (int i = 0; i < friendsIds.length(); i++) {
                usersIds += friendsIds.getString(i);
                if (i != friendsIds.length()){
                    usersIds += ",";
                }
            }

            json = invokeResponse(GET_USERS_BY_ID, usersIds, token);

            object = new JSONObject(json);
            JSONArray userInfo = object.getJSONArray(RESPONSE_OBJECT);

            for (int i = 0; i < userInfo.length(); i++) {
                userList.add(mConvert.convertUser(userInfo.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userList;
    }

    /**
     * Возвращает список диалогов
     * @param offset
     * Смещение в списке диалогов
     * @return
     * Список диалогов
     */
    public List<VkDialog> getDialogs(long offset){
        List<VkDialog> dialogList = new ArrayList<>(20);

        try {
            String token = VkAccount.getInstance().getToken();
            String json = invokeResponse(GET_DIALOGS, String.valueOf(offset), token);

            JSONObject object = new JSONObject(json);
            JSONArray friendsIds = object.getJSONArray(RESPONSE_OBJECT);
            String usersIds = "";

            long dialogCount = friendsIds.getLong(0);
            for (int i = 1; i < friendsIds.length(); i++) {
                usersIds += friendsIds.getJSONObject(i).getString("uid");
                if (i != friendsIds.length()){
                    usersIds += ",";
                }

                VkDialog dialog = new VkDialog(
                        mConvert.convertMessage(friendsIds.getJSONObject(i)));
                dialogList.add(dialog);
            }

            if (dialogList.size() > 0){
                json = invokeResponse(GET_USERS_BY_ID, usersIds, token);

                object = new JSONObject(json);
                JSONArray userInfo = object.getJSONArray(RESPONSE_OBJECT);

                for (int i = 0; i < userInfo.length(); i++) {
                    dialogList.get(i).setUser(mConvert.convertUser(userInfo.getJSONObject(i)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dialogList;
    }

    /**
     * Возвращает список сообщений
     * @param id
     * id пользователя
     * @return
     * Список сообщений
     */
    public List<VkMessage> getMessageHistory(long id){
        List<VkMessage> messageList = new ArrayList<>(40);

        try {
            String token = VkAccount.getInstance().getToken();
            String json = invokeResponse(GET_MESSAGE_HISTORY, String.valueOf(id), token);

            JSONObject object = new JSONObject(json);
            JSONArray messageInfo = object.getJSONArray(RESPONSE_OBJECT);

            for (int i = 1; i < messageInfo.length(); i++) {
                messageList.add(mConvert.convertMessage(messageInfo.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messageList;
    }

    /**
     * Отправляет сообщение указанному пользователю
     * @param id
     * id пользователя
     * @param text
     * Текст сообщения
     * @return
     * Флаг отправки сообщения
     */
    public boolean sendMessage(long id, final String text){
        try {
            String token = VkAccount.getInstance().getToken();
            String json = invokeResponse(SEND_MESSAGE, String.valueOf(id), text, token);

            JSONObject object = new JSONObject(json);

            long messageId = object.getLong(RESPONSE_OBJECT);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Вызывает обработчик обращения к REST API
     * @param params
     * Параметры запроса к REST API
     * @return
     * Json строка
     */
    private String invokeResponse(String... params){
        ResponseAsyncTask responseAsyncTask = new ResponseAsyncTask();
        responseAsyncTask.execute(params);
        try {
            return responseAsyncTask.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Используется для обработки запросов к REST API
     */
    private class ResponseAsyncTask extends AsyncTask<String, Void, String> {
        /**
         * Выполняет запрос к REST API
         * @param strings
         * Параметры запроса
         * @return
         * Json строка
         */
        @Override
        protected String doInBackground(String... strings) {
            Response response = null;

            if (strings[0].equals(GET_ALL_FRIENDS)){
                response = mService.getAllUsers(strings[1]);
                return stringFromResponse(response);
            } else
            if (strings[0].equals(GET_ONLINE_FRIENDS)){
                response = mService.getOnlineUsersIds(strings[1], strings[2]);
            } else
            if (strings[0].equals(GET_USERS_BY_ID)){
                response = mService.getUsersByIds(strings[1], strings[2]);
            } else
            if (strings[0].equals(GET_DIALOGS)){
                response = mService.getDialogs(strings[1], strings[2]);
            } else
            if (strings[0].equals(GET_MESSAGE_HISTORY)){
                response = mService.getMessageHistory(strings[1], strings[2]);
            } else
            if (strings[0].equals(SEND_MESSAGE)){
                response = mService.sendMessage(strings[1], strings[2], strings[3]);
            }
            return stringFromResponse(response);
        }

        /**
         * Возвращает Json строку полученную из результата запроса
         * @param response
         * Объект запроса к REST API
         * @return
         * Json строка
         */
        protected String stringFromResponse(Response response){
            StringBuilder sb = new StringBuilder();

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getBody().in()));
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return sb.toString();
        }
    }

    /**
     * Используется для запросов к REST API
     */
    private interface API {
        /**
         * Возвращает объект запроса к REST API получающий
         * список всех пользователей
         * @param user
         * id пользователя
         * @return
         * Объект запроса к REST API
         */
        @POST("/friends.get?user_id=[user_id]&order=name&count&fields=first_name,last_name," +
                "photo_50,online,sex,bdate&name_case=nom")
        Response getAllUsers(@Query("user_id") String user);

        /**
         * Возвращает объект запроса к REST API получающий
         * список пользователей находящихся в сети
         * @param user
         * id пользователя
         * @param token
         * Ключ доступа
         * @return
         * Объект запроса к REST API
         */
        @POST("/friends.getOnline?user_id=[user_id]&order=name&access_token=[access_token]")
        Response getOnlineUsersIds(@Query("user_id") String user,
                                   @Query("access_token") String token);

        /**
         * Возвращает объект запроса к REST API получающий
         * список пользователей по id
         * @param user
         * id пользователя
         * @param token
         * Ключ доступа
         * @return
         * Объект запроса к REST API
         */
        @POST("/users.get?user_ids=[user_ids]&fields=first_name,last_name,photo_50,online" +
                "&name_case=nom&access_token=[access_token]")
        Response getUsersByIds(@Query("user_ids") String user,
                               @Query("access_token") String token);

        /**
         * Возвращает объект запроса к REST API получающий
         * список диалогов
         * @param offset
         * Смещение в списке диалогов
         * @param token
         * Ключ доступа
         * @return
         * Объект запроса к REST API
         */
        @POST("/messages.getDialogs?offset=[offset]&access_token=[access_token]")
        Response getDialogs(@Query("offset") String offset,
                            @Query("access_token") String token);

        /**
         * Возвращает объект запроса к REST API получающий
         * список сообщений
         * @param user
         * id пользователя
         * @param token
         * Ключ доступа
         * @return
         * Объект запроса к REST API
         */
        @POST("/messages.getHistory?user_id=[user_id]&count=40&access_token=[access_token]")
        Response getMessageHistory(@Query("user_id") String user,
                                   @Query("access_token") String token);

        /**
         * Возвращает объект запроса к REST API отправляющий
         * сообщение указанному пользователю
         * @param user
         * id пользователя
         * @param message
         * Текст сообщения
         * @param token
         * Ключ доступа
         * @return
         * Объект запроса к REST API
         */
        @POST("/messages.send?user_id=[user_id]&message=[message]&access_token=[access_token]")
        Response sendMessage(@Query("user_id") String user, @Query("message") String message,
                             @Query("access_token") String token);
    }
}
