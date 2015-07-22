package com.qto.ru.vkmessanger.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.vk.VkMessage;

import java.util.Calendar;
import java.util.List;

/**
 * Используется для отображения списка сообщений
 */
public class VkMessageAdapter extends ArrayAdapter<VkMessage> {
    /** Контекст приложения */
    private final Activity mContext;
    /** Список сообщений */
    private final List<VkMessage> mItemList;

    public VkMessageAdapter(Activity context, int id, List<VkMessage> names) {
        super(context, id, names);
        mContext = context;
        mItemList = names;
    }

    /**
     * Используется для хранения представлений
     * отображающих информацию
     */
    private static class ViewHolder {
        /** Текст сообщения */
        public TextView text;
        /** Время отправления сообщения */
        public TextView time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VkMessage message = mItemList.get(position);

        LayoutInflater inflater = mContext.getLayoutInflater();
        if (message.getOut() == 1) {
            convertView = inflater.inflate(R.layout.item_message_right, null, true);
        } else {
            convertView = inflater.inflate(R.layout.item_message_left, null, true);
        }

        ViewHolder holder = new ViewHolder();

        holder.text = (TextView)convertView.findViewById(R.id.text);
        holder.time = (TextView)convertView.findViewById(R.id.time);

        convertView.setTag(holder);

        holder.text.setText(message.getBody());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(message.getDate() * 1000);
        String date = DateFormat.format("HH:mm:ss", cal).toString();
        holder.time.setText(date);

        if (message.getRead() == 0){
            Resources resources = mContext.getResources();
            convertView.setBackgroundColor(resources.getColor(R.color.messageNoRead));
        }

        return convertView;
    }
}