package com.khomutov_andrey.hom_ai.yoga.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.khomutov_andrey.hom_ai.yoga.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hom-ai on 10.07.2017.
 * Класс строит диалог с полем ввода текста, для запроса наименования
 */

public class Dlg_QueryTitle extends DialogFragment {

    /**
     * Описание интерфейса callback для обработки реализации реакции на кнопки диалога
     */
    public interface NoticeDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener; //Ссылка на класс-реализующих интерфейс обратного вызова
    View view;
    //StringBuilder sTitleKit;

    // Указываем класс, реализующий обратный вызов реакции нажатия кнопок
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (NoticeDialogListener) context;
    }

    public void attach(Context context){
        mListener = (NoticeDialogListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Настраиваем вид диалога
        view = inflater.inflate(R.layout.dlg_query_title, null);
        EditText editText = (EditText)view.findViewById(R.id.etTitle);
        String dateFormat = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
        //String s = this.getString(R.string.title_kit);
        StringBuilder sTitleKit = new StringBuilder().append(getString(R.string.title_kit)).append(dateFormat);
        editText.setText(sTitleKit.toString());

        builderDialog.setView(view)
                .setMessage(R.string.query_title)
                .setPositiveButton(R.string.btn_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO: Передать заголовок в основное окно
                        mListener.onDialogPositiveClick(Dlg_QueryTitle.this);
                    }
                })
                .setNegativeButton(R.string.btn_cansel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO: отменить, ничего не делать, закрыть диалог
                        mListener.onDialogNegativeClick(Dlg_QueryTitle.this);
                    }
                });
        return builderDialog.create();
    }

    public String getTitle(){
        EditText editText = (EditText) view.findViewById(R.id.etTitle);
        return editText.getText().toString();
    }
}
