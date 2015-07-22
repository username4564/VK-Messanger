package com.qto.ru.vkmessanger.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.vk.VkUser;

import java.util.List;

/**
 * Используется для отображения списка пользователей
 */
public class VkUserAdapter extends ArrayAdapter<VkUser> {
    /** Контекст приложения */
    private final Activity mContext;
    /** Список пользователей */
    private final List<VkUser> mItemList;

    public VkUserAdapter(Activity context, int id, List<VkUser> names) {
        super(context, id, names);
        mContext = context;
        mItemList = names;
    }

    /**
     * Используется для хранения представлений
     * отображающих информацию
     */
    private static class ViewHolder {
        /** Фото пользователя */
        public ImageView photo;
        /** Полное имя пользователя */
        public TextView name;
        /** Индикатор информирующий о том, что пользователь в сети */
        public FrameLayout online;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VkUser user = mItemList.get(position);
        Resources resources = mContext.getResources();

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_user, null, true);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.photo = (ImageView) convertView.findViewById(R.id.photo);
            holder.online = (FrameLayout)convertView.findViewById(R.id.online);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(user.getFullName());

        if (user.getPhoto50() == null){
            holder.photo.setImageDrawable(resources.getDrawable(R.drawable.camera_100));
        } else {
            holder.photo.setImageBitmap(user.getPhoto50());
        }

        if (user.getOnline() == 1){
            holder.online.setBackgroundColor(resources.getColor(R.color.onlineStatus));
        } else {
            holder.online.setBackgroundDrawable(null);
        }

        return convertView;
    }
}