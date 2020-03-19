package com.khomutov_andrey.hom_ai.yoga.controls;

import android.content.Context;
import android.widget.Toast;

import com.khomutov_andrey.hom_ai.yoga.util.AsanaKit;
import com.khomutov_andrey.hom_ai.yoga.util.ControlYoga;

import java.util.ArrayList;

/**
 * Created by hom-ai on 19.06.2017.
 *
 */

public class ControlKitsActivity {
    private Context mContext;
    private ControlYoga mControlYoga;
    private ArrayList<AsanaKit> kits;

    public ControlKitsActivity(Context context, ControlYoga controlYoga){
        mContext = context;
        mControlYoga = controlYoga;
    }

    public void addKit(AsanaKit kit){
        Toast.makeText(mContext, "add",Toast.LENGTH_SHORT).show();
    }

    public void deleteKit(AsanaKit kit){
        Toast.makeText(mContext, "delete", Toast.LENGTH_SHORT).show();
    }
}
