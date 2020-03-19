package com.khomutov_andrey.hom_ai.yoga.util;

import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;

import com.khomutov_andrey.hom_ai.yoga.ControllerAssanaToKit;
import com.khomutov_andrey.hom_ai.yoga.R;

public class DragDropKit implements View.OnDragListener {
    private View viewParent; // Контейнер в котором лежит имадж асаны
    private PresenterInterface.IKitView mControllerKit;
    private Drawable baseColorID;

    public DragDropKit(PresenterInterface.IKitView controllerKit){
        super();
        mControllerKit = controllerKit;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        //Log.d("ActionLog",String.valueOf(dragEvent.getAction()));
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
            {
                baseColorID = ((View)v.getParent()).getBackground();
                return true;
            }
            case DragEvent.ACTION_DRAG_EXITED: {
                Log.d("ActionLog","ACTION_DRAG_EXITED");
                viewParent = (View) v.getParent();
                viewParent.setBackground(baseColorID);
                return true;//
            }
            case DragEvent.ACTION_DRAG_LOCATION: {
                Log.d("ActionLog","ACTION_DRAG_LOCATION");
                viewParent = (View) v.getParent();
                viewParent.setBackgroundColor(v.getContext().getResources().getColor(R.color.colorPrimary));
                //viewParent.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                return true;//
            }
            case DragEvent.ACTION_DROP: {
                Log.d("ActionLog","ACTION_DROP");
                viewParent = (View) v.getParent();
                viewParent.setBackground(baseColorID);
                // Идентификатор набора указывает, что перемещается асана в наборе, если null значит асана перемещается из общего списка
                ClipData clipData = event.getClipData();
                int indexFrom = Integer.decode(clipData.getItemAt(3).getText().toString()); // Позиция асаны которую тащим
                int indexTo = (Integer)v.getTag(R.id.tag_position); // Позиция асаны на которую переместили
                String kitIdTarget = (String)v.getTag(R.id.tag_kit_id);// Идентификатор набора у имеджа над которым отпустили
                // Удаление асаны из набора
                // Если асана над которой бросили не из набора, а асана которую бросили ИЗ набора, значит удаляем
                if(kitIdTarget == null && clipData.getItemAt(2).getText() != null){// Отпустили над имеджем полного списка асан, т.е. удалить асану из набора
                    Log.d("ActionLog", "delete position:"+String.valueOf(indexFrom));
                    mControllerKit.deleteAssana(indexFrom, null);
                    return false; // Выходим
                }
                // Добавление асаны в набор
                // Если асана которую тащили не из набора, и асана над которой бросили ИЗ набора, значит добавляем в набор
                if(clipData.getItemAt(2).getText() == null && kitIdTarget!=null){// Идентификатор набора == null, значит Асана не из набора, значит надо её добавить в набор
                    String idAsana = clipData.getItemAt(0).getText().toString();
                    Log.d("DD_Image", "Add positionTo:"+String.valueOf(indexTo)+ ", id:"+idAsana);
                    mControllerKit.addAssana(indexTo, idAsana);
                    return false; // Выходим
                }
                // Перемещается асана внутри набора, переставить асану
                // Если асаны которую тащим и над которой отпустили обе из набора, тогда перемещаем
                if(clipData.getItemAt(2).getText() != null && kitIdTarget!=null){
                    String idAsanaFrom = clipData.getItemAt(0).getText().toString();
                    String idAsanaTo = (String)v.getTag(R.id.tag_asana_id);
                    mControllerKit.changingKit(idAsanaFrom, indexFrom, idAsanaTo, indexTo);
                    return false;
                }

                return false;//
            }
        }
        return true;
    }

}
