package com.khomutov_andrey.hom_ai.yoga.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.khomutov_andrey.hom_ai.yoga.R;
import com.squareup.picasso.Picasso;

/**
 * Created by hom-ai on 16.05.2017.
 * Выводит изображение ассаны в View, через библиотеку Picasso
 */

public class AssanDisplayer {
    private Context mContext;
    private Asana mAsana;
    private ImageView mImageView;
    private Uri mUri;
    static Picasso picasso;

    public AssanDisplayer(Context c, Asana asana, View view){
        mContext = c;
        mAsana = asana;
        mImageView = (ImageView)view;
    }

    //TODO: Передавать вторым параметром id на ресурс изображения-ошибки
    public void displayDefoultRes(){
        //Log.d("Asana_URI",mAsana.getUri());
        // Для универсальности загружаем изображение через его uri
        int resId = mContext.getResources().getIdentifier("image_add","drawable", mContext.getPackageName());
        Resources res= mContext.getResources();

        mUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+
                res.getResourcePackageName(resId)+"/"+res.getResourceTypeName(resId)+"/"+
                res.getResourceEntryName(resId));

        Picasso
                .with(mContext)
                //.load(resId)
                .load(mUri)
                .error(R.mipmap.ic_launcher)
                .centerInside()
                .fit()
                .into(mImageView);

    }


    //TODO: Передавать вторым параметром id на ресурс изображения-ошибки
    public void display(Uri uri){
        //Log.d("Asana_URI",mAsana.getUri());
        // Для универсальности загружаем изображение через его uri
        //Выбор способа загрузки через Uri.getScheme()
        //int resId = mContext.getResources().getIdentifier(mAsana.getUri(),"drawable", mContext.getPackageName());
        //Resources res= mContext.getResources();
        /*
        String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int permission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        int REQUEST_EXTERNAL_STORAGE = 1;
        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((AppCompatActivity)mContext, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        */
        if(uri!=null){
            mUri = uri;
        }else mUri = Uri.parse(mAsana.getUri());
        //mImageView.setImageURI(mUri);


        Picasso.Builder builder = new Picasso.Builder(mContext.getApplicationContext());
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Toast.makeText(mContext,R.string.err_permissions,Toast.LENGTH_SHORT).show();
            }
        });

        if (picasso ==null){
            picasso = builder.build();
        }
        //picasso = builder.build();
        picasso.load(mUri)
                .error(R.mipmap.ic_launcher)
                //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .centerInside()
                .fit()
                .into(mImageView);

        /*
        Picasso
                .with(mContext)
                //.load(resId)
                .load(mUri)
                .error(R.drawable.asans)
                .centerInside()
                .fit()
                .into(mImageView);
        */
    }
}
