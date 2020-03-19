package com.khomutov_andrey.hom_ai.yoga;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.khomutov_andrey.hom_ai.yoga.ui_sapport.PositionDisplayer;
import com.khomutov_andrey.hom_ai.yoga.util.Asana;
import com.khomutov_andrey.hom_ai.yoga.util.AssanDisplayer;
import com.khomutov_andrey.hom_ai.yoga.util.AsanaKit;
import com.khomutov_andrey.hom_ai.yoga.util.ControlYoga;
import com.khomutov_andrey.hom_ai.yoga.util.PresenterInterface;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//import com.google.android.gms.ads.Mo

public class AssanaActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, PresenterInterface.InterfaceToAsanaView {
    /**
     * Отображает одну асану с описанием.
     * Может работать в двух режимах:
     * - Отображать одну асану. В интенте передаётся идентификатор асаны ASANA_ID = "id"
     * - В режиме слайдшоу набора асан - отображает одну асану, но можно прокрутиь на слудующую асану в наборе
     * В интенте передаётся идентификатор набора KIT_ID = "kit_id"
     * Для удобства реализации слайдшоу, вывод асаны производим через набор,
     * т.е. если передан id асаны, и нужно отобразить одну асану, создаём новый набор, в который помещаем асану.
     * Дальше работаем с набором.
     */
    private String mId; // Идентификатор ассаны
    private String mIdKit; // Идентификатор набора
    private ControlYoga controlYoga; // Управляющий класс
    private AsanaKit kit; // Набор, если в режиме представления набора (слайдшоу)
    private ImageView imgAssana;
    private TextView tvTitle, tvTitle2, tvSlognost, tvTime, tvTitleKit, tvAsanaTime;
    private ToggleButton voiceButton;
    private Button kitPlayButton;
    public static final String KIT_ID = "kit_id";// Идентификатор набора, если не 0, то запускаем в режиме слайдшоу
    public static final String ASANA_ID = "id";// Идентификатор асаны,
    private final String TAB_CONTENT = "content";
    private final String TAB_POSITIVE = "positive";
    private final String TAB_NEGATIVE = "negative";
    private GestureDetector mGestureDetector; // Обработчик жестов
    PositionDisplayer positionDisplayer; // Отображает асану по текущей позиции в наборе (в режиме оторажения слайд-шоу)
    //private static final int SWIPE_MIN_DISTANCE = 120;
    //private static final int SWIPE_MAX_OFF_PATH = 250;
    //private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private TextToSpeech mTTS; // Реализация воспроизведения голосом
    private PresenterInterface.InterfaceFromAssanaView presenter;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        // Получаем идентификатор ассаны(набора), если экран вызван по нажатию на асану(запуск набора)
        mId = getIntent().getStringExtra(ASANA_ID);
        mIdKit = getIntent().getStringExtra(KIT_ID);

