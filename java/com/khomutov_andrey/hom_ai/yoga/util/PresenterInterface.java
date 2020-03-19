package com.khomutov_andrey.hom_ai.yoga.util;

import android.content.Context;
import androidx.appcompat.widget.SearchView;
import android.widget.CursorAdapter;

import java.util.ArrayList;

public interface PresenterInterface {

    public interface InterfaceToAsanaView {
        // К Vew для отображения данных
        public void fillData(Asana asana); //Отобразить данные асаны на экране
        public void viewTimeState(long time); // Отображает сколько времени осталось до конца асаны
        public void changeVoice(boolean isChecked); // Сменить состояние кнопки "Озвучить"
        public void changePlay(boolean play);
        public void speak(String text);
        public boolean isplay(); // Получить состояние проигрывается ли набор
    }

    public interface InterfaceFromAssanaView {
        // Запросы к презентеру на действия из Vew
        public void callPlayAsanaKit(int index); // Запрос на запуска набора асан (отобразить поочерёдно асаны в наборе по таймеру, озвучить названия)
        public void callStopAsanaKit();
    }


    // <-- AsansActivity
    /**
     * Запросы к View на отображение передаваемых данных
     */
    public interface IAsansView {
        public Context getContext();
        public void showAsans(ArrayList<Asana> asanasList);// Отображает список асан на экране
        public void showListSearch(CursorAdapter adapter);// Отображает варианты для поиска в SearchView
        public void showBasket(boolean flag);// Оторажает или скрывает корзину для удаления асаны
        public void openAsana(Asana asana);// Отобразить форму асаны, если asana==null, тогда открывается форма добавления асаны
    }

    /**
     * Запросы к презентору на действия от AssansView(экран списка асан),
     * с реализацией методов обработки событий поиска виджета SearchView
     */
    public interface IAsansPre extends SearchView.OnQueryTextListener, SearchView.OnCloseListener{
        // < Реализация событий SearchView
        @Override
        boolean onQueryTextSubmit(String s);
        @Override
        boolean onQueryTextChange(String s);
        @Override
        boolean onClose();

        // Реализация событий SearchView/>
        ArrayList<Asana> queryAsanasList(String title);// Запрос на список асан по названию или синониму, если null тогда все асаны

    }
    // AsansActivity -->


    // <-- KitActivity

    /**
     * Запросы к View на отображение данных
     */
    public interface IKitView{
        public Context context();
        public void addAssana(int index, String id);
        public void deleteAssana(int index, String id);
        public void showKit(AsanaKit kit);
        public void changingKit(String idAsanaFrom, int indexFrom, String idAsanaTo, int indexTo);
    }

    public interface IKitPre{
        public void updDateAsanaList(String idKit, ArrayList<Asana> asanaList);
        public void upDateKit(AsanaKit kit);
        public AsanaKit changingKitList(AsanaKit kit, String idAsanaFrom, int indexFrom, String idAsanaTo, int indexTo);// Смена порядка асан в наборе
    }
    // KitActivity-->

}
