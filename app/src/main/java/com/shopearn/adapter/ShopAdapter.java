package com.shopearn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shopearn.R;
import com.shopearn.model.Category;

import java.util.List;

/**
 * Created by apple on 14/01/17.
 */

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder>{

    private Context context;
    private List<Category> list;
    private ViewHolder.ClickListener listener;

    public ShopAdapter(Context context, List<Category> list, ViewHolder.ClickListener listener){
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        ViewHolder viewHolder;
        context = parent.getContext();
        // view = LayoutInflater.from(context).inflate(R.layout.alerts_item_grid, parent, false);
            view = LayoutInflater.from(context).inflate(R.layout.home_grid_item, parent, false);

        viewHolder = new ViewHolder(view, viewType, listener);
        view.setOnClickListener(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtCat.setText(list.get(position).getName());
        holder.imgCat.setImageResource(getImage(list.get(position).getName()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ClickListener listener;
        private ImageView imgCat;
        private TextView txtCat;

        public ViewHolder(View itemView, int viewType, ClickListener listener) {
            super(itemView);
            this.listener = listener;

            imgCat = (ImageView) itemView.findViewById(R.id.imgCat);
            txtCat = (TextView) itemView.findViewById(R.id.txtName);
        }



        @Override
        public void onClick(View v) {
//            int id = v.getId();
//            if(id == R.id.imgCat)
                listener.onItemClick(getAdapterPosition());
        }

        public interface ClickListener{
            void onItemClick(int position);
            //            void onMenuItemClick(MenuItem item, int adapterPosition);
            void onMenuButtonClick(View view, int position);

        }
    }

    private int getImage(String name){
        int id = 0;

        if(name.equalsIgnoreCase("apparels"))
            id = R.drawable.apparels;
        if(name.equalsIgnoreCase("books"))
            id = R.drawable.books;
        if(name.equalsIgnoreCase("footwear"))
            id = R.drawable.footwear;
        if(name.equalsIgnoreCase("home"))
            id = R.drawable.home;
        if(name.equalsIgnoreCase("kitchen"))
            id = R.drawable.kitchen;
        if(name.equalsIgnoreCase("laptop"))
            id = R.drawable.laptop;
        if(name.equalsIgnoreCase("miscellaneous"))
            id = R.drawable.miscellaneous;
        if(name.equalsIgnoreCase("smartphones"))
            id = R.drawable.smartphones;

        return id;
    }

}
