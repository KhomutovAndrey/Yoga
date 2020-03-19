package com.khomutov_andrey.hom_ai.yoga.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import com.khomutov_andrey.hom_ai.yoga.R;
import com.khomutov_andrey.hom_ai.yoga.db.DbHelper;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by hom-ai on 16.05.2017.
 * Управляем операциями обработки ассан в хранилище.
 * RESOURCES="res" - Ассаны в xml-файле ресурсов
 * DB="db"; - Ассаны в БД
 * v.2 Инициализация ассан в БД (добавление ассан из ресурса в БД, копирование
 * изображений в папку программы) при первом запуске.
 *
 */

public class ControlYoga {
    public static final String RESOURCES="res"; //Ассаны в xml-файле ресурсов
    public static final String DB="db"; //Ассаны в БД
    private DbHelper db;
    private Context mContext;
    private String mSource=RESOURCES; //Флаг тиа ресурса
    private ArrayList<Asana> assanList, asanaTrainigList; //Список ассан
    //private ArrayList<Asana> seletedAssana

    public ControlYoga(Context context, String source){
        this.mContext = context;
        assanList = new ArrayList<Asana>();
        mSource = source.equals(DB)? DB :RESOURCES;
        if(mSource.equals(DB)){
            db = new DbHelper(mContext);
        }
    }

    /**
     * Возвращает все ассаны из хранилища
     * @return ArrayList<Asana> - спиисок асан
     */
    public ArrayList<Asana> loadFromStorege(){
        switch(mSource){
            case RESOURCES:{
                LoaderFromRes saver = new LoaderFromRes(mContext);
                ArrayList<HashMap<String,String>> list = saver.getListAssan();
                // формируем список объектов Asana
                for(int i=0; i<list.size(); i++){
                    Asana asana = new Asana(list.get(i));
                    assanList.add(asana);
                }
                break;
            }
            case DB:{
                assanList = db.getAssans(null);
                break;
            }
            default: break;
        }
        return assanList;
    }

    /**
     * Проверяет есть ли новые асаны в ресурсах, и добавляет. если есть
     */
    public void checkUpdate(){
        LoaderFromRes loader = new LoaderFromRes(mContext);
        ArrayList<HashMap<String, String>> listAsanaMap; // Список атрибутов асан
        ArrayList<String> currentSets = db.getSets();// Имена наборов асан имеющихся в базе
        ArrayList<String> setsFromRes = loader.getListSet(); // имена наборов асан из ресурсов
        for (String nameSet:setsFromRes) {
            if(!currentSets.contains(nameSet)){
                listAsanaMap = loader.getAsansFromSet(nameSet);//Загрузить асаны из набора с именем nameSet
                //Сохранить загруженные из ресурсов асаны в хранилище
                for (HashMap<String, String> asanaMap: listAsanaMap) {
                    db.addAsana(asanaMap);
                }
                //Сохранить набор с именем nameSet в ханилище
                db.addSet(nameSet);
                //Log.d("listAsanaMap", String.valueOf(listAsanaMap.size()));
            }
        }
        //Проверить наборы
        ParserLoadKits parserKits = new ParserLoadKits(mContext, R.xml.kits);
        ArrayList<HashMap<String, ArrayList<String>>> kits = parserKits.getKitsMap();
        ArrayList<String> currentKits = db.getKitNames(null);
        ArrayList<String> asansList;
        for (HashMap<String, ArrayList<String>> kitMap: kits) {
            String name = kitMap.keySet().iterator().next();
            if(!currentKits.contains(name)){// Дошли до набора, которого нет в БД
                asansList = kitMap.get(name);
                //Добавить набор с асанами в БД
                db.addAsanaKit(name, asansList);
            }
        }
    }

    /**
     * Возвращает ассану по реквезитам
     * Если хранилище - БД, то поиск по id, если хранилище - ресурсы приложения, то поиск по uri
     * @param id идентификатор асаны в виде строки
     * @param uri адрес ресурса - изображение асаны
     * @return
     */
    public Asana getAssana(String id, String uri){
        Asana asana =null;
        switch(mSource){
            case RESOURCES:{
                if(uri==null || uri.isEmpty()){
                    return null;
                }
                for(int i=0; i<assanList.size(); i++){
                    if(assanList.get(i).getUri().equals(uri)){
                        asana = assanList.get(i);
                        return asana;
                    }
                }
                break;
            }
            case DB:{
                if(id==null || id.isEmpty() || id.equals("0")){
                    return null;
                }
                asana = db.getAssanaById(id);
                return asana;
            }
        }
        return asana;
    }

    public ArrayList<Asana> getAsans(String title){
        ArrayList<Asana> list = new ArrayList<Asana>();
        list = db.getAssans(title);
        return list;
    }

