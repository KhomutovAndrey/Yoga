package com.khomutov_andrey.hom_ai.yoga.util;

import android.view.View;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created by hom-ai on 07.07.2017.
 * Определяем индекс визуальноко компанента в контейнере.
 * Требуется при постановке элемента на определённое место
 */

public class IndexChildFromView {
    private View mView;
    private float x,y;

    public IndexChildFromView(){
    }

    private boolean coordinatInnerView(View view, float x, float y){
        boolean result=false;
        //Проверяем координату X
        if(x>=view.getX() && x<=view.getX()+view.getWidth() && y>=view.getY() && y<=view.getY()+view.getWidth()){
            return true;
        }
        return result;
    }

    public int get(View view, float coordinatX, float coordinatY){
        int index=0;
        mView = view;
        x = coordinatX;
        y = coordinatY;

        String viewClassName = mView.getClass().getSimpleName();
        View child;
        switch (viewClassName){
            case "GridView":{
                //Получаем элементы по идексу расположения в контейнере, а не по индексу в списке,
                // что бы не закрывать возможность реагирования на нажатие компанентам внутри элемента списка (внутри грида)
                GridView gridView = (GridView)view;
                for(int i=0; i<gridView.getChildCount(); i++){
                    child = gridView.getChildAt(i);
                    if(coordinatInnerView(child,x,y)){
                        return i;
                    }
                }
                return -1;
            }
            case "LinearLayout": {
                LinearLayout linearLayout = (LinearLayout)mView;
                for(int i=0; i<linearLayout.getChildCount(); i++){
                    child = linearLayout.getChildAt(i);
                    if(x<=child.getX()+child.getWidth()){
                        index = i;
                        return index;
                    }else index = i+1;
                }
                return index;
            }
            case "HorizontalScrollView":{
                HorizontalScrollView scrollView = (HorizontalScrollView)mView;
                LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);
                for(int i=0; i<linearLayout.getChildCount(); i++){
                    child = linearLayout.getChildAt(i);
                    if(x<=child.getX()+child.getWidth()){
                        index = i;
                        return index;
                    }else index = i+1;
                }
                return index;
            }
        }
        return index;
    }
}
