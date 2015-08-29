package com.qto.ru.vkmessanger.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.vk.VkUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Используется для отображения списка пользователей
 */
public class VkUserAdapter extends BaseAdapter {

    /** Список пользователей */
    private final List<VkUser> mItemList;

    public VkUserAdapter(List<VkUser> itemList) {
        mItemList = itemList;
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
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).getUid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VkUser user = mItemList.get(position);
        Resources resources = parent.getResources();

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
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

        Picasso.with(parent.getContext())
                .load(user.getPhoto50Source())
                .into(holder.photo);

        if (user.getOnline()){
            holder.online.setBackgroundColor(resources.getColor(R.color.onlineStatus));
        } else {
            holder.online.setBackgroundDrawable(null);
        }

        return convertView;
    }
}