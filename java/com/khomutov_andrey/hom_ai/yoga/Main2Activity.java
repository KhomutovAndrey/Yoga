package com.khomutov_andrey.hom_ai.yoga;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * Стартовый экран. Предлагает выбор дальнейшей работы
 */
public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ImageView iv_assans = (ImageView)findViewById(R.id.iv_assans);
        ImageView iv_kits = (ImageView)findViewById(R.id.iv_kits);
        final Intent intentAssans = new Intent(this,AssansActivity.class);
        final Intent intentKits = new Intent(this, KitsActivity.class);
        TextView tvVersion = (TextView)findViewById(R.id.tvVersion);
        tvVersion.setText(getString(R.string.version)+BuildConfig.VERSION_NAME);

        iv_assans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.CONTENT, "Assans");
                FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(Main2Activity.this);
                analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                startActivity(intentAssans);
            }
        });

        iv_kits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle params = new Bundle();
                params.putString(FirebaseAnalytics.Param.CONTENT, "Kits");
                FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(Main2Activity.this);
                analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
                startActivity(intentKits);
            }
        });
    }
}
