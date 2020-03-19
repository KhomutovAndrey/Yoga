package com.khomutov_andrey.hom_ai.yoga.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;

import com.khomutov_andrey.hom_ai.yoga.util.Asana;
import com.khomutov_andrey.hom_ai.yoga.util.AsanaKit;
import com.khomutov_andrey.hom_ai.yoga.util.LoaderFromRes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hom-ai on 26.04.2017.
 */

public class DbHelper extends SQLiteOpenHelper implements SaveToDataBase {
    private static final String DATABASE_NAME = "yoga.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_ASSANA = "assana"; // Ассаны
    private static final String TABLE_KIT = "kit"; // Набор - Занятие из одной или нескольких ассан
    private static final String TABLE_KIT_LIST = "kit_list"; // Список ассан одного набора
    private static final String TABLE_TRAIN = "train"; // Тренировки, содержат набор ассан, дату, название
    private static final String TABLE_SETS = "sets"; // Имена списков асан из ресурсов. Техническая талица, для синхрнизации добавлений новых асан при обновлении приложения (asans, asans2, ...)
    private Context mContext;
    private SQLiteDatabase db;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        db = getWritableDatabase();
    }

    /**
     * Создаёт необходимые таблицы, копирует данные из xml-ресурса в БД
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_ASSANA + "(id INTEGER PRIMARY KEY, title TEXT, title2 TEXT, uri TEXT, time TEXT, content TEXT, positive TEXT, negative TEXT, sl TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_KIT + "(id INTEGER PRIMARY KEY, title TEXT, sl TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_KIT_LIST + "(id INTEGER PRIMARY KEY, id_assana INTEGER, id_kit INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_TRAIN + "(id INTEGER PRIMARY KEY, id_kit INTEGER, title TEXT, date TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_SETS + "(id INTEGER PRIMARY KEY, setName TEXT)");

        // Читаем данные из assan.xml и формируем БД
        LoaderFromRes loader = new LoaderFromRes(mContext);
        ArrayList<HashMap<String, String>> forAssan = loader.loadAsana().getListAssan();
        long id_assana; // id вставленной записи в таблицу TABLE_ASSANA для формирования внешнего ключа связанных таблиц
        ContentValues values = new ContentValues();

        // Заполняем таблицу TABLE_ASSANA
        HashMap<String, String> setAssana;
        for (int i = 0; i < forAssan.size(); i++) {
            //values = new ContentValues();
            setAssana = forAssan.get(i);
            values.clear();
            values.put("title", setAssana.get("title"));
            values.put("title2", setAssana.get("title2"));
            //TODO: заменить жёсткую ссылку на вычисляемый путь ?? (тоже самое в onUpgrade)
            values.put("uri", "android.resource://com.khomutov_andrey.hom_ai.yoga/drawable/" + setAssana.get("uri"));// Формируем полный uri ресурса изображения
            values.put("time", setAssana.get("time"));
            values.put("content", setAssana.get("content"));
            values.put("positive", setAssana.get("positive"));
            values.put("negative", setAssana.get("negative"));
            values.put("sl", setAssana.get("sl"));
            db.insert(TABLE_ASSANA, null, values);
        }

        //Заполнить таблицу TABLE_SETS
        ArrayList<String> sets = loader.getListSet();
        for (String setName : sets) {
            values.clear();
            values.put("setName", setName);
            db.insert(TABLE_SETS, null, values);
            //Log.d("TABLE_SETS", String.valueOf(l));
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {// Переходим на позицию старой версии БД и попорядку обновляем версии до актуальной
            case 1: // Обновляем с версии 1 до 2
                //if(oldVersion==1){
                //String sql_asanaSelect = "SELECT * FROM " + TABLE_ASSANA;
                //String sql_asanaUpdate = "UPDATE " + TABLE_ASSANA + "SET uri=? WHEWE id=?";
                String whereClause = "id=?";
                ContentValues values = new ContentValues();
                Cursor cursorAsana = db.query(TABLE_ASSANA, null, null, null, null, null, null);
                if (cursorAsana.moveToFirst()) {
                    do {
                        values.clear();
                        Uri uri = Uri.parse("android.resource://com.khomutov_andrey.hom_ai.yoga/drawable/" + cursorAsana.getString(3));
                        values.put("uri", uri.toString());
                        db.update(TABLE_ASSANA, values, whereClause, new String[]{cursorAsana.getString(0)});
                    } while (cursorAsana.moveToNext());
                }
                cursorAsana.close();
                //}
                //break; //Не используем, что бы перейти на слудующий case - обновить до следующей версии БД
            case 2: { // Обновляем с версии 2 до версии3
                //Добавить новую талицу TABLE_SETS
                db.execSQL("CREATE TABLE " + TABLE_SETS + "(id INTEGER PRIMARY KEY, setName TEXT)");
                //Заполнить данные первого списка асан, вставить первую запись из списка getSets
                ContentValues val = new ContentValues();
                ArrayList<String> sets = new LoaderFromRes(mContext).getListSet(); // Читаем имена наборов из ресурса
                //val.put("setName",sets.get(0)); // Вставляем имя первого набора, т.к. в версии 2 Был только один набор
                val.put("setName", "set1");
                db.insert(TABLE_SETS, null, val); // Сохряняем имя этого набора асан
            }
        }
    }

    /**
     * Возвращает список имён списков асан
     *
     * @return
     */
    public ArrayList<String> getSets() {
        ArrayList<String> setsList = new ArrayList<String>();
        Cursor cursor;
        cursor = db.query(TABLE_SETS, null, null, null, null, null, null);
        String nameSet = "";
        if (cursor.moveToFirst()) {
            do {
                nameSet = cursor.getString(1);
                setsList.add(nameSet);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return setsList;
    }

    /**
     * Добавляет имя списка асан
     * @param nameSet имя списка асан
     * @return идентификатор добавленной записи
     */
    public long addSet(String nameSet){
        long idSet = 0;
        ContentValues values = new ContentValues();
        values.put("setName", nameSet);
        idSet = db.insert(TABLE_SETS, null, values);
        return idSet;
    }


    //Добавляет асану
    public long addAsana(String title, String title2, String uri, String time, String content, String positive, String negative, String sl) {
        long idAsana = 0;
        ContentValues values = new ContentValues();
        //id INTEGER PRIMARY KEY, title TEXT, title2 TEXT, uri TEXT, time TEXT, content TEXT, positive TEXT, negative TEXT, sl TEXT
        values.put("title", title);
        values.put("title2", title2);
        values.put("uri", uri);
        values.put("time", time);
        values.put("content", content);
        values.put("positive", positive);
        values.put("negative", negative);
        values.put("sl", sl);
        idAsana = db.insert(TABLE_ASSANA, null, values);
        return idAsana;
    }

    /**
     * Добавляет асану в БД
     * @param asanaMap - HashMap<String, String> описание реквизитов асаны (ключ - названиеполя, значение - значение поля)
     * @return id добавленной записи
     */
    public long addAsana(HashMap<String, String> asanaMap) {
        long idAsana = 0;
        ContentValues values = new ContentValues();
        //id INTEGER PRIMARY KEY, title TEXT, title2 TEXT, uri TEXT, time TEXT, content TEXT, positive TEXT, negative TEXT, sl TEXT
        if (asanaMap.containsKey("title")) {
            values.put("title", asanaMap.get("title"));
        }
        if (asanaMap.containsKey("title2")) {
            values.put("title2", asanaMap.get("title2"));
        }
        if (asanaMap.containsKey("uri")) {
            values.put("uri", asanaMap.get("uri"));
        }
        if (asanaMap.containsKey("time")) {
            values.put("time", asanaMap.get("time"));
        }
        if (asanaMap.containsKey("content")) {
            values.put("content", asanaMap.get("content"));
        }
        if (asanaMap.containsKey("positive")) {
            values.put("positive", asanaMap.get("positive"));
        }
        if (asanaMap.containsKey("negative")) {
            values.put("negative", asanaMap.get("negative"));
        }
        if (asanaMap.containsKey("sl")) {
            values.put("sl", asanaMap.get("sl"));
        }
        idAsana = db.insert(TABLE_ASSANA, null, values);
        return idAsana;
    }

    //обновляет асану
    public long updateAsana(String id_asana, String title, String title2, String uri, String time, String content, String positive, String negative, String sl) {
        long idAsana = 0;
        ContentValues values = new ContentValues();
        //id INTEGER PRIMARY KEY, title TEXT, title2 TEXT, uri TEXT, time TEXT, content TEXT, positive TEXT, negative TEXT, sl TEXT
        values.put("id", id_asana);
        values.put("title", title);
        values.put("title2", title2);
        values.put("uri", uri);
        values.put("time", time);
        values.put("content", content);
        values.put("positive", positive);
        values.put("negative", negative);
        values.put("sl", sl);
        String whereClause = new String("id = ?");
        String[] whereArgs = new String[]{id_asana};
        idAsana = db.update(TABLE_ASSANA, values, whereClause, whereArgs);
        //idAsana = db.insert(TABLE_ASSANA, null,values);
        return idAsana;
    }

    // Добавляет новый набор ассан
    public long addAssanaKit(String title, String sl) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("sl", sl);
        return db.insert(TABLE_KIT, null, values);
    }

    // Добавляет новую связь ассана-набор для набора ассан
    public long addToKitList(String idAsana, String idKit) {
        ContentValues values = new ContentValues();
        values.put("id_assana", idAsana);
        values.put("id_kit", idKit);
        return db.insert(TABLE_KIT_LIST, null, values);
    }

    /** TODO: Реализовать фильтрацию
     * Возвращает список названий наборов асан, соответствующих указаному названию
     * @param name
     * @return
     */
    public ArrayList<String> getKitNames(String name){
        ArrayList<String> kitNames = new ArrayList<>();
        Cursor cursor;
        String select = "title like ?";
        String[] selectionArgs = new String[]{name};
        cursor = db.query(TABLE_KIT, null, null, null,null,null, null);
        if(cursor.moveToFirst()){
            do{
                kitNames.add(cursor.getString(1));
            }while (cursor.moveToNext());
        }
        return kitNames;
    }

    /*
    Поиск набора ассан по id
     */
    public AsanaKit getKitById(String id) {
        AsanaKit asanaKit = null;
        Cursor cursorKit;
        if (id == null || id.isEmpty()) {
            return asanaKit;
        }
        String selection = "id=?";
        String[] selectionArgs = new String[]{id};
        cursorKit = db.query(TABLE_KIT, null, selection, selectionArgs, null, null, null);

        //Формируем Набор
        if (cursorKit.moveToFirst()) {
            asanaKit = new AsanaKit(cursorKit.getLong(0), cursorKit.getString(1), cursorKit.getString(2), null);
        }
        // Заполняем набор ассанами
        String queryAssana = "select a.id, a.title, a.title2, a.uri, a.time, a.content, a.positive, a.negative, a.sl, kl.id, kl.id_assana, kl.id_kit  " +
                "from kit_list as kl left outer join assana as a on kl.id_assana = a.id " +
                "where kl.id_kit = ?";
        Cursor cursorAssanaList;
        cursorAssanaList = db.rawQuery(queryAssana, selectionArgs);
        ArrayList<Asana> asanaList = buildAssanaListFromCursor(cursorAssanaList);
        asanaKit.setAssanaList(asanaList);
        return asanaKit;
    }

    //Обновляет набор асан (только описание набора)
    public void updateAssanaKit(String id_kit, String title) {
        String whereClause = new String("id = ?");
        String[] whereArgs = new String[]{id_kit};
        ContentValues values = new ContentValues();
        values.put("title", title);
        db.update(TABLE_KIT, values, whereClause, whereArgs);
    }

    // Удаление асаны
    public int deleteAsanaById(long idAsana) {
        int count = 0;
        String whereClause = new String("id=?");
        String whereClause2 = new String("id_assana=?");
        String[] whereArgs = new String[]{String.valueOf(idAsana)};

        db.beginTransaction();
        try {
            db.delete(TABLE_KIT_LIST, whereClause2, whereArgs);
            count = db.delete(TABLE_ASSANA, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return count;
    }

    // Возвращает набор ассан, вместе со списком ассан
    //TODO: доработать фильтрацию с праметрами
    public ArrayList<AsanaKit> getKits(String title, String sl) {
        ArrayList<AsanaKit> asanaKits = new ArrayList<AsanaKit>();
        AsanaKit asanaKit;
        HashMap<String, String> mapValues = new HashMap<>();
        StringBuilder selectionForKits = new StringBuilder();

        Cursor cursorKit = db.query(TABLE_KIT, null, null, null, null, null, null); // Выбираем все наборы

        // Формируем список сущностей-набор (AsanaKit)
        if (cursorKit.moveToFirst()) {
            do {
                asanaKit = new AsanaKit(cursorKit.getLong(0), cursorKit.getString(1), cursorKit.getString(2), null);
                asanaKits.add(asanaKit);
            } while (cursorKit.moveToNext());
        }

        // Каждый набор заполняем ассанами
        String queryAssana = "select a.id, a.title, a.title, a.uri, a.time, a.content, a.positive, a.negative, a.sl, kl.id, kl.id_assana, kl.id_kit  " +
                "from kit_list as kl left outer join assana as a on kl.id_assana = a.id " +
                "where kl.id_kit = ?";
        Cursor cursorAssanaList;
        for (int i = 0; i < asanaKits.size(); i++) {
            asanaKit = asanaKits.get(i);
            String[] selectionArgs2 = new String[]{String.valueOf(asanaKit.getId())};
            cursorAssanaList = db.rawQuery(queryAssana, selectionArgs2);
            ArrayList<Asana> asanaList = buildAssanaListFromCursor(cursorAssanaList);
            asanaKit.setAssanaList(asanaList);
            asanaKits.set(i, asanaKit);
        }
        return asanaKits;
    }

    //Возвращает количество наборов, содержащих указанную асану
    public int getCountKitsByAsanaId(long asanaId) {
        int count = 0;
        String query = "select id from " + TABLE_KIT_LIST + " where id_assana =?";
        String[] selectionArgs = new String[]{String.valueOf(asanaId)};
        Cursor cursor;
        cursor = db.query(TABLE_KIT_LIST, null, "id_assana=?", selectionArgs, null, null, null);
        count = cursor.getCount();
        return count;
    }

    /**
     * Очищает список ассан для набора.
     *
     * @param id_kit - идентификатор набора, у которого требуется удалить список ассан
     */
    public void clearKitList(String id_kit) {
        String whereClause = "id_kit = ?";
        String[] whereArgs = new String[]{id_kit};
        db.delete(TABLE_KIT_LIST, whereClause, whereArgs);
    }

    /**
     * Удаляет набор и список ассан этого набора
     *
     * @param id_kit - идентификатор набора
     */
    public void deleteKit(String id_kit) {
        clearKitList(id_kit);// очичтили список ассан набора
        String whereClause = "id = ?";
        String[] whereArgs = new String[]{id_kit};
        db.delete(TABLE_KIT, whereClause, whereArgs);
    }

    /**
     * Добавляет асаны в набор
     *
     * @param id_kit   - идентификатор набора
     * @param assansId - Строковый список идентификаторов асан
     */
    public void addAssanaList(String id_kit, ArrayList<String> assansId) {
        ContentValues values = new ContentValues();
        values.put("id_kit", id_kit);
        for (int i = 0; i < assansId.size(); i++) {
            values.put("id_assana", assansId.get(i));
            db.insert(TABLE_KIT_LIST, null, values);
        }
    }

    // Возвращает список ассан из курсора БД
    private ArrayList<Asana> buildAssanaListFromCursor(Cursor cursor) {
        ArrayList<Asana> asanaList = new ArrayList<>();
        HashMap<String, String> mapValues = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                mapValues.clear();
                mapValues.put("id", cursor.getString(0));
                mapValues.put("title", cursor.getString(1));
                mapValues.put("title2", cursor.getString(2));
                mapValues.put("uri", cursor.getString(3));
                mapValues.put("time", cursor.getString(4));
                mapValues.put("content", cursor.getString(5));
                mapValues.put("positive", cursor.getString(6));
                mapValues.put("negative", cursor.getString(7));
                mapValues.put("sl", cursor.getString(8));
                asanaList.add(new Asana(mapValues));
            } while (cursor.moveToNext());
        }
        return asanaList;
    }


    public Asana findAssanaById(String id) {
        HashMap<String, String> mapValues = new HashMap<String, String>();
        String selection = "id =";
        String[] selectionArgs = new String[]{id};
        Cursor cursor = db.query(TABLE_ASSANA, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            mapValues.put("id", cursor.getString(0));
            mapValues.put("title", cursor.getString(1));
            mapValues.put("title2", cursor.getString(2));
            mapValues.put("uri", cursor.getString(3));
            mapValues.put("time", cursor.getString(4));
            mapValues.put("content", cursor.getString(5));
            mapValues.put("positive", cursor.getString(6));
            mapValues.put("negative", cursor.getString(7));
            mapValues.put("sl", cursor.getString(8));
        }
        Asana asana = new Asana(mapValues);
        return asana;
    }

    // Возврящает список асан
    public ArrayList<Asana> getAssans(String title) {
        ArrayList<Asana> assanList = new ArrayList<Asana>();
        HashMap<String, String> mapValues = new HashMap<String, String>();
        String selection = null;
        String[] selectionArgs = null;
        if (title != null && title.length() > 0) {
            selection = "title like ? or title2 like ? ";
            selectionArgs = new String[]{"%" + title + "%", "%" + title + "%"};
        }
        Cursor cursor = db.query(TABLE_ASSANA, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                //mapValues = new HashMap<String, String>();
                mapValues.clear();
                mapValues.put("id", cursor.getString(0));
                mapValues.put("title", cursor.getString(1));
                mapValues.put("title2", cursor.getString(2));
                mapValues.put("uri", cursor.getString(3));
                mapValues.put("time", cursor.getString(4));
                mapValues.put("content", cursor.getString(5));
                mapValues.put("positive", cursor.getString(6));
                mapValues.put("negative", cursor.getString(7));
                mapValues.put("sl", cursor.getString(8));
                assanList.add(new Asana(mapValues));
            } while (cursor.moveToNext());

        }
        return assanList;
    }

    // Возврящает список названий асан
    public ArrayList<String> getAssansTitle(String title) {
        ArrayList<String> assanList = new ArrayList<String>();
        //HashMap<String, String> mapValues = new HashMap<String, String>();
        //String table = "select ";
        String selection = null;// Условие выборки
        String[] selectionArgs = null;// Значения для условия
        String[] columns = new String[]{"title", "title2"};
        if (title != null && title.length() > 0) {
            selection = "title like ? or title2 like ? ";
            selectionArgs = new String[]{"%" + title + "%", "%" + title + "%"};
        }
        //TODO: Переписать запрос на объединение таблиц в идин столбец из полей title и title2 ?
        Cursor cursor = db.query(true, TABLE_ASSANA, columns, selection, selectionArgs, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                assanList.add(cursor.getString(0));
                assanList.add(cursor.getString(1));
            } while (cursor.moveToNext());

        }
        return assanList;
    }

    public Asana getAssanaById(String id) {
        Asana asana = null;
        HashMap<String, String> mapValues = new HashMap<String, String>();
        String selection = "id = ? ";
        String[] selectionArgs = new String[]{id};
        Cursor cursor = db.query(TABLE_ASSANA, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            mapValues.clear();
            mapValues.put("id", cursor.getString(0));
            mapValues.put("title", cursor.getString(1));
            mapValues.put("title2", cursor.getString(2));
            mapValues.put("uri", cursor.getString(3));
            mapValues.put("time", cursor.getString(4));
            mapValues.put("content", cursor.getString(5));
            mapValues.put("positive", cursor.getString(6));
            mapValues.put("negative", cursor.getString(7));
            mapValues.put("sl", cursor.getString(8));
        }
        asana = new Asana(mapValues);
        return asana;
    }

    @Override
    /** Реализация интерфейса SaveToDataBase,
     * сохраняющая набор асан из загруженного XML
     * @param title Название набора;
     * @param listUri Список uri асан
     */
    public void addAsanaKit(String title, ArrayList<String> listUri) {
        /**
         * TABLE_KIT ="kit"; // Набор - Занятие из одной или нескольких ассан
         * TABLE_KIT_LIST ="kit_list"; // Список ассан одного набора
         *  db.execSQL("CREATE TABLE "+ TABLE_KIT_LIST + "(id INTEGER PRIMARY KEY, id_assana INTEGER, id_kit INTEGER)");
         */
        //ContentValues cv = new ContentValues();
        Long kit_id;
        String sql_kit = "INSERT INTO " + TABLE_KIT + " VALUES(?,?,?);";
        String sql_kitList = "INSERT INTO " + TABLE_KIT_LIST + " VALUES(?,?,?)";
        //String sql_asana = "SELECT id FROM " +TABLE_ASSANA+ " WHERE uri=?";
        SQLiteStatement statement_kit_insert = db.compileStatement(sql_kit);
        SQLiteStatement statement_kitList_insert = db.compileStatement(sql_kitList);
        ContentValues cv_kitList = new ContentValues();
        Cursor cursorAsana;
        String selection = "uri=?";
        String[] selectionArgs;
        // Добавляем набор с асанами через транзакцию, т.к. набор добавляется со всеми асанами,
        // либо набор не добавляется вообще, если какая-то асана не добавилась
        db.beginTransaction();
        // Сначала добавляем набор
        try {
            statement_kit_insert.clearBindings();
            statement_kit_insert.bindString(2, title);// Связываем со вторым столбцом значение title
            kit_id = statement_kit_insert.executeInsert();// Исполняем запрос - добавляем набор в таблицу
            if (kit_id != -1) {
                // Находим асану по uri из списка, если успешно добавляем, иначе прекращаем транзакцию
                cv_kitList.clear();
                for (int i = 0; i < listUri.size(); i++) {
                    selectionArgs = new String[]{listUri.get(i)};
                    cursorAsana = db.query(TABLE_ASSANA, null, selection, selectionArgs, null, null, null);
                    if (cursorAsana.moveToFirst()) {//Формируем данные для добавления строки в TABLE_KIT_LIST
                        cv_kitList.put("id_assana", cursorAsana.getLong(0));
                        cv_kitList.put("id_kit", kit_id);
                        db.insert(TABLE_KIT_LIST, null, cv_kitList);
                    }//else //TODO: прервать транзакцию
                }
            }//else //TODO: прервать транзакцию
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }
}
