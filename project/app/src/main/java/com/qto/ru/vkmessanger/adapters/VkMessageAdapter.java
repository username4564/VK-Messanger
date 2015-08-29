package com.qto.ru.vkmessanger.adapters;

import android.content.res.Resources;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.vk.VkMessage;

import java.util.Calendar;
import java.util.List;

/**
 * Используется для отображения списка сообщений
 */
public class VkMessageAdapter extends BaseAdapter {

    /** Список сообщений */
    private final List<VkMessage> mItemList;

    public VkMessageAdapter(List<VkMessage> itemList) {
        mItemList = itemList;
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
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).getDate();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VkMessage message = mItemList.get(position);

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (message.getOut()) {
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

        if (!message.getRead()){
            Resources resources = parent.getResources();
            convertView.setBackgroundColor(resources.getColor(R.color.messageNoRead));
        }

        return convertView;
    }
}