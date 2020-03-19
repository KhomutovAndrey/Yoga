package com.khomutov_andrey.hom_ai.yoga;

import android.content.Context;

import com.khomutov_andrey.hom_ai.yoga.adapters.Adt_gridAdapter;
import com.khomutov_andrey.hom_ai.yoga.util.Asana;
import com.khomutov_andrey.hom_ai.yoga.util.AsanaKit;
import com.khomutov_andrey.hom_ai.yoga.util.ControlYoga;
import com.khomutov_andrey.hom_ai.yoga.util.PresenterInterface;

import java.util.ArrayList;

public class KitActivityPre implements PresenterInterface.IKitPre {
    Context mContext;
    ControlYoga controlYoga;
    PresenterInterface.IKitView mKitView;

    public KitActivityPre(PresenterInterface.IKitView activityKit){
        mContext = activityKit.context();
        controlYoga = new ControlYoga(mContext, "db");
    }

    private ControlYoga getControlYoga(){
        if(controlYoga == null){
            return controlYoga = new ControlYoga(mContext, "db");
        } else return controlYoga;
    }

    @Override
    public void updDateAsanaList(String idKit, ArrayList<Asana> asanaList) {
        //controlYoga = getControlYoga();
        AsanaKit kit = controlYoga.getKitById(idKit);
        kit.setAssanaList(asanaList);
        controlYoga.updateAssanaList(kit);
    }

    @Override
    public void upDateKit(AsanaKit kit) {
        getControlYoga().updateAssanaList(kit);
    }

    @Override
    public AsanaKit changingKitList(AsanaKit kit, String idAsanaFrom, int indexFrom, String idAsanaTo, int indexTo) {
        controlYoga = getControlYoga();
        Asana asana = controlYoga.getAssana(idAsanaFrom, null);
        AsanaKit asanaKit = kit;
        if(indexTo < indexFrom){
            asanaKit.getAssanaList().remove(indexFrom);
            asanaKit.getAssanaList().add(indexTo,asana);
        }
        if(indexTo - 1 > indexFrom){// Исключаем, когда асана перемещается на соседнее справа место
            asanaKit.getAssanaList().add(indexTo,asana);
            asanaKit.getAssanaList().remove(indexFrom);
        }
        if(indexTo-1 == indexFrom){ // Асана перемещается на соседнее справа место
            asanaKit.getAssanaList().add(indexTo+1,asana);
            asanaKit.getAssanaList().remove(indexFrom);
        }
        controlYoga.updateAssanaList(asanaKit);
        return asanaKit;
    }
}
