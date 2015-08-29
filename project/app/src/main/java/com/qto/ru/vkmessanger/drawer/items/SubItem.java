package com.qto.ru.vkmessanger.drawer.items;


import com.qto.ru.vkmessanger.drawer.DrawerListAdapter;
import com.qto.ru.vkmessanger.drawer.IDrawerListItem;

public class SubItem implements IDrawerListItem {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getType() {
        return DrawerListAdapter.TYPE_SUB;
    }
}
