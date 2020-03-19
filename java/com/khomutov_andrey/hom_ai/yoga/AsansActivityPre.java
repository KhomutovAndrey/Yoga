package com.khomutov_andrey.hom_ai.yoga;

import android.content.Context;
import com.khomutov_andrey.hom_ai.yoga.util.Asana;
import com.khomutov_andrey.hom_ai.yoga.util.ControlYoga;
import com.khomutov_andrey.hom_ai.yoga.util.PresenterInterface;
import java.util.ArrayList;

public class AsansActivityPre implements PresenterInterface.IAsansPre {
    PresenterInterface.IAsansView mAsanasView;// Для управления экраном отображения
    //Asana mAsana;
    ArrayList<Asana> mAsanalist;
    ControlYoga mControl;
    Context mContext;
    String query;

    public AsansActivityPre(PresenterInterface.IAsansView asanasView) {
        this.mAsanasView = asanasView;
        mContext = asanasView.getContext();
        mControl = new ControlYoga(mContext, "db");
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        //Log.d("SearchView_",s);
        query = s;
        //Toast.makeText(mContext, "sbt:"+query+":", Toast.LENGTH_SHORT).show();
        mAsanalist = mControl.getAsans(s);
        mAsanasView.showAsans(mAsanalist);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        //Log.d("SearchView_",s);
        query = s;
        if(s.length()<1){
            onQueryTextSubmit("");
        }
        ArrayList<String> titles = mControl.getAsanasTitle(query);
        //TODO: сформировать адаптер для отображения вариантов у элемента поиска
        //mAsanasView.showListSearch(titles);

        //Log.d("QueryTextch", titles.toString());
        //Toast.makeText(mContext, "chg:"+query+":", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onClose() {
        onQueryTextSubmit(null);
        //Toast.makeText(mContext, "cls:"+query+":", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public ArrayList<Asana> queryAsanasList(String title) {
        //Toast.makeText(mContext, title, Toast.LENGTH_SHORT).show();
        mAsanalist = mControl.getAsans(title);
        mAsanasView.showAsans(mAsanalist);
        return mAsanalist;
    }
}
