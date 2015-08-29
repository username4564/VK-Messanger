package com.qto.ru.vkmessanger.drawer.items;


import android.graphics.drawable.Drawable;

import com.qto.ru.vkmessanger.drawer.DrawerListAdapter;
import com.qto.ru.vkmessanger.drawer.IDrawerListItem;

public class ListItem implements IDrawerListItem {
    private String name;
    private Drawable icon;
    private String info;

    public ListItem(String name, Drawable icon) {
        this.name = name;
        this.icon = icon;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int getType() {
        return DrawerListAdapter.TYPE_ITEM;
    }
}
