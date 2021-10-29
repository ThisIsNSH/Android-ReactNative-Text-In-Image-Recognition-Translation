package com.jahnvi.textrecog.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jahnvi.textrecog.R;
import com.jahnvi.textrecog.model.Translate;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    List<Translate> list;
    Activity context;

    public CategoryAdapter(List<Translate> list, Activity context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_adapter, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Translate translate = list.get(i);
        myViewHolder.original.setText(translate.getOriginal());
        myViewHolder.translate.setText(translate.getTranslated());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView original, translate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            translate = itemView.findViewById(R.id.translate);
            original = itemView.findViewById(R.id.original);
        }
    }
}
