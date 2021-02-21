package com.example.mytorch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.List;

public class SampleAdapter extends Adapter<ViewHolder> {
    private List<String> items;

    protected class ItemViewHolder extends ViewHolder {
        protected TextView textView;

        public ItemViewHolder(View view) {
            super(view);

            this.textView = (TextView) view.findViewById(R.id.item_tview);
        }
    }

    public int getItemCount() {
        return Constants.LIST_MAX_LENGTH;
    }

    public SampleAdapter(List<String> list) {
        this.items = list;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ItemViewHolder(LayoutInflater.from(viewGroup.getContext())

                .inflate(R.layout.item2, viewGroup, false));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        TextView textView = ((ItemViewHolder) viewHolder).textView;
        List<String> list = this.items;
        textView.setText((CharSequence) list.get(i % list.size()));
    }
}
