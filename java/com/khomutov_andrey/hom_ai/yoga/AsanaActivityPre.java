package com.khomutov_andrey.hom_ai.yoga;
import android.os.CountDownTimer;

import com.khomutov_andrey.hom_ai.yoga.util.Asana;
import com.khomutov_andrey.hom_ai.yoga.util.AsanaKit;
import com.khomutov_andrey.hom_ai.yoga.util.PresenterInterface;

/**
 * Реализует логику вывода данных на экран для AsanaActivity
 */
public class AsanaActivityPre implements PresenterInterface.InterfaceFromAssanaView {
    PresenterInterface.InterfaceToAsanaView mAsanaView;
    Asana mAsana;
    AsanaKit mAsanaKit;
    AsanaTimer timer;

    public AsanaActivityPre(PresenterInterface.InterfaceToAsanaView asanaView, AsanaKit kit){
        this.mAsanaView = asanaView;
        mAsanaKit = kit;
    }

    /**
     * Запускает набор асан. По таймеру сменяет асаны в наборе
     * @param index - номер позиции с которой начинать проигрывать
     */
    @Override
    public void callPlayAsanaKit(int index) {
        if (mAsanaKit==null || mAsanaKit.getSize()<1){
            return;
        }
        mAsana = (Asana)mAsanaKit.getObjectAT(index);
        mAsanaView.fillData(mAsana);// отображаем первую асану на экране
        mAsanaView.changePlay(false); //Блокируем кнопку запуска
        timer = new AsanaTimer(mAsana.getTime(), 500);
        timer.start();
    }

    @Override
    public void callStopAsanaKit() {
        if(timer !=null) {
            timer.cancel();
        }
    }


    /**
     * Таймер выполнения асаны.
     * уведомления о ходе таймера через указанный интервал
     * По истечению указаного времени, меняет асану.
     */
    public class AsanaTimer extends CountDownTimer {
        long countMillisec; // Сколько прошло времени
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public AsanaTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * Действия для уведомления о ходе таймера с интервалом millisUntilFinished
         * @param millisUntilFinished - интервал для уведомлений о ходе таймера
         */
        @Override
        public void onTick(long millisUntilFinished) {
            //long count = millisUntilFinished;
            mAsanaView.viewTimeState(millisUntilFinished);
            //mAsanaView.viewTimeState(String.valueOf(millisUntilFinished));
            //Log.d("AsanaTimerTick", ":"+String.valueOf(millisUntilFinished));
        }

        /**
         * Действия по истечению таймера
         */
        @Override
        public void onFinish() {
            mAsanaView.viewTimeState(0);
            if(mAsanaKit.hasNext()){
                mAsana = (Asana) mAsanaKit.next();
                mAsanaView.fillData(mAsana);
                timer = new AsanaTimer(mAsana.getTime(), 500);
                timer.start();
            }else {// По завершению переходим на первую асану набора
                mAsana = (Asana)mAsanaKit.first();
                mAsanaView.speak("Завершено");
                mAsanaView.changePlay(true); //Разблокируем кнопку запуска
                mAsanaView.changeVoice(false);
                mAsanaView.fillData(mAsana);
                //Log.d("AsanaTimerTick", "timer finish");
            }
        }
    }


}
