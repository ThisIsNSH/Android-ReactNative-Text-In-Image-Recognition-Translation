package com.jahnvi.textrecog.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jahnvi.textrecog.R;
import com.jahnvi.textrecog.RecyclerViewClickListener;
import com.jahnvi.textrecog.model.Language;
import com.jahnvi.textrecog.model.Translate;

import java.util.List;

public class PickerAdapter extends RecyclerView.Adapter<PickerAdapter.MyViewHolder> {

    List<Language> list;
    Activity context;
    private static RecyclerViewClickListener itemListener;

    public PickerAdapter(List<Language> list, Activity context, RecyclerViewClickListener itemListener) {
        this.list = list;
        this.context = context;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.picker_adapter, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final Language language = list.get(i);
        myViewHolder.original.setText(language.getLanguage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView original;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            original = itemView.findViewById(R.id.original);
            original.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getPosition());
        }
    }

}
