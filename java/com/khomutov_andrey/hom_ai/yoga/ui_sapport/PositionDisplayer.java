package com.khomutov_andrey.hom_ai.yoga.ui_sapport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.khomutov_andrey.hom_ai.yoga.R;
import com.khomutov_andrey.hom_ai.yoga.util.IteratorAsana;

/**
 * Created by Andrey on 11.11.2017.
 * Класс реализует вывод и логику работы индикатора текущего положения элемента списка.
 * Полоежение в списке и размер - через интерфейс IteratorAsana
 * Событие изменения текущего положения - через интерфейс IndicatorPosition
 * Компанент индикатора - ImageView в который загружается изображение,
 * изменение изображения (текущий) по состоянию поля enable (меняется изображение)
 */

public class PositionDisplayer extends LinearLayout implements IndicatorPosition {
    private IteratorAsana mLinkedComponent; //Список элементов для индикатора положения
    private LinearLayout layout; // корневой лейаут на котором располагаются индикаторы
    Context mContext;

    public PositionDisplayer(Context context) {
        super(context);
        mContext = context;
        initComponent();
    }

    private void initComponent() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.ui_position_displayer, this);
        layout = (LinearLayout) view.findViewById(R.id.layout_position_disp);
    }

    // Устанавливает ссылку на список для отображения индикатора
    public PositionDisplayer setLinkedComponent(IteratorAsana linkedComponent) {
        mLinkedComponent = linkedComponent;
        return this;
    }


    public void show() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0);
        //Удаляем ранее добавленные компаненты индикаторы
        if(layout!=null){
            layout.removeAllViews();
        }
        // Формируем компаненты индикатора
        if (mLinkedComponent != null) {
            ImageView image;
            boolean enabled = true;
            for (int i = 0; i < mLinkedComponent.getSize(); i++) {
                if (mLinkedComponent.getCurrentPosition() == i) {
                    enabled = true;
                } else enabled = false;
                image = new ImageView(mContext);
                image.setLayoutParams(params);
                image.setEnabled(enabled);
                image.setImageResource(R.drawable.position_displayer);
                layout.addView(image);
            }
        }

    }

    // Обработка события именения текущего положения списка
    @Override
    public void change() {
        if (mLinkedComponent == null) return;
        int curPosition = mLinkedComponent.getCurrentPosition();

        for (int i = 0; i < layout.getChildCount(); i++) {
            if (i==curPosition) {
                ((ImageView) layout.getChildAt(i)).setEnabled(true);
            } else {
                ((ImageView) layout.getChildAt(i)).setEnabled(false);
            }

        }
    }

}
