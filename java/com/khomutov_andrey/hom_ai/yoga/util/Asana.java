package com.khomutov_andrey.hom_ai.yoga.util;

import java.util.HashMap;

/**
 * Created by hom-ai on 26.04.2017.
 * Класс описания сущности Асана
 */

public class Asana {
    long id; // идентификатор в БД
    String title; // каноническое наименование
    String title2; // русское наименование
    String uri; // ссылка на ресурс изображения
    long time; // продолжительность ассаны в секундах
    String content; // Описание ассаны
    String positive; // Показание
    String negative; // Противопоказание
    String sl; // Уровень сложности

    public Asana(long idAssana, String title, String title2, String uri, long time, String content, String positive, String negative, String sl) {
        this.id = idAssana;
        this.title = title;
        this.title2 = title2;
        this.uri = uri;
        this.time = time;
        this.content = content;
        this.positive = positive;
        this.negative = negative;
        this.sl = sl;

    }

    // TODO: Перед публикацией. Добавить проверку значений при присовении.
    public Asana(HashMap<String, String> values){
        if(values.containsKey("id")){
            this.id = Integer.decode(values.get("id"));
        }
        this.title = values.get("title");
        this.title2 = values.get("title2");
        this.uri = values.get("uri");
        this.time = Long.decode(values.get("time"));
        this.content = values.get("content");
        this.positive = values.get("positive");
        this.negative = values.get("negative");
        this.sl = values.get("sl");

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // TODO: Перед публикацией. Добавить проверку возвращения корректного значения.
    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNegative() {
        return negative;
    }

    public void setNegative(String negative) {
        this.negative = negative;
    }

    public String getPositive() {
        return positive;
    }

    public void setPositive(String positive) {
        this.positive = positive;
    }

    // TODO: Перед публикацией. Добавить проверку возвращения корректного значения.
    public long getTime() {
        return time;
    }

    public String getSTime(){
        return String.valueOf(time);
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle2() {
        if(title2==null){
            return "";
        }
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }
}


