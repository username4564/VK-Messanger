package com.qto.ru.vkmessanger.drawer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qto.ru.vkmessanger.R;
import com.qto.ru.vkmessanger.drawer.items.HeaderItem;
import com.qto.ru.vkmessanger.drawer.items.ListItem;
import com.qto.ru.vkmessanger.drawer.items.SubItem;
import com.qto.ru.vkmessanger.util.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;


public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_SUB = 2;
    public static final int TYPE_SPACE = 3;

    private List<IDrawerListItem> items;
    private Context context;

    private static OnItemSelectedListener onItemSelectedListener;


    public DrawerListAdapter(Context context, List<IDrawerListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View view = null;

        switch (type){
            case TYPE_HEADER:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_drawer_header, viewGroup, false);
                break;
            case TYPE_ITEM:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_drawer_list, viewGroup, false);
                break;
            case TYPE_SUB:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_drawer_sub, viewGroup, false);
                break;
            case TYPE_SPACE:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_drawer_space, viewGroup, false);
                break;
        }

        return view == null ? null : new ViewHolder(view, type);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        IDrawerListItem record = items.get(i);
        switch (record.getType()){
            case TYPE_HEADER:
                HeaderItem headerItem = (HeaderItem)record;

                viewHolder.name.setText(headerItem.getName());
                RequestCreator loader;
                if (headerItem.getAvatar() != null){
                    loader = Picasso.with(context)
                            .load(headerItem.getAvatar());
                } else {
                    loader = Picasso.with(context)
                            .load(R.drawable.camera_100);
                }
                loader.transform(new CircleTransform())
                        .into(viewHolder.icon);
                break;
            case TYPE_ITEM:
                ListItem listItem = (ListItem)record;
                viewHolder.name.setText(listItem.getName());
                viewHolder.icon.setImageDrawable(listItem.getIcon());
                viewHolder.info.setText(listItem.getInfo());
                break;
            case TYPE_SUB:
                SubItem subItem = (SubItem)record;
                viewHolder.name.setText(subItem.getName());
                break;
            case TYPE_SPACE:
                break;
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
    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private ImageView icon;
        private TextView info;

        public ViewHolder(View itemView, int type) {
            super(itemView);
            switch (type){
                case TYPE_HEADER:
                    name = (TextView)itemView.findViewById(R.id.name);
                    icon = (ImageView)itemView.findViewById(R.id.icon);
                    break;
                case TYPE_ITEM:
                    name = (TextView)itemView.findViewById(R.id.name);
                    icon = (ImageView)itemView.findViewById(R.id.icon);
                    info = (TextView)itemView.findViewById(R.id.info);
                    itemView.setOnClickListener(this);
                    break;
                case TYPE_SUB:
                    name = (TextView)itemView.findViewById(R.id.name);
                    break;
                case TYPE_SPACE:
                    break;
            }
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