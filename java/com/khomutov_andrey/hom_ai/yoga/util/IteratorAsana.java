package com.khomutov_andrey.hom_ai.yoga.util;

import java.util.Iterator;

/**
 * Created by Andrey on 11.11.2017.
 * Добавляет возможность итерации назад,
 * и проверку существования предыдущего элемента
 */

public interface IteratorAsana extends Iterator {
    int getCurrentPosition();
    boolean hasPrev();
    boolean hasNext();
    Object prev();
    Object next();
    Object first();
    Object getObjectAT(int index);
    int getSize();
}
