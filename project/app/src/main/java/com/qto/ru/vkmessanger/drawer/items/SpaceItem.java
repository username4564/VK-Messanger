package com.qto.ru.vkmessanger.drawer.items;


import com.qto.ru.vkmessanger.drawer.DrawerListAdapter;
import com.qto.ru.vkmessanger.drawer.IDrawerListItem;

public class SpaceItem implements IDrawerListItem {
    @Override
    public int getType() {
        return DrawerListAdapter.TYPE_SPACE;
    }
}
