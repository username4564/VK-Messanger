package com.qto.ru.vkmessanger.vk;


import android.os.AsyncTask;

import com.qto.ru.vkmessanger.util.ResponseUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    /** Константа получения количества непрочитанных сообщений */
    private static final String GET_UNREAD_COUNT = "6";
    /** Константа получения информации о LongPoll сервере */
    private static final String GET_LONG_POLL = "7";

    /** Объект работы с REST API */
    private static VkRest sSingletone;
    /** Объект осуществляющий обработку запросов к REST API */
    private IResponse mService;

    /**
     * Конструктор объекта работы с REST API
     */
    private VkRest(){
        mService = new RestAdapter.Builder()
                .setEndpoint(END_POINT)
                .build()
                .create(IResponse.class);
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
                VkUser user = new VkUser(userInfo.getJSONObject(i));
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

            if (!usersIds.equals("")) {
                json = invokeResponse(GET_USERS_BY_ID, usersIds, token);

                object = new JSONObject(json);
                JSONArray userInfo = object.getJSONArray(RESPONSE_OBJECT);

                for (int i = 0; i < userInfo.length(); i++) {
                    userList.add(new VkUser(userInfo.getJSONObject(i)));
                }
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

            for (int i = 1; i < friendsIds.length(); i++) {
                usersIds += friendsIds.getJSONObject(i).getString("uid");
                if (i != friendsIds.length()){
                    usersIds += ",";
                }

                VkDialog dialog = new VkDialog(new VkMessage(friendsIds.getJSONObject(i)));
                dialogList.add(dialog);
            }

            if (dialogList.size() > 0){
                json = invokeResponse(GET_USERS_BY_ID, usersIds, token);

                object = new JSONObject(json);
                JSONArray userInfo = object.getJSONArray(RESPONSE_OBJECT);
                String usersIdsArray[] = usersIds.split(",");

                for (int i = 0; i < userInfo.length(); i++) {
                    VkUser user = new VkUser(userInfo.getJSONObject(i));
                    for (int j = 0; j < dialogList.size(); j++) {
                        if (usersIdsArray[j].equals(String.valueOf(user.getUid()))) {
                            dialogList.get(j).setUser(user);
                        }
                    }
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
    public List<VkMessage> getMessageHistory(long id, int count){
        List<VkMessage> messageList = new ArrayList<>(count);

        try {
            String token = VkAccount.getInstance().getToken();
            String json = invokeResponse(GET_MESSAGE_HISTORY, String.valueOf(id),
                    String.valueOf(count), token);

            JSONObject object = new JSONObject(json);
            JSONArray messageInfo = object.getJSONArray(RESPONSE_OBJECT);

            for (int i = 1; i < messageInfo.length(); i++) {
                messageList.add(new VkMessage(messageInfo.getJSONObject(i)));
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
    public long sendMessage(long id, final String text){
        try {
            String token = VkAccount.getInstance().getToken();
            String json = invokeResponse(SEND_MESSAGE, String.valueOf(id), text, token);

            JSONObject object = new JSONObject(json);

            return object.getLong(RESPONSE_OBJECT);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Получает информацию об указанном пользователя
     * @param id
     * id пользователя
     * @return
     * Объект хранящий информацию о пользователе
     */
    public VkUser getUserById(long id){
        try {
            String token = VkAccount.getInstance().getToken();
            String json = invokeResponse(GET_USERS_BY_ID, String.valueOf(id), token);

            JSONObject object = new JSONObject(json);

            return new VkUser(object.getJSONArray(RESPONSE_OBJECT).getJSONObject(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Получает количество непрочитанных сообщений
     * @return
     * Количество непрочитанных сообщений
     */
    public int getUnreadCount(){
        try {
            String token = VkAccount.getInstance().getToken();
            String json = invokeResponse(GET_UNREAD_COUNT, token);

            JSONObject object = new JSONObject(json);

            return object.getJSONObject(RESPONSE_OBJECT).getInt("count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Получает информацию о LongPoll сервере
     * @return
     * Строки с информацией о сервере LongPoll
     * 0 - ключ, 1 - сервер, 2 - номер последнего события
     */
    public String[] getLongPoll(){
        try {
            String token = VkAccount.getInstance().getToken();
            String json = invokeResponse(GET_LONG_POLL, token);

            JSONObject object = new JSONObject(json);

            String key = object.getJSONObject(RESPONSE_OBJECT).getString("key");
            String server = object.getJSONObject(RESPONSE_OBJECT).getString("server");
            String ts = object.getJSONObject(RESPONSE_OBJECT).getString("ts");

            String longPoll[] = new String[3];
            longPoll[0] = key;
            longPoll[1] = server;
            longPoll[2] = ts;

            return longPoll;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
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
         *
         * @param strings Параметры запроса
         * @return Json строка
         */
        @Override
        protected String doInBackground(String... strings) {
            Response response = null;

            try {
                if (GET_ALL_FRIENDS.equals(strings[0])) {
                    response = mService.getAllUsers(strings[1]);
                    return ResponseUtils.stringFromResponse(response);
                } else if (GET_ONLINE_FRIENDS.equals(strings[0])) {
                    response = mService.getOnlineUsersIds(strings[1], strings[2]);
                } else if (GET_USERS_BY_ID.equals(strings[0])) {
                    response = mService.getUsersByIds(strings[1], strings[2]);
                } else if (GET_DIALOGS.equals(strings[0])) {
                    response = mService.getDialogs(strings[1], strings[2]);
                } else if (GET_MESSAGE_HISTORY.equals(strings[0])) {
                    response = mService.getMessageHistory(strings[1], strings[2], strings[3]);
                } else if (SEND_MESSAGE.equals(strings[0])) {
                    response = mService.sendMessage(strings[1], strings[2], strings[3]);
                } else if (GET_UNREAD_COUNT.equals(strings[0])) {
                    response = mService.getUnreadCount(strings[1]);
                } else if (GET_LONG_POLL.equals(strings[0])) {
                    response = mService.getLongPoll(strings[1]);
                }
                //Log.d("XX", "Response: " + response.getUrl());
            } catch (Exception e) {
            }

            return ResponseUtils.stringFromResponse(response);
        }
    }

    /**
     * Используется для запросов к REST API
     */
    private interface IResponse {
        /**
         * Возвращает объект запроса к REST API получающий
         * список всех пользователей
         * @param user
         * id пользователя
         * @return
         * Объект запроса к REST API
         */
        @POST("/friends.get?order=name&count&fields=first_name,last_name," +
                "photo_100,online,sex,bdate&name_case=nom")
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
        @POST("/friends.getOnline?order=name")
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
        @POST("/users.get?fields=first_name,last_name,photo_100,online&name_case=nom")
        Response getUsersByIds(@Query("user_ids") String user,
                               @Query("access_token") String token);

        /**
         * Возвращает объект запроса к REST API получающий
         * список диалогов
         * @param count
         * Количество загружаемых диалогов диалогов
         * @param token
         * Ключ доступа
         * @return
         * Объект запроса к REST API
         */
        @POST("/messages.getDialogs")
        Response getDialogs(@Query("count") String count,
                            @Query("access_token") String token);

        /**
         * Возвращает объект запроса к REST API получающий
         * список сообщений
         * @param user
         * id пользователя
         * @param count
         * Количество загружаемых сообщений
         * @param token
         * Ключ доступа
         * @return
         * Объект запроса к REST API
         */
        @POST("/messages.getHistory")
        Response getMessageHistory(@Query("user_id") String user,
                                   @Query("count") String count,
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
        @POST("/messages.send")
        Response sendMessage(@Query("user_id") String user, @Query("message") String message,
                             @Query("access_token") String token);

        /**
         * Возвращает объект запроса к REST API получающий
         * количество не прочитанных сообщений
         * @param token
         * Ключ доступа
         * @return
         * Объект запроса к REST API
         */
        @POST("/messages.getDialogs?v=5.35&count=0&unread=1")
        Response getUnreadCount(@Query("access_token") String token);

        /**
         * Возвращает объект запроса к REST API получающий
         * информацию о LongPoll сервере
         * @param token
         * Ключ доступа
         * @return
         * Объект запроса к REST API
         */
        @POST("/messages.getLongPollServer")
        Response getLongPoll(@Query("access_token") String token);
    }
}
