package com.qto.ru.vkmessanger.drawer;


import android.graphics.drawable.Drawable;

public class DrawerItem {
    private String name;
    private Drawable icon;
    private String info;
    private int type;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public DrawerItem(String user, String nullable, Drawable icon){
        this.name = user;
        this.icon = icon;
        this.type = DrawerListAdapter.TYPE_HEADER;
    }

    public DrawerItem(String name, Drawable icon){
        this.name = name;
        this.icon = icon;
        this.type = DrawerListAdapter.TYPE_ITEM;
    }

    public DrawerItem(String name){
        this.name = name;
        this.type = DrawerListAdapter.TYPE_SUB;
    }

    public DrawerItem(){
        this.type = DrawerListAdapter.TYPE_SPACE;
    }

    public int getType() {
        return type;
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

}
