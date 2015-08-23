package com.qto.ru.vkmessanger;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.qto.ru.vkmessanger.fragments.MessageFragment;

/**
 * Используется для отображения сообщений
 */
public class MessageActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        Log.d("XX", "onCreate " + toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            MessageFragment fragment = (MessageFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.fragment);
            long uid = getIntent().getExtras().getLong("uid");
            String name = getIntent().getExtras().getString("name");
            fragment.setUid(uid);
            setTitle(name);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
