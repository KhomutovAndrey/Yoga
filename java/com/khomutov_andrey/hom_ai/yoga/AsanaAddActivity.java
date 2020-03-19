package com.khomutov_andrey.hom_ai.yoga;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost;

import com.google.android.gms.common.api.GoogleApiClient;
import com.khomutov_andrey.hom_ai.yoga.util.Asana;
import com.khomutov_andrey.hom_ai.yoga.util.AssanDisplayer;
import com.khomutov_andrey.hom_ai.yoga.util.ControlYoga;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class AsanaAddActivity extends AppCompatActivity {
    private EditText etTitle1, etTitle2, etContent, etPositive, etNegative;
    private Spinner spSlojnost;
    private EditText etTime;
    private ImageView imageView;
    private Uri selectedImage = null;
    private String mId; // Идентификатор ассаны
    private final String TAB_CONTENT = "content";
    private final String TAB_POSITIVE = "positive";
    private final String TAB_NEGATIVE = "negative";
    private final int SELECT_PHOTO = 101;
    private ControlYoga controlYoga;
    Asana asana;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asana_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Получаем идентификатор ассаны, если экран вызван для редактирования асаны
        mId = getIntent().getStringExtra(AssanaActivity.ASANA_ID);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Save().onClick(view);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        etTitle1 = (EditText) findViewById(R.id.etTitle1);
        etTitle2 = (EditText) findViewById(R.id.etTitle2);
        spSlojnost = (Spinner) findViewById(R.id.spSlognost);
        etTime = (EditText) findViewById(R.id.etTime);
        etContent = (EditText) findViewById(R.id.etContent);
        etPositive = (EditText) findViewById(R.id.etPositive);
        etNegative = (EditText) findViewById(R.id.etNegative);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new LoadImage(this));
        new AssanDisplayer(this, null, imageView).displayDefoultRes();
        initTab();

        controlYoga = new ControlYoga(this, "db");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.slognost, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSlojnost.setAdapter(adapter);

        if(asana==null){ //Объект может существовать, если экран вызван для редактирования
            asana = controlYoga.getAssana(mId, null);
        }
        fillData(asana);
    }

    private void fillData(Asana asana){
        if(asana==null) return;
        new AssanDisplayer(this, asana, imageView).display(null);
        etTitle1.setText(asana.getTitle());
        etTitle2.setText(asana.getTitle2());
        //tvSlognost.setText(getString(R.string.slognost) + ":" + asana.getSl());
        spSlojnost.setSelection(Integer.decode(asana.getSl())-1);
        StringBuilder sDlitelnost = new StringBuilder();//.append(getString(R.string.time));
        DateFormat df = new SimpleDateFormat("mm:ss");
        Date t = new Date(asana.getTime());// Получаем в секундах
        //String s2 = df.format(t);
        sDlitelnost.append(df.format(t));
        etTime.setText(sDlitelnost.toString());

        // Обновляем данные в панели закладок
        EditText etText = (EditText) findViewById(R.id.etContent);
        etText.setText(asana.getContent());
        etText = (EditText) findViewById(R.id.etPositive);
        etText.setText(asana.getPositive());
        etText = (EditText) findViewById(R.id.etNegative);
        etText.setText(asana.getNegative());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("permission","onActivityResult");
        // Получаем постаянные права на доступ к файлам внешней памяти
        //selectedImage = data.getData();

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    selectedImage = data.getData();
                    // Устанавливаем постаянные права на доступ к файлу
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        this.getContentResolver().takePersistableUriPermission(selectedImage, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    if(asana!=null){//Обновляем Uri асаны
                        asana.setUri(selectedImage.toString());
                    }
                        AssanDisplayer displayer = new AssanDisplayer(this, null, imageView);
                        displayer.display(selectedImage);
                }
                break;
        }

    }


    private class LoadImage implements View.OnClickListener {
        private Activity activity;

        public LoadImage(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View view) {
            //Log.d("permission","load");
            // Запрашиваем права на доступ к мультимедиа
            String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            int REQUEST_EXTERNAL_STORAGE = 1;
            int permission = 0;
            for (String perm : PERMISSIONS_STORAGE) {
                permission = ContextCompat.checkSelfPermission(activity, perm);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(PERMISSIONS_STORAGE, 1234);
                    }
                }
            }

            // Открываем диалог выбора изображения
            if (Build.VERSION.SDK_INT < 19) {
                //Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            } else {
                Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                photoPickerIntent.addCategory(Intent.CATEGORY_OPENABLE);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }

        }
    }

    private void initTab() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec(TAB_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab_view_selector, getTheme()));
        } else tabSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab_view_selector));
        tabSpec.setContent(R.id.tab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(TAB_POSITIVE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab_posotove_selector, getTheme()));
        } else
            tabSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab_posotove_selector));
        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(TAB_NEGATIVE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab_negative_selector, getTheme()));
        } else
            tabSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab_negative_selector));
        tabSpec.setContent(R.id.tab3);
        tabHost.addTab(tabSpec);
    }


    private class Save implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String title = etTitle1.getText().toString();
            String title2 = etTitle2.getText().toString();
            String sl = String.valueOf(spSlojnost.getSelectedItemPosition() + 1);
            //Date date;
            long time;
            try {
                Date date = new SimpleDateFormat("mm:ss").parse(etTime.getText().toString());
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                //пересчитываем в миллисекунды
                time = (calendar.get(Calendar.MINUTE) * 60000) + (calendar.get(Calendar.SECOND) * 1000);
            } catch (ParseException e) {
                e.printStackTrace();
                time = 60000;
            }
            String content = etContent.getText().toString();
            String positive = etPositive.getText().toString();
            String negative = etNegative.getText().toString();
            //TODO: Рассмотреть возможность копирования изображения асаны в отдельную директорию
            String imagePath;
            ControlYoga controlYoga = new ControlYoga(AsanaAddActivity.this, ControlYoga.DB);
            if (selectedImage != null) {
                imagePath = selectedImage.toString();
            } else imagePath = controlYoga.getDefoultImage();
            //Asana asana = new Asana(0, title, title2, imagePath, time, content, positive, negative, sl);//16/08/2018

            if(asana!=null){
                if(selectedImage==null){
                    selectedImage = Uri.parse(asana.getUri());
                }
                asana = new Asana(asana.getId(), title, title2, selectedImage.toString(), time, content, positive, negative, sl);
                //Обновление асаны
                controlYoga.updateAsana(asana);
            } else{
                Asana asana = new Asana(0, title, title2, imagePath, time, content, positive, negative, sl);
                //Asana asana = new Asana(0, title, title2, selectedImage.toString(), time, content, positive, negative, sl);
                controlYoga.addAsana(asana);
            }
            controlYoga.close();
            AsanaAddActivity.this.finish();
        }
    }
}
