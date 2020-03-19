package com.khomutov_andrey.hom_ai.yoga.behavior;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.khomutov_andrey.hom_ai.yoga.KitActivity;

/**
 * Created by hom-ai on 04.07.2017.
 * Класс описывает реакцию на длинное нажатие.
 * Вызывает открытие экрана набора ассан
 * long mIdKit - идентификатор набора, который следует отобразить на жкране
 */

public class OpenKit implements View.OnClickListener {
    long mIdKit;
    public static final String sId="id";

    public OpenKit(long id) {
        mIdKit = id;
    }

    public long getId() {
    return mIdKit;
}

    /** Обработка действия, открываем экран просмотра конкретного набора ассан.
     * Предаём в интент указанный id набора, и запускаем
     * @param view
     * @return
     */
    @Override
    public void onClick(View view) {
        //Log.d("OpenKitTAG","viewClick="+view.toString());
        Context context = view.getContext();
        Intent intent = new Intent(context, KitActivity.class);
        intent.putExtra(sId,mIdKit);
        context.startActivity(intent);
    }


}
