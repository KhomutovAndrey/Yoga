package com.khomutov_andrey.hom_ai.yoga.db;

import java.util.ArrayList;

/**
 * Created by Andrey on 23.10.2017.
 * Интерфейс для реализации коллбэк сохранения загруженых наборов асан в локальную БД
 */
//TODO: Удалить интерфейс, реализовано черезм метод БД
public interface SaveToDataBase {
    /**
     * Сохраняет один набор асан
     * @param title - название набора
     * @param listUri - список uri асан в виде строк
     */
    public void addAsanaKit(String title, ArrayList<String> listUri);
}