    public ArrayList<String> getAsanasTitle(String title){
        ArrayList<String> list;// = new ArrayList<String>();
        list = db.getAssansTitle(title);
        return list;
    }

    /**
     * Добавляет асану
     * @param asana - асана
     * @return
     */
    public long addAsana(Asana asana){
        if(asana==null){
            return 0;
        }
        String title = asana.getTitle();
        String title2 = asana.getTitle2();
        String sUri = asana.getUri();
        String time = String.valueOf(asana.getTime());
        String content = asana.getContent();
        String positive = asana.getPositive();
        String negative =asana.getNegative();
        String sl = asana.getSl();
        return db.addAsana(title, title2, sUri, time, content, positive, negative, sl);
    }

    /**
     * Обновление асаны. Сохраняет значения из асаны, переданной в параметре, по её id
     * @param asana - асана
     * @return
     */
    public long updateAsana(Asana asana){
        if(asana==null){
            return 0;
        }
        String id_asana = String.valueOf(asana.getId());
        String title = asana.getTitle();
        String title2 = asana.getTitle2();
        String sUri = asana.getUri();
        String time = String.valueOf(asana.getTime());
        String content = asana.getContent();
        String positive = asana.getPositive();
        String negative =asana.getNegative();
        String sl = asana.getSl();
        return db.updateAsana(id_asana, title, title2, sUri, time, content, positive, negative, sl);
    }

    /**
     * Возвращает количество наборов, содержащих указанную асану
     * @param asana
     * @return
     */
    public int getCountKitsByAsana(Asana asana){
        int count =0;
        count = db.getCountKitsByAsanaId(asana.getId());
        return count;
    }

    /**
     * Удаляет асану по её идентификатору
     * @param asana - асана
     * @return
     */
    public int deleteAsana(Asana asana){
        return db.deleteAsanaById(asana.getId());
    }

    /**
     * Добавляет набор ассан
     * @param asanaKit - Набор асан
     * @return
     */
    public long addAssanaKit(AsanaKit asanaKit){
        if(asanaKit ==null){
            return -1;
        }
        long assanaKit_id = db.addAssanaKit(asanaKit.getTitle(), asanaKit.getSl());
        ArrayList<String> assansID = new ArrayList();
        ArrayList<Asana> list = asanaKit.getAssanaList();
        for (Asana asana :list) {
            assansID.add(String.valueOf(asana.getId()));
        }
        db.addAssanaList(String.valueOf(assanaKit_id),assansID);
        return assanaKit_id;
    }

    // Добляет набор ассан
    public void addAssanaKit(String title, String sl, ArrayList<Asana> asanaList){
        if (asanaList == null || asanaList.isEmpty()){
            return;
        }
        long assanaKit_id = db.addAssanaKit(title, sl);
        for(int i = 0; i< asanaList.size(); i++){
            long idAssana = asanaList.get(i).getId();
            db.addToKitList(String.valueOf(idAssana), String.valueOf(assanaKit_id));
        }
    }

    /**
     * Возвращает наборы ассан
     * @return
     */
    public ArrayList<AsanaKit> getKits(){
        ArrayList<AsanaKit> asanaKits = new ArrayList<>();
        asanaKits = db.getKits(null, null); //05.07.2017
        return asanaKits;
    }

    /**
     * Возвращяет набор асан по его идентификатору
     * @param id - идентификатор асан
     * @return
     */
    public AsanaKit getKitById(String id){
        if(id==null || id.isEmpty()|| id.equals("0")){
            return null;
        }
        return db.getKitById(id);
    }

    public void clearAssanaKit(AsanaKit asanaKit){
        db.clearKitList(String.valueOf(asanaKit.getId()));
    }

    public void updateAssanaKit(AsanaKit asanaKit){
        db.updateAssanaKit(String.valueOf(asanaKit.getId()), asanaKit.getTitle());
    }

    public void deleteAssanaKit(AsanaKit asanaKit){
        db.deleteKit(String.valueOf(asanaKit.getId()));
    }

    // Обновляет список асан указанного набора
    public void updateAssanaList(AsanaKit asanaKit){
        clearAssanaKit(asanaKit);
        ArrayList<String> assansID = new ArrayList();
        ArrayList<Asana> list = asanaKit.getAssanaList();
        for (Asana asana :list) {
            assansID.add(String.valueOf(asana.getId()));
        }
        db.addAssanaList(String.valueOf(asanaKit.getId()),assansID);
    }

    public void close(){
        db.close();
    }

    public String getDefoultImage(){
        String path="";
        int resId = mContext.getResources().getIdentifier("ic_launcher","mipmap", mContext.getPackageName());
        Resources res= mContext.getResources();

        Uri mUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+
                res.getResourcePackageName(resId)+"/"+res.getResourceTypeName(resId)+"/"+
                res.getResourceEntryName(resId));
        path = mUri.toString();
        return path;
    }

}
