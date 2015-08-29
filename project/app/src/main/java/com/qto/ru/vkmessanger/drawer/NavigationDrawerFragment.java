package com.qto.ru.vkmessanger.drawer;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.drawer.items.HeaderItem;
import com.qto.ru.vkmessanger.drawer.items.ListItem;
import com.qto.ru.vkmessanger.drawer.items.SpaceItem;

import java.util.ArrayList;
import java.util.List;


public class NavigationDrawerFragment extends Fragment {

    public static final String PREF_FILE_NAME = "preferences";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    private View containerView;

    public NavigationDrawerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(
                readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if (savedInstanceState != null){
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        return view;
    }

    public List<IDrawerListItem> getMenu(){
        List<IDrawerListItem> listMenu = new ArrayList<>();
        Resources resources = getResources();

        listMenu.add(new HeaderItem(null, getString(R.string.app_name)));

        listMenu.add(new ListItem(getString(R.string.title_section1),
                resources.getDrawable(R.drawable.ic_chat_black_24dp)));
        listMenu.add(new ListItem(getString(R.string.title_section2),
                resources.getDrawable(R.drawable.ic_group_black_24dp)));
        listMenu.add(new ListItem(getString(R.string.title_section3),
                resources.getDrawable(R.drawable.ic_people_outline_black_24dp)));

        listMenu.add(new SpaceItem());

        listMenu.add(new ListItem(getString(R.string.title_settings),
                resources.getDrawable(R.drawable.ic_settings_white_24dp)));

        return listMenu;
    }

    public void setUp(int fragmentId, final DrawerLayout drawerLayout, Toolbar toolbar) {
        containerView = (View)getActivity().findViewById(fragmentId).getParent();
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!mUserLearnedDrawer){
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer + "");
                }
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        if(!mUserLearnedDrawer && !mFromSavedInstanceState){
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences(Context context,
                                         String preferenceName, String preferenceValue){
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context,
                                             String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    public boolean drawerOpened(){
        return mDrawerLayout.isDrawerOpen(containerView);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(containerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(containerView);
    }

}
