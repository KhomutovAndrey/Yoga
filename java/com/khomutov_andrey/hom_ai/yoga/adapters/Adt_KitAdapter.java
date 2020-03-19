package com.khomutov_andrey.hom_ai.yoga.adapters;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.khomutov_andrey.hom_ai.yoga.R;
import com.khomutov_andrey.hom_ai.yoga.behavior.OpenKit;
import com.khomutov_andrey.hom_ai.yoga.util.Asana;
import com.khomutov_andrey.hom_ai.yoga.util.AsanaKit;
import com.khomutov_andrey.hom_ai.yoga.util.ViewAssanaBuilder;

import java.util.ArrayList;

/**
 * Created by hom-ai on 15.06.2017.
 * Адаптер для набора ассан к RecyclerView
 */

public class Adt_KitAdapter extends RecyclerView.Adapter<Adt_KitAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public interface NoticeDeleted{
            public void onDeletedIndex(long index);
        }

        NoticeDeleted noticeDeleted;
        CardView cv; // CardView из разметки
        TextView titleKit; // для вывода наименования набора ассан
        ImageView imageAssana; // Изображение ассаны
        ImageView ivDelete;
        LinearLayout hllAssans; // Горизонтальный контейнер для изображений
        //int index;

        public void setListener(final OpenKit openKit){
            //Log.d("OpenKitTAG","viewClick="+view.toString());
            hllAssans.setOnClickListener(openKit);
            LinearLayout vLayout = (LinearLayout)itemView.findViewById(R.id.vLayout);// для перехвата нажатия всех элементов
            vLayout.setOnClickListener(openKit);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO: Открыть диалог удаления набора
                    noticeDeleted.onDeletedIndex(openKit.getId());
                    //Log.d("AsanaKit","viewClick="+view.toString());
                }
            });
        }

        public ViewHolder(View itemView, NoticeDeleted noticeDeleted, int index) {
            super(itemView);
            this.noticeDeleted = noticeDeleted;
            //cv = (CardView)itemView.findViewById(R.id.cv);
            titleKit = (TextView)itemView.findViewById(R.id.title_kit);
            hllAssans = (LinearLayout)itemView.findViewById(R.id.horizontal_layout);
            ivDelete = (ImageView)itemView.findViewById(R.id.ivDelete);
        }
    }


    Context mContext; // Для формирования вьюшки и вывода изображения
    ArrayList<AsanaKit> kits; // Данные (список наборов ассан)

    public Adt_KitAdapter (Context context, ArrayList<AsanaKit> kits){
        this.mContext = context;
        this.kits = kits;
    }

    public void setData(ArrayList<AsanaKit> kits){
        this.kits = kits;
    }

    @Override
    public Adt_KitAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adt_kit_item, parent, false);
        ViewHolder.NoticeDeleted noticeDeleted = (ViewHolder.NoticeDeleted)mContext;
        ViewHolder viewHolder = new ViewHolder(v, noticeDeleted, -1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AsanaKit kit = kits.get(position);
        holder.titleKit.setText(kit.getTitle());
        //holder.index=position;
        LinearLayout ll = holder.hllAssans;

        /** Очищаем ll от добавленных ранее view(асан)
         * Оставляем последний элемент - кнопка удаления набора
         */
        while (ll.getChildCount()>1){
            ll.removeViewAt(0);
        }

        // Настраиваем реакцию на длительный клик по элементу списка. Запускает экран просмотра набора, по его id
        holder.setListener(new OpenKit(kit.getId()));
        // Выводим изображения ассан
        ArrayList<Asana> assans = kits.get(position).getAssanaList();
        for (int i = 0; i < assans.size(); i++) {
            //ll.addView(new ViewAssanaBuilder(mContext, holder.hllAssans, assans.get(i), false).build());
            ll.addView(new ViewAssanaBuilder(mContext, holder.hllAssans, assans.get(i), false).build(),i);
        }
        holder.hllAssans.scrollTo(0,0);
    }

    @Override
    public int getItemCount() {
        return kits.size();
    }
}
