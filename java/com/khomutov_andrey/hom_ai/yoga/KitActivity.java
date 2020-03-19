package com.khomutov_andrey.hom_ai.yoga;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.view.DragEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.khomutov_andrey.hom_ai.yoga.adapters.Adt_gridAdapter;
import com.khomutov_andrey.hom_ai.yoga.util.Asana;
import com.khomutov_andrey.hom_ai.yoga.util.AsanaKit;
import com.khomutov_andrey.hom_ai.yoga.util.ControlYoga;
import com.khomutov_andrey.hom_ai.yoga.util.DragDropKit;
import com.khomutov_andrey.hom_ai.yoga.util.PresenterInterface;

import java.util.ArrayList;

/**
 * Экран списка наборов
 */
//TODO: переработать под пресентер KitActivityPre
public class KitActivity extends AppCompatActivity implements PresenterInterface.IKitView {
    private GridView gridAssan; // Контейнер для отображения ассан набора
    private GridView gridAssanAll; // Контейнер для отображения всех ассан
    //private TextView tvLabelKit; // Вывести заголовок набора ассан
    private EditText etLabelKit;
    private ToggleButton tbLock; // Открывает/закрывает режим редактирования (состав и порядок асан) набора
    private AsanaKit asanaKit; // Набор ассан
    private ArrayList<Asana> asanaList; // Общий список ассан
    private ControlYoga controlYoga;
    //DragDropListener splitterListener; // Реализация D&D для разделителя и значков асан
    PresenterInterface.IKitPre presener;
    private Long idKit;
    private DragDropView ddView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit);

        Intent intent = getIntent();
        idKit = intent.getLongExtra("id", 0);

        tbLock = (ToggleButton)findViewById(R.id.tbLock);
        //tbLock.setOnClickListener(new LockButton());
        tbLock.setOnCheckedChangeListener(new ChangeLock());
        etLabelKit = (EditText)findViewById(R.id.etLabelKit);
        etLabelKit.setEnabled(!tbLock.isChecked());
        gridAssan = (GridView)findViewById(R.id.gridAssans);
        gridAssanAll = (GridView)findViewById(R.id.gridAssansAll);

        int numberColumns = getResources().getInteger(R.integer.number_columns_grid);//Количество столбцов в гриде
        gridAssan.setNumColumns(numberColumns);
        gridAssanAll.setNumColumns(numberColumns);
        gridAssan.setTag("add");
        gridAssanAll.setTag("delete");// по этому тегу удаление ассаны (при переносе элемента в контейнер, читается тэг и сравнивается)

        controlYoga = new ControlYoga(this, "db");

        CardView cvKit = (CardView)findViewById(R.id.cvKit);
        ddView = new DragDropView();
        cvKit.setOnDragListener(ddView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        etLabelKit.setEnabled(!tbLock.isChecked());
        asanaKit = controlYoga.getKitById(String.valueOf(idKit));
        if(asanaKit ==null){
            asanaKit = new AsanaKit(0,"","0",null);
        }
        asanaList = controlYoga.loadFromStorege();// Получаем ассаны из источника
        presener = new KitActivityPre(this);
        ImageView ivSlideShowe = (ImageView)findViewById(R.id.ivSlideShow);
        ivSlideShowe.setOnClickListener(new KitSlideShow());
        fillData();
    }

    private void fillData(){
        etLabelKit.setText(asanaKit.getTitle());
        Adt_gridAdapter gridAdapter = new Adt_gridAdapter(this, asanaKit.getAssanaList(),
                String.valueOf(asanaKit.getId()), this, presener);
        gridAdapter.setSelected(true);
        DragDropKit ddkit = new DragDropKit(this);
        gridAdapter.setDragDropListener(ddkit);
        gridAssan.setAdapter(gridAdapter);
        gridAssan.setOnDragListener(ddView);
        Adt_gridAdapter gridAdapterAll = new Adt_gridAdapter(this, asanaList, null, this, presener);
        gridAdapterAll.setDragDropListener(ddkit);
        gridAssanAll.setAdapter(gridAdapterAll);
        if(asanaKit.getId()==0){//Если пустой/новый набор, то открываем режим редактирования набора
            etLabelKit.setEnabled(true);
            etLabelKit.setText(R.string.title_kit);
            tbLock.setChecked(false);
        }
    }

    @Override
    public void addAssana(int index, String id) {
        //TODO: переписать через презентер
        Asana asana = controlYoga.getAssana(id,null); // Асана, которую нужно добавить в набор
        if(asanaKit.getId()==0){// Если новый набор, ещё не сохранён
            asanaKit.setId(controlYoga.addAssanaKit(asanaKit));
            idKit = asanaKit.getId();
        }
        if(asanaKit.getAssanaList().size()>index){
            asanaKit.getAssanaList().add(index, asana);
        } else asanaKit.getAssanaList().add(asana);
        asanaKit.setTitle(etLabelKit.getText().toString());
        controlYoga.updateAssanaKit(asanaKit);
        controlYoga.updateAssanaList(asanaKit);
        // Отображение данных
        ((Adt_gridAdapter)gridAssan.getAdapter()).setData(asanaKit.getAssanaList());
        ((Adt_gridAdapter)gridAssan.getAdapter()).notifyDataSetChanged();
    }

     //TODO: переписать: только отображение данных, обновить грид
    @Override
    public void deleteAssana(int index, String id) {
        asanaKit.getAssanaList().remove(index);
        //сохранить изменения в хранилище
        controlYoga.updateAssanaList(asanaKit);
        ((Adt_gridAdapter)gridAssan.getAdapter()).setData(asanaKit.getAssanaList());
        ((Adt_gridAdapter)gridAssan.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public Context context() {
        return this;
    }

    @Override
    public void showKit(AsanaKit kit) {
        asanaKit = kit;
        ((Adt_gridAdapter)gridAssan.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void changingKit(String idAsanaFrom, int indexFrom, String idAsanaTo, int indexTo) {
        asanaKit = presener.changingKitList(asanaKit, idAsanaFrom, indexFrom, idAsanaTo, indexTo);
        //Отобразить набор
        ((Adt_gridAdapter)gridAssan.getAdapter()).setData(asanaKit.getAssanaList());
        ((Adt_gridAdapter)gridAssan.getAdapter()).notifyDataSetChanged();
    }

    /**
     * Обработка смены состояния редактрования набора
     * Открываем/закрываем режим D&D для перетаскивания элементов набора
     */
    private class ChangeLock implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (tbLock.isChecked()){// Заблокиовано
                //Сохранить изменения(название, порядок следования элементов в наборе)
                asanaKit.setTitle(etLabelKit.getText().toString());
                controlYoga.updateAssanaKit(asanaKit);
                controlYoga.updateAssanaList(asanaKit);
                etLabelKit.setEnabled(false);
                gridAssan.setOnDragListener(null); //TODO: убрать после реализации перетаскивания асан
                gridAssanAll.setOnDragListener(null);//TODO: убрать после реализации перетаскивания асан
                ((Adt_gridAdapter)gridAssan.getAdapter()).setLock(true);
                ((Adt_gridAdapter)gridAssanAll.getAdapter()).setLock(true);
            }else{// Разрешено изменять
                etLabelKit.setEnabled(true);
                //Открыть драгдроп
                //splitterListener.setAssanaKit(asanaKit);
                //gridAssan.setOnDragListener(splitterListener);//TODO: убрать после реализации перетаскивания асан
                //gridAssanAll.setOnDragListener(splitterListener);//TODO: убрать после реализации перетаскивания асан
                ((Adt_gridAdapter)gridAssan.getAdapter()).setLock(false);
                ((Adt_gridAdapter)gridAssanAll.getAdapter()).setLock(false);
            }
        }
    }

    // Открывает экран просмотра асаны в режиме слайдшоу, т.е. с возможностью пролистывать все асаны набора
    private class KitSlideShow implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(asanaKit.getId()>0) {
                Intent intent = new Intent(KitActivity.this, AssanaActivity.class);
                intent.putExtra(AssanaActivity.KIT_ID, String.valueOf(asanaKit.getId()));
                startActivity(intent);
            }
        }
    }


    private class DragDropView implements View.OnDragListener{
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            final int action = dragEvent.getAction();
            //Log.d("DD_Image", "Grid addAsana:");
            switch (action){
                case DragEvent.ACTION_DROP:{
                    // Добавление асаны в набор
                    // Если асана которую тащили не из набора, и асана над которой бросили ИЗ набора, значит добавляем в набор
                    ClipData clipData = dragEvent.getClipData();
                    if(clipData.getItemAt(2).getText() == null){// Идентификатор набора == null, значит Асана не из набора, значит надо её добавить в набор
                        String idAsana = clipData.getItemAt(0).getText().toString();
                        //Log.d("DD_Image", "Grid addAsana:"+ ", id:"+idAsana);
                        addAssana(asanaKit.getAssanaList().size(), idAsana);
                        return false; // Выходим
                    }
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controlYoga.close();
    }
}
