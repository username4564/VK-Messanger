package com.qto.ru.vkmessanger.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.qto.ru.vkmessanger.util.ResponseUtils;
import com.qto.ru.vkmessanger.vk.VkRest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;


public class PollingService extends IntentService {

    private static final String TAG = "PS";

    /** Название сервиса */
    public static final String SERVICE_NAME = "LongPollService";
    /** Действие добавления сообщения */
    public static final String ACTION_MESSAGE_ADD =
            "com.qto.ru.vkmessanger.action.MESSAGE_ADD";
    /** Действие прочтения сообщений */
    public static final String ACTION_MESSAGE_READ =
            "com.qto.ru.vkmessanger.action.MESSAGE_READ";
    /** Действие прочтения исходящих сообщений */
    public static final String ACTION_MESSAGE_READ_OUT =
            "com.qto.ru.vkmessanger.action.MESSAGE_READ_OUT";
    /** Действие входа пользователя в сеть */
    public static final String ACTION_USER_ONLINE =
            "com.qto.ru.vkmessanger.action.USER_ONLINE";
    /** Действие выхода пользователя из сети */
    public static final String ACTION_USER_OFFLINE =
            "com.qto.ru.vkmessanger.action.USER_OFFLINE";
    /** Действие изменения счетчика собитий */
    public static final String ACTION_COUNTER_CHANGED =
            "com.qto.ru.vkmessanger.action.COUNTER_CHANGED";


    /** Дополнение id отправителя */
    public static final String EXTRA_USER_ID = "com.qto.ru.vkmessanger.extra.USER_ID";
    /** Дополнение счетчика событий */
    public static final String EXTRA_COUNTER = "com.qto.ru.vkmessanger.extra.COUNTER";


    /** Константа добавления сообщения */
    private static final int POLL_MESSAGE_ADD = 4;
    /** Константа прочтения сообщений */
    private static final int POLL_MESSAGE_READ = 6;
    /** Константа прочтения исходящих сообщений */
    private static final int POLL_MESSAGE_READ_OUT = 7;
    /** Константа входа пользователя в сеть */
    private static final int POLL_USER_ONLINE = 8;
    /** Константа выхода пользователя из сети */
    private static final int POLL_USER_OFFLINE = 9;
    /** Константа изменения счетчика собитий */
    private static final int POLL_COUNTER_CHANGED = 80;

    /** Флаг информирующий о том, что
     * сервис работает */
    private boolean mRunning;
    /** Объект осуществляющий обработку запросов к REST API */
    private IResponse mService;

    public PollingService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        while (mRunning) {
            try {
                VkRest rest = VkRest.getInstance();
                String[] longPoll = rest.getLongPoll();
                String[] server = longPoll[1].split("/");

                String key = longPoll[0];
                String ts = longPoll[2];
                String serverStart = server[0];
                String serverEnd = server[1];

                Log.d(TAG, "Poll - server: " + serverStart + "/" + serverEnd
                        + "  key: " + key + "  ts: " + ts);

                mService = new RestAdapter.Builder()
                        .setEndpoint("http://" + serverStart + "/")
                        .build()
                        .create(IResponse.class);

                while (mRunning) {
                    try {
                        Response response = mService.waitLongPoll(serverEnd, key, ts);
                        String responseString = ResponseUtils.stringFromResponse(response);
                        Log.d(TAG, "Poll - string: " + responseString);

                        try {
                            JSONObject object = new JSONObject(responseString);
                            JSONArray updates = object.getJSONArray("updates");

                            ts = object.getString("ts");

                            for (int i = 0; i < updates.length(); i++){
                                JSONArray param = updates.getJSONArray(i);
                                Intent broadcast = null;
                                int type = param.getInt(0);
                                Log.d(TAG, "Poll - type: " + type);

                                switch (type){
                                    case POLL_MESSAGE_ADD:
                                        broadcast = new Intent(ACTION_MESSAGE_ADD)
                                                .putExtra(EXTRA_USER_ID, param.getInt(3));
                                        break;
                                    case POLL_MESSAGE_READ:
                                        broadcast = new Intent(ACTION_MESSAGE_READ)
                                                .putExtra(EXTRA_USER_ID, param.getInt(1));
                                        break;
                                    case POLL_MESSAGE_READ_OUT:
                                        broadcast = new Intent(ACTION_MESSAGE_READ_OUT)
                                                .putExtra(EXTRA_USER_ID, param.getInt(1));
                                        break;
                                    case POLL_USER_ONLINE:
                                        broadcast = new Intent(ACTION_USER_ONLINE)
                                                .putExtra(EXTRA_USER_ID, param.getInt(1));
                                        break;
                                    case POLL_USER_OFFLINE:
                                        broadcast = new Intent(ACTION_USER_OFFLINE)
                                                .putExtra(EXTRA_USER_ID, param.getInt(1));
                                        break;
                                    case POLL_COUNTER_CHANGED:
                                        broadcast = new Intent(ACTION_COUNTER_CHANGED)
                                                .putExtra(EXTRA_COUNTER, param.getInt(1));
                                        break;
                                }

                                if (broadcast != null){
                                    sendBroadcast(broadcast);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e){
                        /* Timeout */
                        Log.d(TAG, "Poll - timeout");
                        SystemClock.sleep(1000);
                    }
                }

            } catch (Exception e){
                Log.d(TAG, "Poll - connection failed ");
                SystemClock.sleep(1000);
            }
        }

        Log.d(TAG, "Poll - finish");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRunning = true;

        Log.d(TAG, "Poll - onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRunning = false;

        Log.d(TAG, "Poll - onDestroy");
    }

    public interface IResponse {
        /**
         * Возвращает объект запроса к REST API получающий
         * обновления от LongPoll сервера
         * @param server
         * Адрес сервера
         * @param key
         * Секретный ключ сессии
         * @param ts
         * Номер последнего события
         * @return
         */
        @POST("/{server}?act=a_check&wait=10&mode=2")
        Response waitLongPoll(@Path("server") String server,
                              @Query("key") String key, @Query("ts") String ts);
    }
}
