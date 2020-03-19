package com.khomutov_andrey.hom_ai.yoga.adapters;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.khomutov_andrey.hom_ai.yoga.AssanaActivity;
import com.khomutov_andrey.hom_ai.yoga.ControllerAssanaToKit;
import com.khomutov_andrey.hom_ai.yoga.util.Asana;
import com.khomutov_andrey.hom_ai.yoga.R;
import com.khomutov_andrey.hom_ai.yoga.util.AssanDisplayer;
import com.khomutov_andrey.hom_ai.yoga.util.DragDropKit;
import com.khomutov_andrey.hom_ai.yoga.util.PresenterInterface;

import java.util.ArrayList;

/**
 * Created by hom-ai on 17.05.2017.
 * Адаптер для GridView, с поддержкой перемещения асаны ерез D&D
 */

public class Adt_gridAdapter extends BaseAdapter implements ControllerAssanaToKit {
    private Context mContext;
    private Asana mAsana;
    private ArrayList<Asana> mData = new ArrayList<Asana>();
    private int currentPosition = 0;
    private String mSeleted = "";
    String mKitId = null;
    String TAG = "drag&drop";
    private Drawable baseColorID;
    private View.OnDragListener ddListener = null;
    PresenterInterface.IKitView mPresenterView;
    PresenterInterface.IKitPre mPresenter;
    private boolean lock = true; // Разрешено/запрещено перемещать асаны

    public Adt_gridAdapter(Context c, ArrayList<Asana> data, String kitId, PresenterInterface.IKitView presenterView, PresenterInterface.IKitPre presenter) {
        mContext = c;
        mData = data;
        mKitId = kitId;
        mPresenterView = presenterView;
        mPresenter = presenter;
        //ddListener = new DragDropListener(this);
        //ddListener = new DragDropKit(this);
    }

    public void setDragDropListener(View.OnDragListener ddListener){
        this.ddListener = ddListener;
    }


    //TODO: убрать обрабтчик набора?
    // Помечает как выбранную, т.е. перенесённую в блок добавленных
    public void setSelected(boolean selected) {
        mSeleted = selected ? "selected" : "";
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public void setData(ArrayList<Asana> data) {
        mData = data;
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setCurrent(int position) {
        if (position > 0 && position < mData.size()) {
            currentPosition = position;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View grid;
        mAsana = mData.get(position);
        if (convertView == null) {
            //grid = new View(mContext); 16.11.2017
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = inflater.inflate(R.layout.adt_grid_item, parent, false);
            baseColorID = grid.getBackground();
        } else {
            grid = (View) convertView;
            baseColorID = grid.getBackground();
        }

        final ImageView image = (ImageView) grid.findViewById(R.id.image_assana);
        TextView text = (TextView) grid.findViewById(R.id.text_assana);
        image.setOnDragListener(ddListener);

        //Настройка Drag&Drop для image
        image.setTag(R.id.tag_asana_id, String.valueOf(mAsana.getId()));
        image.setTag(R.id.tag_position, position);
        image.setTag(R.id.tag_kit_id, mKitId);

        //image.setOn
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(lock){
                    return false;
                }
                //Log.d(TAG,"onToach");
                // Получаем ассану по значению position - индекс нажатого элемента в контейнере
                //mAsana = mData.get(position);
                ClipData clipData;
                Asana asana = mData.get(position);
                clipData = ClipData.newPlainText("id", String.valueOf(asana.getId())); //Идентификатор асаны
                Log.d("DD_Image", "LongClick: id:"+String.valueOf(asana.getId()));
                String id = (String) v.getTag();
                ClipData.Item item = ClipData.newPlainText("uri", mAsana.getUri()).getItemAt(0);
                clipData.addItem(item);
                item = ClipData.newPlainText("kitID", mKitId).getItemAt(0); // Идентификатор набора
                clipData.addItem(item);
                item = ClipData.newPlainText("index", String.valueOf(position)).getItemAt(0); // Позиция элемента в сонтейнере
                clipData.addItem(item);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(image);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(clipData, shadowBuilder, null, View.DRAG_FLAG_GLOBAL);
                } else
                    v.startDrag(clipData, shadowBuilder, null, View.DRAG_FLAG_GLOBAL);
                //Log.d(TAG, "LongClick:"+clipData.toString() + " uri:"+mAsana.getUri());
                return false;
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AssanaActivity.class);
                if (mKitId != null && !mKitId.equals("0") && !mKitId.isEmpty()) {
                    intent.putExtra(AssanaActivity.KIT_ID, mKitId);
                }else {
                    Asana asana = mData.get(position);
                    intent.putExtra(AssanaActivity.ASANA_ID, String.valueOf(asana.getId()));
                }
                mContext.startActivity(intent);
            }
        });

        //Вывод изображения
        new AssanDisplayer(mContext, mAsana, image).display(null);
        text.setText(mAsana.getTitle());
        return grid;
    }

    /**
     * Переключает режим редактирования позиций асан. Включает\отключает режим D&D
     * @param lock - ключ режима редактирования (возможности перетаскивать асаны в наборе)
     * @return ключ режима редактирования
     */
    public boolean setLock(boolean lock){
        this.lock = lock;
        return this.lock;
    }

    @Override
    public void addAssana(int index, String id) {

    }

    @Override
    public void deleteAssana(int index, String id) {
        mData.remove(index);
        mPresenter.updDateAsanaList(mKitId, mData);
        this.notifyDataSetChanged();
    }

}
