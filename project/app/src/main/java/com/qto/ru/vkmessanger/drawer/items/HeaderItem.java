package com.qto.ru.vkmessanger.drawer.items;


import com.qto.ru.vkmessanger.drawer.DrawerListAdapter;
import com.qto.ru.vkmessanger.drawer.IDrawerListItem;

public class HeaderItem implements IDrawerListItem {
    private String name;
    private String avatar;

    public HeaderItem(String avatar, String name) {
        this.avatar = avatar;
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getType() {
        return DrawerListAdapter.TYPE_HEADER;
    }
}
