package com.qto.ru.vkmessanger;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.qto.ru.vkmessanger.adapters.VkMessageAdapter;
import com.qto.ru.vkmessanger.vk.VkMessage;
import com.qto.ru.vkmessanger.vk.VkRest;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Используется для отображения сообщений
 */
public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    /** Объект работы с REST API */
    private VkRest mRest;

    /** id пользователя */
    private long mUid;
    /** Имя и фамилия пользователя */
    private String mName;

    /** Текст для сообщения */
    private EditText mText;
    /** Список сообщений */
    private ListView mMessageList;
    /** Адаптер для отображения списка сообщений */
    private VkMessageAdapter mMessageListAdapter;
    /** Кнопка отправки сообщения */
    private Button mSend;

    /** Поток загрузки списка сообщений */
    private Thread mLoadThread;
    /** Таймер обновления данных */
    private Timer mUpdateTimer;

    /** Флаг информирующий о том, что происходит
     * первое обновление данных */
    private boolean mFirstUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            mUid = getIntent().getExtras().getLong("uid");
            mName = getIntent().getExtras().getString("name");
        }

        mRest = VkRest.getInstance();
        mFirstUpdate = true;

        mSend = (Button)findViewById(R.id.send);
        mText = (EditText)findViewById(R.id.text);
        mMessageList = (ListView)findViewById(R.id.messages);

        mSend.setOnClickListener(this);

        setTitle(mName);

        mUpdateTimer = new Timer();
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                update();
            }
        };
        mUpdateTimer.schedule(updateTask, 0, 10 * 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUpdateTimer != null){
            mUpdateTimer.cancel();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.send){
            if (!mText.getText().toString().equals("")) {
                mRest = VkRest.getInstance();
                mRest.sendMessage(mUid, mText.getText().toString());
                mText.setText("");
                if (mLoadThread != null && !mLoadThread.isAlive()) {
                    update();
                }
            }
        }
    }

    /**
     * Обновляет список сообщений
     */
    private void update(){
        if (mLoadThread == null || !mLoadThread.isAlive()) {
            final Activity activity = this;
            mLoadThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<VkMessage> messages = mRest.getMessageHistory(mUid);
                    Collections.reverse(messages);
                    mMessageList.post(new Runnable() {
                        @Override
                        public void run() {
                            mMessageListAdapter = new VkMessageAdapter(activity, R.layout.item_message_left, messages);

                            if (mFirstUpdate) {
                                mMessageList.setAdapter(mMessageListAdapter);
                                mMessageList.setSelection(messages.size() - 1);
                                mFirstUpdate = false;
                            } else {
                                int firstItem = mMessageList.getFirstVisiblePosition();
                                mMessageList.setAdapter(mMessageListAdapter);
                                if (mMessageList.getCount() < firstItem) {
                                    firstItem = mMessageList.getCount() - 1;
                                }
                                mMessageList.setSelection(firstItem);
                            }
                        }
                    });
                }
            });
            mLoadThread.start();
        }
    }

}
