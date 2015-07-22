package com.qto.ru.vkmessanger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.qto.ru.vkmessanger.fragments.AllFriendsFragment;
import com.qto.ru.vkmessanger.fragments.DialogsFragment;
import com.qto.ru.vkmessanger.fragments.NavigationDrawerFragment;
import com.qto.ru.vkmessanger.fragments.OnlineFriendsFragment;
import com.qto.ru.vkmessanger.vk.VkAccount;

/**
 * Используется для отображения главного окна с
 * выплывающим меню
 */
public class NavDrawerActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    /**
     * Константа с кодом для результата авторизации
     */
    private static final int AUTH_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getString(R.string.title_section1);

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        VkAccount account = VkAccount.getInstance();
        account.restore(this);

        if (account.getToken() == null && account.getUid() == null) {
            startActivityForResult(new Intent(this, AuthActivity.class), AUTH_CODE);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        onSectionAttached(position + 1);

        Fragment fragment = new Fragment();
        switch (position + 1){
            case 1:
                fragment = new DialogsFragment();
                break;
            case 2:
                fragment = new OnlineFriendsFragment();
                break;
            case 3:
                fragment = new AllFriendsFragment();
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
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
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.menu_drawer, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_exit) {
            VkAccount account = VkAccount.getInstance();
            account.reset(this);

            android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
            cookieManager.removeAllCookie();

            startActivityForResult(new Intent(this, AuthActivity.class), AUTH_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case AUTH_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    VkAccount account = VkAccount.getInstance();
                    account.setToken(data.getStringExtra("token"));
                    account.setUid(data.getStringExtra("uid"));
                    account.save(this);

                    mNavigationDrawerFragment.selectItem(0);
                }
                if (resultCode == Activity.RESULT_CANCELED){
                    finish();
                }
                break;
        }
    }

}
