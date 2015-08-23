package com.qto.ru.vkmessanger.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.vk.VkDialog;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

/**
 * Используется для отображения списка диалогов
 */
public class VkDialogAdapter extends ArrayAdapter<VkDialog> {
    /** Контекст приложения */
    private final Activity mContext;
    /** Список диалогов */
    private final List<VkDialog> mItemList;

    public VkDialogAdapter(Activity context, int id, List<VkDialog> names) {
        super(context, id, names);
        mContext = context;
        mItemList = names;
    }

    /**
     * Используется для хранения представлений
     * отображающих информацию
     */
    private static class ViewHolder {
        /** Полное имя пользователя */
        public TextView name;
        /** Текст последнего сообщения */
        public TextView text;
        /** Дата отправки последнего сообщения */
        public TextView date;
        /** Индикатор исходящего сообщения */
        public TextView out;
        /** Индикатор информирующий о том, что пользователь в сети */
        public FrameLayout online;
        /** Фото пользователя */
        public ImageView photo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VkDialog dialog = mItemList.get(position);

        Resources resources = mContext.getResources();

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_dialog, null);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.photo = (ImageView)convertView.findViewById(R.id.photo);
            holder.text = (TextView)convertView.findViewById(R.id.text);
            holder.date = (TextView)convertView.findViewById(R.id.date);
            holder.out = (TextView)convertView.findViewById(R.id.out);
            holder.online = (FrameLayout)convertView.findViewById(R.id.online);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.name.setText(dialog.getUser().getFullName());

        Picasso.with(parent.getContext())
                .load(dialog.getUser().getPhoto50Source())
                .into(holder.photo);

        holder.text.setText(dialog.getMessage().getBody());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dialog.getMessage().getDate() * 1000);
        String date = DateFormat.format("dd-MM-yyyy HH:mm", calendar).toString();
        holder.date.setText(date);

        if (dialog.getMessage().getOut() == 1) {
            holder.out.setText(">>  ");
        } else {
            holder.out.setText("");
        }

        if (dialog.getMessage().getRead() == 0 && dialog.getMessage().getOut() == 0) {
            convertView.setBackgroundColor(resources.getColor(R.color.messageNoRead));
        } else
        if (dialog.getMessage().getRead() == 0 && dialog.getMessage().getOut() == 1) {
            holder.text.setBackgroundColor(resources.getColor(R.color.messageNoRead));
            holder.out.setBackgroundColor(resources.getColor(R.color.messageNoRead));
        } else {
            holder.text.setBackgroundDrawable(null);
            holder.out.setBackgroundDrawable(null);
            convertView.setBackgroundDrawable(null);
        }

        if (dialog.getUser().getOnline() == 1) {
            holder.online.setBackgroundColor(resources.getColor(R.color.onlineStatus));
        } else {
            holder.online.setBackgroundDrawable(null);
        }

        return convertView;
    }
}