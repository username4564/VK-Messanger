package com.qto.ru.vkmessanger.drawer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.util.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;


public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.ViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_SUB = 2;
    public static final int TYPE_SPACE = 3;

    private List<DrawerItem> items;
    private Context context;

    private OnItemSelectedListener onItemSelectedListener;


    public DrawerListAdapter(List<DrawerItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        if (TYPE_HEADER == type) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawer_header, viewGroup, false);
            return new ViewHolder(v, type);
        } else
        if (TYPE_ITEM == type){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawer_list, viewGroup, false);
            return new ViewHolder(v, type);
        } else
        if (TYPE_SUB == type){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawer_sub, viewGroup, false);
            return new ViewHolder(v, type);
        } else
        if (TYPE_SPACE == type){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawer_space, viewGroup, false);
            return new ViewHolder(v, type);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        DrawerItem record = items.get(i);
        if (TYPE_HEADER == record.getType()){
            viewHolder.name.setText(record.getName());
            if (record.getPath() != null){
                Picasso.with(context)
                        .load(record.getPath())
                        .transform(new CircleTransform())
                        .into(viewHolder.icon);
            } else {
                Picasso.with(context)
                        .load(R.drawable.camera_100)
                        .transform(new CircleTransform())
                        .into(viewHolder.icon);
            }
        } else
        if (TYPE_ITEM == record.getType()){
            viewHolder.name.setText(record.getName());
            viewHolder.icon.setImageDrawable(record.getIcon());
            viewHolder.info.setText(record.getInfo());
        } else
        if (TYPE_SUB == record.getType()){
            viewHolder.name.setText(record.getName());
        } else
        if (TYPE_SPACE == record.getType()){
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        return items.get(position).getType();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener){
        this.onItemSelectedListener = onItemSelectedListener;
    }

    /**
     * Реализация класса ViewHolder, хранящего ссылки на виджеты.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private ImageView icon;
        private TextView info;

        public ViewHolder(View itemView, int type) {
            super(itemView);
            if (TYPE_HEADER == type){
                name = (TextView)itemView.findViewById(R.id.name);
                icon = (ImageView)itemView.findViewById(R.id.icon);
            } else
            if (TYPE_ITEM == type){
                name = (TextView)itemView.findViewById(R.id.name);
                icon = (ImageView)itemView.findViewById(R.id.icon);
                info = (TextView)itemView.findViewById(R.id.info);
                itemView.setOnClickListener(this);
            } else
            if (TYPE_SUB == type){
                name = (TextView)itemView.findViewById(R.id.name);
            } else
            if (TYPE_SPACE == type){}
        }

        @Override
        public void onClick(View view) {
            if (onItemSelectedListener != null){
                onItemSelectedListener.onItemSelected(view, getPosition());
            }
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(View view, int position);
    }

}