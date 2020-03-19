package com.khomutov_andrey.hom_ai.yoga.util;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khomutov_andrey.hom_ai.yoga.R;

/**
 * Created by hom-ai on 29.05.2017.
 * Вспомогательный класс, строит визуальный компонент ассаны
 */

public class ViewAssanaBuilder {
    private final Context mContext;
    private final Asana mAsana;
    private View mView;

    //TODO: поробовать выводить на экран через этот класс, либо удалить
    private class AView extends View {
        private int index;
        private Asana asana;
        private View view;

        public AView(Context context) {
            super(context);
        }

        public AView(Context context, View view, int index, Asana asana){
            super(context);
            this.view = view;
            this.index=index;
            this.asana = asana;
        }

    }

    // Конструктор инициализирует необходимые компоненты и данные
    // viewGroup - Контейнер (родительский) в который будет добавлен визуальный компонент ассана
    public ViewAssanaBuilder(Context context, final ViewGroup viewGroup, final Asana asana, boolean dragFlag){
        mContext = context;
        mAsana = asana;
        mView = new View(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.adt_grid_item, viewGroup, false);
        TextView text = (TextView) mView.findViewById(R.id.text_assana);
        text.setText(asana.getTitle());
        final ImageView image = (ImageView) mView.findViewById(R.id.image_assana);
        ////Настройка Drag&Drop для image
        if(dragFlag){// Поведение Drag&Drop требуется не всегда
            image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ClipData clipData;
                    clipData = ClipData.newPlainText("id",String.valueOf(asana.getId()));
                    ClipData.Item item = ClipData.newPlainText("uri", mAsana.getUri()).getItemAt(0);
                    clipData.addItem(item);
                    item = ClipData.newPlainText("selected", "selected").getItemAt(0);
                    clipData.addItem(item);
                    // Индекс добавленного визуального компанента ассаны в родительсокм контейнере
                    // Потребуется для внешней обработки Drag&Drop, определения места компанента в контейнере
                    int index = getChildIndex(viewGroup, mView);
                    item = ClipData.newPlainText("childIndex", String.valueOf(index)).getItemAt(0);
                    clipData.addItem(item);
                    View.DragShadowBuilder dragShadow = new View.DragShadowBuilder(image);
                    v.startDrag(clipData, dragShadow, null, 0);
                    return false;
                }
            });
        }
        new AssanDisplayer(mContext, asana, image).display(null); // Создаём визуальный компонент ассаны

    }

    // Возвращает построенную вьюху, соджержащую визуальные компаненты ассаны
    public View build(){
        AView v = new AView(mContext, mView, 0, mAsana);
        //return v;
        return mView;
    }

    // Определяем индекс компанента во внешнем контейнере
    private int getChildIndex(ViewGroup viewGroup, View view){
        int index=0;
        for(int i=0; i<viewGroup.getChildCount(); i++){
            if (viewGroup.getChildAt(i).equals(view)){
                index = i;
                return index;
            }
        }
        return index;
    }
}
