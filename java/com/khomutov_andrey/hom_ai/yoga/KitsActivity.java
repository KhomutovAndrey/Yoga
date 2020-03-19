package com.khomutov_andrey.hom_ai.yoga;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.khomutov_andrey.hom_ai.yoga.adapters.Adt_KitAdapter;
import com.khomutov_andrey.hom_ai.yoga.controls.ControlKitsActivity;
import com.khomutov_andrey.hom_ai.yoga.dialogs.Dlg_QueryDelete;
import com.khomutov_andrey.hom_ai.yoga.util.AsanaKit;
import com.khomutov_andrey.hom_ai.yoga.util.ControlYoga;

import java.util.ArrayList;

/**
 * Активити работы с наборами ассан
 */

public class KitsActivity extends AppCompatActivity implements Adt_KitAdapter.ViewHolder.NoticeDeleted, Dlg_QueryDelete.NoticeDialogListener{
    ArrayList<AsanaKit> kits;
    RecyclerView recyclerView;
    ControlYoga controlYoga;
    ControlKitsActivity controlKitsActv;
    long deliteIndex;
    //private FirebaseAnalytics mFirebaseAnalitics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kits);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KitsActivity.this, KitActivity.class);
                startActivity(intent);
                Snackbar.make(view, getString(R.string.kitAdded), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //controlKitsActv
            }
        });

        controlYoga = new ControlYoga(this,"db");
        controlKitsActv = new ControlKitsActivity(this, controlYoga);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //mFirebaseAnalitics = FirebaseAnalytics.getInstance(this);
        //Bundle bundle = new Bundle();
        //bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "jhg");
        //mFirebaseAnalitics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        kits = controlYoga.getKits();
        Adt_KitAdapter kitAdapter = new Adt_KitAdapter(this, kits);
        recyclerView.setAdapter(kitAdapter);
    }

    //Удаляет итем из списка
    @Override
    public void onDeletedIndex(long index) {
        //Log.d("AsanaKit","deleted");
        DialogFragment dialog = new Dlg_QueryDelete();
        //Устанавливаем аргументы (текстовые значения для заголовка и кнопок)
        Bundle arg = new Bundle();
        arg.putString("message", getResources().getString(R.string.delete_query));
        arg.putString("positive", getResources().getString(R.string.yes));
        arg.putString("negative", getResources().getString(R.string.no));
        dialog.setArguments(arg);

        ((Dlg_QueryDelete)dialog).attach(this);
        dialog.show(getSupportFragmentManager(), "QueryDeleted");
        deliteIndex = index;
    }

    //Удаляет набор из БД, обновляет данные
    @Override
    public void onDeleted(boolean responce) {
        //Log.d("AsanaKit","deleted!");
        if(responce ){
            AsanaKit asanaKit = controlYoga.getKitById(String.valueOf(deliteIndex));
            controlYoga.deleteAssanaKit(asanaKit);
            //Обновляем данные на экране
            kits = controlYoga.getKits();
            ((Adt_KitAdapter)recyclerView.getAdapter()).setData(kits);
            ((Adt_KitAdapter)recyclerView.getAdapter()).notifyDataSetChanged();
            //Log.d("AsanaKit","deleted!!!"+String.valueOf(deliteIndex));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controlYoga.close();
    }
}
