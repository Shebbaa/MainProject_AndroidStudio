package com.example.isp291_folomeevstepan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ServicesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_services, container, false);
        RecyclerView rv = v.findViewById(R.id.rv_services);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ServiceItem> items = new ArrayList<>();
        // Здесь мы привязываем типы заявок к новым Активити (создадим их позже)
        items.add(new ServiceItem("Ремонт", "Диагностика и устранение неисправностей.", android.R.color.holo_blue_dark, CreateRepairActivity.class));
        items.add(new ServiceItem("Покраска", "Полная или частичная покраска. Выбор цвета.", android.R.color.holo_red_dark, CreatePaintActivity.class));
        items.add(new ServiceItem("Чистка", "Комплексная чистка и уход.", android.R.color.holo_green_dark, CreateCleaningActivity.class));
        items.add(new ServiceItem("Другое", "Индивидуальные услуги.", android.R.color.holo_orange_dark, CreateRequestActivity.class)); // Оставим старую как "Другое"

        rv.setAdapter(new ServiceAdapter(items));
        return v;
    }

    // Внутренний адаптер
    private class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.Holder> {
        List<ServiceItem> list;
        ServiceAdapter(List<ServiceItem> list) { this.list = list; }

        @NonNull @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            ServiceItem item = list.get(position);
            holder.title.setText(item.title);
            holder.desc.setText(item.description);
            holder.img.setImageResource(item.imageResId);
            holder.btn.setOnClickListener(v -> {
                Intent i = new Intent(getContext(), item.targetActivity);
                i.putExtra("category", item.title); // Передаем категорию
                startActivity(i);
            });
        }

        @Override public int getItemCount() { return list.size(); }

        class Holder extends RecyclerView.ViewHolder {
            TextView title, desc;
            ImageView img;
            Button btn;
            Holder(View v) {
                super(v);
                title = v.findViewById(R.id.tv_service_title);
                desc = v.findViewById(R.id.tv_service_desc);
                img = v.findViewById(R.id.img_service);
                btn = v.findViewById(R.id.btn_create_service);
            }
        }
    }
}