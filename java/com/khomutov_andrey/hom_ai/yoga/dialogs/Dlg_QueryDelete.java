package com.khomutov_andrey.hom_ai.yoga.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.khomutov_andrey.hom_ai.yoga.R;

/**
 * Created by hom-ai on 13.07.2017.
 * Строит диалог подтверждения удаления элемента
 */

public class Dlg_QueryDelete extends DialogFragment {

    /*
    Описание интерфейса для реализации collbeck обработки рекции кнопок диалога
     */
    public interface NoticeDialogListener{
        public void onDeleted(boolean responce);
    }
    private NoticeDialogListener mListener;

    /*
    // Устанавливаем обработчик реализации collbeck обработки рекции кнопок диалога
    public void attach(Context context){
        mListener = (NoticeDialogListener)context;
    }
    */

    public void attach(NoticeDialogListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Восстанавливаем переданные аргументы - значение надписей для загловка и кнопок
        Bundle arg = getArguments();
        String message = getString(R.string.delete_query);
        String positive = getString(R.string.yes);
        String negative = getString(R.string.no);
        if(arg!=null){
            message = arg.getString("message",message);
            positive = arg.getString("positive",positive);
            negative = arg.getString("negative",negative);
        }

        AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity());
        builderDialog.setMessage(message)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDeleted(true);
                    }
                })
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDeleted(false);
                    }
                });

        return builderDialog.create();
    }
}