        //Кнопка редактирования асаны, открывает экран добавления асаны, в который передаёт id асаны
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mId!=null && !mId.isEmpty()){
                    Intent intent = new Intent(AssanaActivity.this, AsanaAddActivity.class);
                    intent.putExtra(AssanaActivity.ASANA_ID, String.valueOf(mId));
                    startActivity(intent);
                }
            }
        });
        imgAssana = (ImageView) findViewById(R.id.imgAssana);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle2 = (TextView) findViewById(R.id.tvTitle2);
        tvSlognost = (TextView) findViewById(R.id.tvSlognost);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvTitleKit = (TextView) findViewById(R.id.titleKit);
        tvAsanaTime = (TextView)findViewById(R.id.tvAsanaTime);

        kitPlayButton = (Button)findViewById(R.id.kitPlayButton);
        kitPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.callPlayAsanaKit(kit.getCurrentPosition());
            }
        });
        voiceButton = (ToggleButton) findViewById(R.id.voiceButton);
        voiceButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeVoice(isChecked);
            }
        });

        //Если открыт набор, то скрываем кнопку редактирования асаны
        LinearLayout layoutKitControl = (LinearLayout)findViewById(R.id.layoutKitControl);
        if (mIdKit != null && !mIdKit.equals("0") && !mIdKit.isEmpty()) {//Режим набора
            fab.setVisibility(View.GONE);
            layoutKitControl.setVisibility(View.VISIBLE);
            tvAsanaTime.setVisibility(View.VISIBLE);
            LinearLayout layout = (LinearLayout) findViewById(R.id.activity_assana);
            positionDisplayer = new PositionDisplayer(getApplicationContext());
            layout.addView(positionDisplayer);
        }else{//режим одной асаны
            fab.setVisibility(View.VISIBLE);
            layoutKitControl.setVisibility(View.GONE);
            tvAsanaTime.setVisibility(View.GONE);
        }
        //получаем идентификаторы вьюшек
        LinearLayout linear_bottom_sheet = (LinearLayout) findViewById(R.id.linear_bottom_sheet);
        //настройка поведения нижнего экрана
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(linear_bottom_sheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        initTab();
        // Подключаем управляющий класс
        controlYoga = new ControlYoga(this, "db");
        // Подключаем оработчик воспроизведения голосом
        mTTS = new TextToSpeech(AssanaActivity.this, AssanaActivity.this);
        // Подключаем обработчик смены асаны жестом (движение пальца по экрану)
        mGestureDetector = new GestureDetector(this, new GestureScrollAssana());
        tvAsanaTime = (TextView) findViewById(R.id.tvAsanaTime);

        // Инициализируем блок рекламы
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    // Указываем обработчика событий касания экрана, для обработки жестов
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Получаем асану и набор, если передан его id
        Asana asana = controlYoga.getAssana(mId, null);
        kit = controlYoga.getKitById(mIdKit);
        if (kit == null) {
            tvTitleKit.setVisibility(View.GONE); //Скрываем название набора
            kit = new AsanaKit(asana);
        } else {
            tvTitleKit.setText(kit.getTitle());
            tvTitleKit.setVisibility(View.VISIBLE);
            kit.setIndicator(positionDisplayer);
            positionDisplayer.setLinkedComponent(kit);
            positionDisplayer.show();
        }
        presenter = new AsanaActivityPre(this, kit);
        //kit.setIndicator(positionDisplayer);
        //positionDisplayer.setLinkedComponent(kit);
        //positionDisplayer.show();
        asana = kit.getAssanaList().get(0);
        fillData(asana);
    }

    @Override
    public void fillData(Asana asana) {
        // отображаем одну асану
        new AssanDisplayer(this, asana, imgAssana).display(null);
        tvTitle.setText(asana.getTitle());
        tvTitle2.setText(asana.getTitle2());
        tvSlognost.setText(getString(R.string.slognost) + ":" + asana.getSl());
        StringBuilder sDlitelnost = new StringBuilder().append(getString(R.string.time));
        DateFormat df = new SimpleDateFormat("mm:ss");
        Date t = new Date(asana.getTime());// Получаем в секундах
        sDlitelnost.append(" ").append(df.format(t));
        tvTime.setText(sDlitelnost.toString());
        tvAsanaTime.setText(sDlitelnost.toString());
        // Обновляем данные в панели закладок
        TextView textView = (TextView) findViewById(R.id.tvContent);
        textView.setText(asana.getContent());
        textView = (TextView) findViewById(R.id.tvPositive);
        textView.setText(asana.getPositive());
        textView = (TextView) findViewById(R.id.tvNegative);
        textView.setText(asana.getNegative());
        if(voiceButton.isChecked()) { // Если нажата кнопка озвучки, то озвучиваем асану
            //mTTS.speak(asana.getTitle(), TextToSpeech.QUEUE_FLUSH, null); // Озвучиваем название асаны
            speak(asana.getTitle()+ "..."+ asana.getTitle2());
        }
    }


    @Override
    public void viewTimeState(long time) {
        if(time>0){
            DateFormat df = new SimpleDateFormat("mm:ss");
            Date t = new Date(time);// Получаем в секундах
            tvAsanaTime.setText(df.format(t));
        }else{
            tvAsanaTime.setText("");
        }
    }

    @Override
    public void changeVoice(boolean isChecked) {
        voiceButton.setChecked(isChecked);
        if (isChecked){
            voiceButton.setBackgroundResource(R.drawable.voice);
        }else {
            voiceButton.setBackgroundResource(R.drawable.voice_off);
        }
    }

    @Override
    public void changePlay(boolean play) {
        kitPlayButton.setEnabled(play);
        if(play){
            kitPlayButton.setBackgroundResource(R.drawable.play);
        }else {
            kitPlayButton.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    public void speak(String text) {
        if (mTTS == null) {
            mTTS = new TextToSpeech(AssanaActivity.this, AssanaActivity.this);
        }
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null); // Озвучиваем название асаны
    }

    @Override
    public boolean isplay() {
        return !kitPlayButton.isEnabled();
    }

    // Инициализация и настройка вкладок
    private void initTab() {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec(TAB_CONTENT);
        tabSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab_view_selector));
        tabSpec.setContent(R.id.tab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(TAB_POSITIVE);
        tabSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab_posotove_selector));
        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec(TAB_NEGATIVE);
        tabSpec.setIndicator(null, getResources().getDrawable(R.drawable.tab_negative_selector));
        tabSpec.setContent(R.id.tab3);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);
    }



    @Override
    protected void onDestroy() {
        controlYoga.close();//Закрываем запрос к БД
        if(mTTS!=null){
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }

    /*
    Реализация метода класса TextToSpeech - синтерзатор речи, для озвучивания названия асаны
     */
    @Override
    public void onInit(int status) {
        if(status==TextToSpeech.SUCCESS){
            Locale locale = new Locale("ru");
            //Locale locale = Locale.getDefault();
            int result = mTTS.setLanguage(locale);
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                //Log.d("TTS--", "Language not supported");
            }
        }else {
            //Log.d("TTS--", "error");
        }
    }


    public class GestureScrollAssana implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            //Log.d("GestFling","onDown");
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            //Log.d("GestFling","onShowPress");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //Log.d("GestFling","onSingleTapUp");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //Log.d("GestFling","onScroll");
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            //Log.d("GestFling","onLongPress");
        }

        //TODO: Переложить в класс-презентер
        // Меняем асаны по направлению жеста
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // справа налево
            if (e1.getX() - e2.getX() > 100) {
                // Отображаем предыдущую асану, если есть
                if (kit.hasNext()) {
                    Asana asana = (Asana) kit.next();
                    fillData(asana);
                    if(isplay()) {
                        presenter.callStopAsanaKit();
                        presenter.callPlayAsanaKit(kit.getCurrentPosition());
                    }
                    // Озвучиваем название асаны
                    //mTTS.speak(asana.getTitle(), TextToSpeech.QUEUE_FLUSH, null);
                }
            } else if (e2.getX() - e1.getX() > 100) { //слева направо
                // Отображаем следущую асану, если есть
                if (kit.hasPrev()) {
                    Asana asana = (Asana) kit.prev();
                    fillData(asana);
                    if(isplay()) {
                        presenter.callStopAsanaKit();
                        presenter.callPlayAsanaKit(kit.getCurrentPosition());
                    }
                    // Озвучиваем название асаны
                    //mTTS.speak(asana.getTitle(), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
            return true;
        }
    }
}
