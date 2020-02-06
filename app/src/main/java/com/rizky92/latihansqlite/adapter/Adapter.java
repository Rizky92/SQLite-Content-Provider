package com.rizky92.latihansqlite.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rizky92.latihansqlite.CustomOnClickListener;
import com.rizky92.latihansqlite.NoteAddUpdateActivity;
import com.rizky92.latihansqlite.R;
import com.rizky92.latihansqlite.entities.Notes;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private ArrayList<Notes> list = new ArrayList<>();
    private Activity activity;

    public Adapter(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<Notes> getList() {
        return list;
    }

    public void setList(ArrayList<Notes> list) {
        if (list.size() > 0) {
            this.list.clear();
        }
        this.list.addAll(list);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        holder.tvTitle.setText(list.get(position).getTitle());
        holder.tvDesc.setText(list.get(position).getDescription());
        holder.tvDate.setText(list.get(position).getDate());
        holder.cardView.setOnClickListener(new CustomOnClickListener(position, new CustomOnClickListener.OnItemClickCallback() {
            @Override
            public void onItemClicked(View view, int pos) {
                Intent intent = new Intent(activity, NoteAddUpdateActivity.class);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, pos);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, list.get(pos));
                activity.startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_UPDATE);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addItem(Notes notes) {
        this.list.add(notes);
        notifyItemInserted(list.size() - 1);
    }

    public void updateItem(int pos, Notes notes) {
        this.list.set(pos, notes);
        notifyItemChanged(pos, notes);
    }

    public void removeItem(int pos) {
        this.list.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, list.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final TextView tvTitle, tvDesc, tvDate;
        final CardView cardView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvDesc = itemView.findViewById(R.id.tv_item_desc);
            tvDate = itemView.findViewById(R.id.tv_item_date);
            cardView = itemView.findViewById(R.id.item_note);
        }
    }
}
