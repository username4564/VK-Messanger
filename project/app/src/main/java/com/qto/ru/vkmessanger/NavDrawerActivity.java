package com.qto.ru.vkmessanger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.qto.ru.vkmessanger.drawer.DrawerListAdapter;
import com.qto.ru.vkmessanger.drawer.NavigationDrawerFragment;
import com.qto.ru.vkmessanger.drawer.items.HeaderItem;
import com.qto.ru.vkmessanger.drawer.IDrawerListItem;
import com.qto.ru.vkmessanger.drawer.items.ListItem;
import com.qto.ru.vkmessanger.fragments.AllFriendsFragment;
import com.qto.ru.vkmessanger.fragments.DialogsFragment;
import com.qto.ru.vkmessanger.fragments.OnlineFriendsFragment;
import com.qto.ru.vkmessanger.fragments.SettingsFragment;
import com.qto.ru.vkmessanger.services.PollingService;
import com.qto.ru.vkmessanger.vk.VkAccount;
import com.qto.ru.vkmessanger.vk.VkRest;
import com.qto.ru.vkmessanger.vk.VkUser;

import java.util.List;


public class NavDrawerActivity extends AppCompatActivity implements DrawerListAdapter.OnItemSelectedListener {
    private RecyclerView mDrawerList;
    private NavigationDrawerFragment mDrawerFragment;

    private CharSequence mTitle;

    private List<IDrawerListItem> mItemList;
    private DrawerListAdapter mItemListAdapter;

    private Thread mUpdateThread;
    private VkUser mUser;

    private static final int RESULT_AUTH_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mDrawerFragment.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        mDrawerList = (RecyclerView)findViewById(R.id.drawerList);
        mItemList = mDrawerFragment.getMenu();
        mItemListAdapter = new DrawerListAdapter(this, mItemList);
        mDrawerList.setAdapter(mItemListAdapter);
        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));
        mItemListAdapter.setOnItemSelectedListener(this);

        VkAccount account = VkAccount.getInstance();
        account.restore(this);

        if (account.getToken() == null && account.getUid() == null) {
            startActivityForResult(new Intent(this, AuthActivity.class), RESULT_AUTH_CODE);
        }

        onItemSelected(null, 1);


        Intent intent = new Intent(this, PollingService.class);
        startService(intent);

        updateItemList(true);
    }

    @Override
    public void onItemSelected(View view, int position) {
        Fragment fragment = null;
        switch (position){
            case 1:
                fragment = new DialogsFragment();
                break;
            case 2:
                fragment = new OnlineFriendsFragment();
                break;
            case 3:
                fragment = new AllFriendsFragment();
                break;
            case 5:
                fragment = new SettingsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", mUser);
                fragment.setArguments(bundle);
                break;
        }

        if (fragment != null) {
            onSectionAttached(position);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }

        if (mDrawerFragment != null) {
            mDrawerFragment.closeDrawer();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 5:
                mTitle = getString(R.string.title_settings);
        }
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case RESULT_AUTH_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    VkAccount account = VkAccount.getInstance();
                    account.setToken(data.getStringExtra("token"));
                    account.setUid(data.getStringExtra("uid"));
                    account.save(this);

                    onItemSelected(null, 1);
                    updateItemList(true);
                }
                if (resultCode == Activity.RESULT_CANCELED){
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerFragment.drawerOpened()){
            mDrawerFragment.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(mBroadcastReceiver, new IntentFilter(PollingService.ACTION_COUNTER_CHANGED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        stopService(new Intent(this, PollingService.class));
    }

    public void resetAuth(){
        VkAccount account = VkAccount.getInstance();
        account.reset(this);

        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.removeAllCookie();

        startActivityForResult(new Intent(this, AuthActivity.class), RESULT_AUTH_CODE);
    }

    private void updateItemList(final boolean all){
        if (mUpdateThread == null || !mUpdateThread.isAlive()) {
            mUpdateThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    VkAccount account = VkAccount.getInstance();

                    if (account != null && account.isActive()) {
                        VkRest rest = VkRest.getInstance();

                        if (all) {
                            mUser = rest.getUserById(Long.parseLong(VkAccount.getInstance().getUid()));
                            if (mUser != null) {
                                HeaderItem headerItem = (HeaderItem)mItemList.get(0);
                                headerItem.setAvatar(mUser.getPhoto50Source());
                                headerItem.setName(mUser.getFullName());

                            }
                        }

                        int unreadCount = rest.getUnreadCount();
                        ListItem dialogs = (ListItem)mItemList.get(1);
                        String info = unreadCount == 0 ? "" : "+" + unreadCount;
                        dialogs.setInfo(info);

                        mDrawerList.post(new Runnable() {
                            @Override
                            public void run() {
                                mItemListAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                }
            });
            mUpdateThread.start();
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateItemList(false);
        }
    };

}
