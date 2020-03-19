package com.khomutov_andrey.hom_ai.yoga.util;

import android.content.Context;
import com.khomutov_andrey.hom_ai.yoga.db.SaveToDataBase;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Andrey on 21.10.2017.
 * Класс загружает наборы асан (Kits), из источника (внешнего, или вложенных)
 * Сохраняет наборы в БД, таблицы TABLE_KIT ="kit" - описание набора, и TABLE_KIT_LIST ="kit_list" - список асан в наборе
 * Описание наборов с асанами загружается в виде XML-файла.
 *<kits>
 *  <kit title="Для сна">
 *      <asana uri="uttanasana"/>
 *      <asana uri="pashch"/>
 *      <asana uri="shavasana"/>
 *  </kit>
 *</kits>
 * Формируем список набора, содержащий в себе список значений uri(ресурс асаны) для набора.
 *
 */

public class LoaderKits {

    private ArrayList<KitFromXml> kits;
    private Context mContext;
    public static final String RESOURCES="res";
    public static final String URI="uri";
    private int mId; //Идентификатор локального ресурса
    private SaveToDataBase saveToDataBase;

    /**
     *
     * @param context
     * @param resources - указывается откуда берётся файл-описание наборов:
     *                  RESOURCES="res" - локальный файл ресурсов
     *                  URI="uri" - загружается из сети
     * @param id - идентификатор ресурса (), в случае, если читается локальный ресурс
     */
    public LoaderKits(Context context, String resources, int id){
        this.mContext = context;
        this.mId = id;
        kits = new ArrayList<>();
    }

    private void parsXmlFromResource(){
        KitFromXml kit=null;
        ArrayList<String> list = new ArrayList<String>();
        XmlPullParser parcer = mContext.getResources().getXml(mId);
        try {
            while (parcer.getEventType()!=XmlPullParser.END_DOCUMENT){
                // Находим тэг <kit>, описывающий набор(через атрибут title),  и содержащий вложенные тэги асан
                if (parcer.getEventType()==XmlPullParser.START_TAG && parcer.getName().equals("kit")) {
                    String title = parcer.getAttributeValue(0); //Получаем название набора
                    kit = new KitFromXml(title, null);
                    kits.add(kit);
                }
                    //находим тег <asana>
                    if (parcer.getEventType()==XmlPullParser.START_TAG && parcer.getName().equals("asana")){
                        //Добавляем считаную асану в набор
                        String uri = "android.resource://com.khomutov_andrey.hom_ai.yoga/drawable/"+parcer.getAttributeValue(0);
                        kit.getListUri().add(uri);// Заполняем uri асаны
                        kits.set(kits.size()-1,kit); // Вставляем обновлённый набор на его индекс в списке
                    }
                parcer.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSaver(SaveToDataBase saver){
        this.saveToDataBase = saver;
    }

    public void save(){
        parsXmlFromResource();
        if (saveToDataBase!=null){
            KitFromXml kit;
            for (int i=0; i<kits.size(); i++){
                kit = kits.get(i);
                saveToDataBase.addAsanaKit(kit.getTitle(), kit.getListUri());
            }
        }
    }

    //TODO: Скрыть в приватный, или убрать. Метод для проверки
    public ArrayList<KitFromXml> getKits(){
        parsXmlFromResource();
        return kits;
    }


    private class KitFromXml{
        private String title;
        private ArrayList<String> listUri;

        public KitFromXml(String title, ArrayList<String> list){
            this.title = title;
            if(list==null){
                this.listUri = new ArrayList<String>();
            } else this.listUri = list;
            //this.listUri = (list==null)? list : new ArrayList<String>();
            //this.listUri = list;
        }

        public String getTitle(){
            if(title!=null){
                return title;
            } else return"";
        }

        public ArrayList<String> getListUri(){
            return listUri;
        }
    }
}
