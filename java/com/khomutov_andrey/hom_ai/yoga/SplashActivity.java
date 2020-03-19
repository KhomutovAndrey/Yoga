package com.khomutov_andrey.hom_ai.yoga;


import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.khomutov_andrey.hom_ai.yoga.db.DbHelper;
import com.khomutov_andrey.hom_ai.yoga.util.ControlYoga;

import java.util.Set;

public class SplashActivity extends AppCompatActivity {
    public static final String SETTINGS="Yoga.Settings";
    public static final String KITS="kits";
    public static final String SET_KITS="set_kits";
    Set<String> setKits;
    SharedPreferences mSettings;
    ControlYoga control;
    DbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Формируем БД
        control= new ControlYoga(this, ControlYoga.DB);
        //db = new DbHelper(this); // Создаём базу и заполняем данными из ресурсов, если нужно
        control.checkUpdate();

        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
