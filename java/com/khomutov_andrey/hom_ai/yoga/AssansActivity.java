package com.khomutov_andrey.hom_ai.yoga;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.GridView;
import com.khomutov_andrey.hom_ai.yoga.adapters.Adt_gridAdapter;
import com.khomutov_andrey.hom_ai.yoga.dialogs.Dlg_QueryDelete;
import com.khomutov_andrey.hom_ai.yoga.util.Agregate;
import com.khomutov_andrey.hom_ai.yoga.util.Asana;
import com.khomutov_andrey.hom_ai.yoga.util.ControlYoga;
import com.khomutov_andrey.hom_ai.yoga.util.PresenterInterface;


import java.util.ArrayList;

/**
 * Класс обработчик экрана списка асан
 */
public class AssansActivity extends AppCompatActivity implements  PresenterInterface.IAsansView{ //Dlg_QueryTitle.NoticeDialogListener, Adt_gridAdapter.DragAction {
    private ControlYoga controlYoga; // Контроллер обработки сущностей
    private GridView gridView; // общий контейнер ассан
    //private LinearLayout linearLayoutTop; // Контейнер для выбранных ассан
    //private HorizontalScrollView scroll; // Прокрутка для контейнера выбранных элементов
    private ArrayList<Asana> asanaList; // Общий список ассан
    //private ArrayList<Asana> asanaTrainigList; // Список выбранных ассан
    private Asana selectedAsana; // Выбранная ассана
    //private Agregate agregate; // Агрегатор состояния ассан, используется для восстановления выбранных, при повороте экрана
    private Context mContext;
    private Button basket;
    private SearchView searchView;
    private PresenterInterface.IAsansPre presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assans);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        FloatingActionButton fabAddAsana = (FloatingActionButton) findViewById(R.id.fabAddAsana);
        //FloatingActionButton fabAddKit = (FloatingActionButton) findViewById(R.id.fabAddKit);
        fabAddAsana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AsanaAddActivity.class);
                mContext.startActivity(intent);
            }
        });

        /*
        fabAddKit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String title="";
                // Запускаем диалог и обрабатываем нажатия
                DialogFragment dialog = new Dlg_QueryTitle();
                //dialog.onAttach(AssansActivity.this);
                ((Dlg_QueryTitle)dialog).attach(AssansActivity.this);
                dialog.show(getFragmentManager(), "QueryTitleDialog");//TODO: попробовать getSupportFragmentManager для более низких версий
                Snackbar.make(v, getString(R.string.kit_add_quen), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null).show();
            }
        });
        */
        basket = (Button)findViewById(R.id.basket);

        int numberColumns = getResources().getInteger(R.integer.number_columns_grid);//Количество столбцов в гриде
        gridView = (GridView)findViewById(R.id.gridView);
        gridView.setNumColumns(numberColumns);

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {//
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAsana = asanaList.get(position);
                return false;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO: установить текущую позицию в адаптере,
                ((Adt_gridAdapter) gridView.getAdapter()).setCurrent(i); //TODO: можно удалить
                Intent intent = new Intent(mContext, AssanaActivity.class);
                intent.putExtra("id", String.valueOf(asanaList.get(i).getId()));
                mContext.startActivity(intent);
            }
        });
        presenter = new AsansActivityPre(this);
        gridView.setOnDragListener(dragListener);
        basket.setOnDragListener(dragListener);
        //agregate = (Agregate) getLastCustomNonConfigurationInstance();// восстанавливаем данные после смены конфигурации
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Получаем строку из SearchVew
        String query = null;
        if (searchView!=null){
            query = searchView.getQuery().toString();
        }
        asanaList = presenter.queryAsanasList(query);
        //Toast.makeText(mContext, "!"+query, Toast.LENGTH_SHORT).show();
        showAsans(asanaList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(presenter);
        searchView.setOnCloseListener(presenter);

        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    /*
    @Override //TODO: Удалить, используется для список выбранных для тренировки асан
    public Object onRetainCustomNonConfigurationInstance() {
        agregate = new Agregate(asanaList, asanaTrainigList, selectedAsana);
        return agregate;
    }

     */

    // Обрабатываем D&D
    View.OnDragListener dragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            basket.setVisibility(View.VISIBLE);// Отображаем кнопку-корзину
            basket.setBackgroundResource(R.drawable.basket_b);
            //TODO: controlYoga убрать controlYoga после вывода в презентер
            if (controlYoga == null){
                controlYoga = new ControlYoga(AssansActivity.this, "db");
            }
            switch (action){
                //case DragEvent.ACTION_DRAG_STARTED: break;
                case DragEvent.ACTION_DRAG_ENTERED: {
                    if(v.getClass().getSimpleName().equals("AppCompatButton")){
                        Button button = (Button)v;
                        button.setScaleX(2);
                        button.setScaleY(2);
                        button.setBackgroundResource(R.drawable.basket_b);
                    }
                    break;
                }
                case DragEvent.ACTION_DRAG_EXITED: {
                    if(v.getClass().getSimpleName().equals("AppCompatButton")){
                        Button button = (Button)v;
                        button.setScaleX(1);
                        button.setScaleY(1);
                    }
                    break;
                }
                case DragEvent.ACTION_DRAG_ENDED: {
                    if(v.getClass().getSimpleName().equals("AppCompatButton")){
                        //((Button)v).setBackgroundResource(R.drawable.basket_a);
                        Button button = (Button)v;
                        button.setScaleX(1);
                        button.setScaleY(1);
                        button.setVisibility(View.GONE);// Скрываем кнопку-корзину
                    }
                    return true;
                }
                case DragEvent.ACTION_DROP: {
                    basket.setVisibility(View.GONE);// Скрываем кнопку-корзину
                    //Находим ассану, которую перетаскивали из основного контейнера
                    //TODO: Проверить возможность замены на selectedAsana
                    Asana asana = controlYoga.getAssana(event.getClipData().getItemAt(0).getText().toString(),
                            event.getClipData().getItemAt(1).getText().toString());
                    ClipData clipData = event.getClipData();
                    if(v.getClass().getSimpleName().equals("AppCompatButton")){//Элемент перемещается в корзину, для удаления
                        //Определить, есть ли наборы с такой асаной, в зависимости от результата формировать разные сообщения диалогового окна
                        String message = getResources().getString(R.string.delete_query);
                        String positive = getResources().getString(R.string.yes);
                        String negative =  getResources().getString(R.string.no);
                        if(controlYoga.getCountKitsByAsana(selectedAsana)>0){
                            message = "Асана содержится в наборах, и будет удалена из всех наборов. Продолжить удаление?";
                            positive = "Продолжить";
                        }
                        Bundle arg = new Bundle();
                        arg.putString("message", message);
                        arg.putString("positive", positive);
                        arg.putString("negative",negative);
                        androidx.fragment.app.DialogFragment dialog = new Dlg_QueryDelete();
                        //Устанавливаем аргументы (текстовые значения для заголовка и кнопок)
                        dialog.setArguments(arg);
                        ((Dlg_QueryDelete)dialog).attach(new DeleteAsana());
                        dialog.show(getSupportFragmentManager(), "QueryDeleted");
                        return false;
                    }
                    return false;
                }
            }
            return true;//TODO: проверить с true
        }
    };

    /* Устаревший
    private void removeViewFrom(View viewContainer, int childIndex){
        linearLayoutTop.removeViewAt(childIndex);
        asanaTrainigList.remove(childIndex);
    }
     */

    /* Устаревший
    //Добавление визуального отображение ассаны в контейнер
    private void addViewTo(View view, Asana asana, int index){
        if(asana ==null){
            return;
        }
        switch (view.getClass().getSimpleName()){
            case "GridView":{
                GridView grid = (GridView)view;
                Adt_gridAdapter adapter = new Adt_gridAdapter(this, asanaTrainigList, null, null, null);// TODO: подставить реальный презентер для запросов?
                grid.setAdapter(adapter);
                break;
            }
            case "LinearLayout":{
                View v = new ViewAssanaBuilder(this, linearLayoutTop, asana, true).build();
                linearLayoutTop.addView(v, index);
                scroll.scrollTo(linearLayoutTop.getWidth(), 0);
                break;
            }
            case "HorizontalScrollView":{
                View v = new ViewAssanaBuilder(this, linearLayoutTop, asana, true).build();
                linearLayoutTop.addView(v, index);
                scroll.scrollTo(linearLayoutTop.getWidth(), 0);
                break;
            }
        }
    }
    */

    /*
    //TODO: Удадить (использовался для добавления набора)
    //Добавление тренировочного набора
    public void addAssanaKit(String title){
        if(asanaTrainigList == null || asanaTrainigList.isEmpty()){
            return;
        }
        AsanaKit asanaKit = new AsanaKit(-1, title, "", asanaTrainigList);
        controlYoga.addAssanaKit(asanaKit);
    }
    */

    @Override
    public Context getContext() {
        return this;
    }

    /**
     * Отображает список асан
     * @param asanasList список асан
     */
    @Override
    public void showAsans(ArrayList<Asana> asanasList) {
        Adt_gridAdapter gridAdapter = new Adt_gridAdapter(this, asanasList, null, null, null); // Адаптер для таблица с асанами
        gridAdapter.setLock(false);
        gridView.setAdapter(gridAdapter);
        //gridView.setOnDragListener(dragListener);
    }

    @Override
    public void showListSearch(CursorAdapter adapter) {
        //AutoCompleteTextView searchText = (AutoCompleteTextView)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        //searchView.setSuggestionsAdapter();
        return ;
    }

    @Override
    public void showBasket(boolean flag) {

    }

    @Override
    public void openAsana(Asana asana) {

    }


    // Обработчик подтверждения удаления в диалоговом окне (в диалоговом окне нажата кнопка подтверждения)
    private class DeleteAsana implements Dlg_QueryDelete.NoticeDialogListener{
        @Override
        public void onDeleted(boolean responce) {
            basket.setVisibility(View.GONE);// Скрываем кнопку с корзиной
            //TODO: controlYoga убрать controlYoga после вывода в презентер
            if (controlYoga == null){
                controlYoga = new ControlYoga(AssansActivity.this, "db");
            }
            if(responce){
                int countDeleted = controlYoga.deleteAsana(selectedAsana);
                if(countDeleted>0){
                    asanaList = controlYoga.loadFromStorege();
                    //TODO: Поробовать не создавать адаптер заново, а обновить его данные
                    Adt_gridAdapter gridAdapter = new Adt_gridAdapter(AssansActivity.this, asanaList, null, null, null);
                    gridAdapter.setLock(false);
                    gridView.setAdapter(gridAdapter);
                }
            }
        }
    }

    /* Устаревший
    // Реализация имплементированных методов диалогового окна запроса наименования для добавления набора, для обработки нажатий кнопок в диалоге
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        String title = ((Dlg_QueryTitle)dialog).getTitle();
        addAssanaKit(title);
        asanaTrainigList.clear();
        linearLayoutTop.removeAllViews();
        FloatingActionButton fabAddKit = (FloatingActionButton) findViewById(R.id.fabAddKit);
        Snackbar.make(fabAddKit, getString(R.string.kitAdded)+" "+title, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
    */

    /* Устаревшее
    // Реализация имплементированных методов диалогового окна, для обработки нажатий кнопок в диалоге
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO: Убрать вызовы controlYoga после переноса в презентер всех операций
        if(controlYoga!=null) {
            controlYoga.close();
        }
    }
}
