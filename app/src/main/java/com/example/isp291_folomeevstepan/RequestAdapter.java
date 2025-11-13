package com.example.isp291_folomeevstepan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<Request> requests;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Request request);
    }

    public RequestAdapter(List<Request> requests, OnItemClickListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Request req = requests.get(position);
        String text = "Имя: " + req.model + "\nДата создания: " + req.creationDate + "\nДата выполнения: " + (req.completionDate != null ? req.completionDate : "N/A") + "\nСтатус: " + req.status;
        holder.tvItem.setText(text);
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(req));
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;

        ViewHolder(View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tv_item);
        }
    }
}