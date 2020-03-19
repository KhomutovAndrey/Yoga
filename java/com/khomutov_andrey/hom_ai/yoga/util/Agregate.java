package com.khomutov_andrey.hom_ai.yoga.util;

import java.util.ArrayList;

/**
 * Created by hom-ai on 23.05.2017.
 * Класс агрегатор теекущего состояния (динамические данные) главной активности.
 */

public class Agregate {
    private Asana mAsana; // Выбранная (в главной активности) асана
    private ArrayList<Asana> mAsanaList, mAsanaTrainigList; // Списки ассан(общий список и список выбранных)

    public Agregate (ArrayList<Asana> asanaList, ArrayList<Asana> asanaTrainigList, Asana asana){
        mAsana = asana;
        mAsanaList = asanaList;
        mAsanaTrainigList = asanaTrainigList;
        if (asanaList ==null){
            asanaList = new ArrayList<Asana>();
        }
        if (asanaTrainigList ==null){
            asanaTrainigList = new ArrayList<Asana>();
        }
    }

    public Asana getmAsana() {
        return mAsana;
    }

    public void setmAsana(Asana mAsana) {
        this.mAsana = mAsana;
    }

    public ArrayList<Asana> getmAsanaList() {
        return mAsanaList;
    }

    public void setmAsanaList(ArrayList<Asana> mAsanaList) {
        this.mAsanaList = mAsanaList;
    }

    public ArrayList<Asana> getmAsanaTrainigList() {
        return mAsanaTrainigList;
    }

    public void setmAsanaTrainigList(ArrayList<Asana> mAsanaTrainigList) {
        this.mAsanaTrainigList = mAsanaTrainigList;
    }
}
