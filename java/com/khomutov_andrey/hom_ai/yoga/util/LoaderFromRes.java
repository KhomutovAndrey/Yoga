package com.khomutov_andrey.hom_ai.yoga.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.khomutov_andrey.hom_ai.yoga.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hom-ai on 26.04.2017.
 * Возвращает список асан из XML-фала ресурсов
 * Сохраняет ресурсы изображений из архива приложения в папку приложения (пока не используется)
 */

public class LoaderFromRes {
    private final Context mContext;
    private HashMap<String, String> mForAssan; // Контейнер, хранящий значения полей ассыны (class Assan)
    // Значение ключа в контейнере mForAssan, хранящем имя ресурса.
    // Используется для извлечения имени, при сохранении на карту.
    private final String URI_FROM_XML = "uri";
    private ArrayList<HashMap<String, String>> mListAssan; // Список значений ассан (mForAssan), формируется чтением и разбором Xml-ресурса

    /**
     * Конструктор.
     *
     * @param context
     */
    public LoaderFromRes(Context context) {
        mContext = context;
        mListAssan = new ArrayList<HashMap<String, String>>();
    }

    /**
     * Формирует список значений всех асан из файла-ресурса
     *
     * @return возвращает экземпляр LoaderFromRes
     */
    public LoaderFromRes loadAsana() {
        XmlPullParser parser = mContext.getResources().getXml(R.xml.assan);
        try {// Читаем Xml и формируем список значений полей Ассан
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                //int i = parser.getEventType(); // START_TAG = 2; END_TAG = 3;
                //String pars = parser.getName();
                if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("asana")) {
                    mForAssan = new HashMap<String, String>();
                    mForAssan.put("idAssana", "0"); // id
                    mForAssan.put("title", parser.getAttributeValue(null, "title")); // title
                    mForAssan.put("title2", parser.getAttributeValue(null, "title2")); // title2
                    mForAssan.put("uri", parser.getAttributeValue(null, "uri")); // uri
                    mForAssan.put("time", parser.getAttributeValue(null, "time")); // time
                    mForAssan.put("content", parser.getAttributeValue(null, "content")); // content
                    mForAssan.put("positive", parser.getAttributeValue(null, "positive")); // positive
                    mForAssan.put("negative", parser.getAttributeValue(null, "negative")); // negative
                    mForAssan.put("sl", parser.getAttributeValue(null, "sl")); // sl
                    mListAssan.add(mForAssan);
                    //mForAssan.clear();
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Возвращает список имён "Спиков асан" из ресурса
     *
     * @return
     */
    public ArrayList<String> getListSet() {
        ArrayList<String> list = new ArrayList<String>();
        //Считать из XML с асанами имена списков асан
        XmlPullParser parser = mContext.getResources().getXml(R.xml.assan);
        String nameSet = "";
        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("set")) {
                    nameSet = parser.getAttributeValue(null, "uniqName");
                    list.add(nameSet);
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Возвращает список асан (в виде наборов атрибутов)по имени списка(в файле ресурса)
     *
     * @return Список асан, ArrayList<HashMap<String, String>>(), список наборов значения параметров каждой асаны
     */
    public ArrayList<HashMap<String, String>> getAsansFromSet(String nameSet) {
        if (mListAssan != null) {
            mListAssan.clear();
        } else mListAssan = new ArrayList<HashMap<String, String>>();
        // Загрузить асаны из набора
        XmlPullParser parser = mContext.getResources().getXml(R.xml.assan);
        String nameSetFromRes="";
        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if(parser.getEventType() == XmlPullParser.START_TAG) {
                    /**
                    //Удалить после проверки считывания второго наора
                    if(name.equals("asana")){
                        flag++;
                    }
                    **/
                    if(parser.getName().equals("set")){
                        nameSetFromRes = parser.getAttributeValue(null,"uniqName");
                        parser.next();
                    }
                    if(parser.getName().equals("asana") && nameSetFromRes.equals(nameSet)){
                        mForAssan = new HashMap<String, String>();
                        mForAssan.put("idAssana", "0"); // id
                        mForAssan.put("title", parser.getAttributeValue(null, "title")); // title
                        mForAssan.put("title2", parser.getAttributeValue(null, "title2")); // title2
                        mForAssan.put("uri", "android.resource://com.khomutov_andrey.hom_ai.yoga/drawable/" + parser.getAttributeValue(null, "uri")); // uri
                        mForAssan.put("time", parser.getAttributeValue(null, "time")); // time
                        mForAssan.put("content", parser.getAttributeValue(null, "content")); // content
                        mForAssan.put("positive", parser.getAttributeValue(null, "positive")); // positive
                        mForAssan.put("negative", parser.getAttributeValue(null, "negative")); // negative
                        mForAssan.put("sl", parser.getAttributeValue(null, "sl")); // sl
                        mListAssan.add(mForAssan);
                    }
                }
                if(parser.getEventType() == XmlPullParser.END_TAG){
                   if (parser.getName().equals("set") && nameSetFromRes.equals(nameSet) ){ // Конец тэга set
                       return mListAssan;
                   }
                }
                parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mListAssan;
    }

    /**
     * Копирует изображения из ресурсов в каталог приложения, и возвращает список значений ассан
     *
     * @param dir - Строковое значение каталога программы
     * @return Список ассан в виде списка значений полей для каждой ассаны
     */
    public ArrayList<HashMap<String, String>> load(String dir) {//
        //TODO: Внутренний каталог (проверить работоспособность когда будет реализована загрузка изображений для отображения)
        //String DIR = mContext.getFilesDir().toString(); // Внутренний каталог
        //TODO: Проверить как отработает на телефоне без карты
        String DIR = mContext.getExternalFilesDir(null).getAbsolutePath(); // Каталого на внешней карте
        //TODO: Раскрыть, кгода будет вызов из экрана заставки, каталог передаётся в параметре
        //final String DIR=dir; // Каталог программы

        // Сохраняемфайлы в каталог программы
        for (int i = 0; i < mListAssan.size(); i++) {
            StringBuilder fileName = new StringBuilder(); // Полное имя файла ресурса для сохранения в каталоге
            String nameResource = mListAssan.get(i).get(URI_FROM_XML); // имя ресурса
            fileName.append(DIR).append(File.separator).append(nameResource).append(".jpg"); // Полное имя файла
            // Идентификатор ресурса
            int idResource = mContext.getResources().
                    getIdentifier(nameResource, "drawable", "com.khomutov_andrey.hom_ai.yoga");
            // Получаем изображение
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), idResource);
            File file = new File(fileName.toString());
            try { // Сохраняем в файл
                OutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mListAssan;
    }

    /**
     * Возвращает список асан (асаны представлены в виде контейнера с именем параметра и его значения для)
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getListAssan() {
        return mListAssan;
    }

}
