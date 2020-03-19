package com.khomutov_andrey.hom_ai.yoga.util;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ParserLoadKits {

    private Context mContext;
    private ArrayList<HashMap<String, ArrayList<String>>> kits;
    //private ArrayList<String> kitNames;
    //private ArrayList<String> kitURIs;
    private XmlPullParser parcer;
    private int resId;


    public ParserLoadKits(Context context, int resId){
        this.mContext = context;
        this.resId =resId;
        parcer = mContext.getResources().getXml(resId);
    }

    public XmlPullParser parser(){
        if (parcer == null){
            parcer = mContext.getResources().getXml(resId);
        }
        return parcer;
    }

    public ArrayList<HashMap<String, ArrayList<String>>> getKitsMap(){
        kits = new ArrayList<>();
        ArrayList<String> kitURIs = new ArrayList<>();
        String kitTitle="";
        try {
            while (parcer.getEventType()!=XmlPullParser.END_DOCUMENT){
                // Находим тэг <kit>, описывающий набор(через атрибут title),  и содержащий вложенные тэги асан
                if (parcer.getEventType()==XmlPullParser.START_TAG && parcer.getName().equals("kit")) {
                    kitTitle = parcer.getAttributeValue(0); //Получаем название набора
                    kitURIs = new ArrayList<>();
                }
                //находим тег <asana>
                if (parcer.getEventType()==XmlPullParser.START_TAG && parcer.getName().equals("asana")){
                    //Добавляем считаную асану в набор
                    String uri = "android.resource://com.khomutov_andrey.hom_ai.yoga/drawable/"+parcer.getAttributeValue(0);
                    kitURIs.add(uri);
                }
                if (parcer.getEventType()==XmlPullParser.END_TAG && parcer.getName().equals("kit")){
                    HashMap<String, ArrayList<String>> kitMap = new HashMap<>();
                    kitMap.put(kitTitle, kitURIs);
                    kits.add(kitMap);
                }
                parcer.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kits;
    }

}
