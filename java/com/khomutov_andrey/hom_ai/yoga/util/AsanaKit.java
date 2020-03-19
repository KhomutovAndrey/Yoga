package com.khomutov_andrey.hom_ai.yoga.util;

import com.khomutov_andrey.hom_ai.yoga.ui_sapport.IndicatorPosition;

import java.util.ArrayList;

/**
 * Created by hom-ai on 06.06.2017.
 * Класс описывает набор(kit) из ассан, для тренировки
 * Набор - Занятие из одной или нескольких ассан
 */

public class AsanaKit implements IteratorAsana {
    private long mId;
    private String mTitle;
    private String mSl;
    private ArrayList<Asana> mAsanaList; // Список ассан, составляющих набор
    private int position; // Позиция текущей\выбранной ассаны (для итерации)
    private IndicatorPosition mIndicator; // Для оповещения UI-индикатора, о том, что текущая позиция сменилась

    public AsanaKit(long id, String title, String sl, ArrayList<Asana> asanaList) {
        mId = id;
        mTitle = title;
        mSl = sl;
        position = 0;
        if (asanaList == null) {
            asanaList = new ArrayList<Asana>();
        }
        mAsanaList = asanaList;
    }

    public AsanaKit(Asana asana){
        if (asana !=null){
            mId = 0;
            mTitle = "";
            mSl = "1";
            position = 0;
            ArrayList<Asana> asanaList = new ArrayList<Asana>();
            asanaList.add(asana);
            this.mAsanaList = asanaList;
        }
    }

    public ArrayList<Asana> getAssanaList() {
        return mAsanaList;
    }

    public void setAssanaList(ArrayList<Asana> asanaList) {
        if (asanaList == null) {
            asanaList = new ArrayList<Asana>();
        }
        this.mAsanaList = asanaList;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getSl() {
        return mSl;
    }

    public void setSl(String sl) {
        this.mSl = sl;
    }

    public void setId(Long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }

    public long getCurrentAssanaId() {
        return mAsanaList.get(position).getId();
    }

    public void setIndicator(IndicatorPosition indicator){
        mIndicator = indicator;
    }

    @Override
    public boolean hasNext() {
        if (position < mAsanaList.size()-1) {
            return true;
        }
        return false;
    }

    @Override
    public int getCurrentPosition() {
        return position;
    }

    public boolean hasPrev() {
        if (position > 0 && position < mAsanaList.size()) {
            return true;
        }
        return false;
    }

    @Override
    public Object next() {
        if(position >= mAsanaList.size()-1){
            return null;
        }
        position++;
        //return mAsanaList.get(position - 1);
        if(mIndicator!=null)mIndicator.change();
        return mAsanaList.get(position);
    }

    public Object prev() {
        if (hasPrev()) {
            position--;
            if(mIndicator!=null)mIndicator.change();
            return mAsanaList.get(position);
        } else return null;
    }

    @Override
    public Object first() {
        if(mAsanaList!=null && mAsanaList.size()>0){
            position = 0;
            if(mIndicator!=null)mIndicator.change();
            return mAsanaList.get(0);
        }else
            return null;
    }

    @Override
    public Object getObjectAT(int index) {
        int size = mAsanaList.size();
        if(mAsanaList!=null && size>0){
            if(size>index && index>=0){
                return mAsanaList.get(index);
            }
        }
        return null;
    }

    @Override
    public int getSize() {
        return mAsanaList.size();
    }

    @Override
    public void remove() {
        mAsanaList.remove(position);
    }
}
