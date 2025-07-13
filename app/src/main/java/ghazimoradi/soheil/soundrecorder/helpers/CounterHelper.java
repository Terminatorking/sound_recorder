package ghazimoradi.soheil.soundrecorder.helpers;

import android.os.Handler;
import android.util.Log;

public class CounterHelper {

    public static class CounterThread extends Thread {
        private final Runnable uiFunction;
        private final Handler handler = new Handler();
        private float counter = 0;
        private boolean shouldStopCounter = false;

        public float getCounter() {
            return this.counter;
        }

        public void resetCounter() {
            counter = 0;
        }

        public void startCounter() {
            shouldStopCounter = false;
            this.start();
        }

        public void stopCounter() {
            shouldStopCounter = true;
        }

        public CounterThread(Runnable uiFunction) {
            this.uiFunction = uiFunction;
        }

        @Override
        public void run() {
            while (!shouldStopCounter) {
                try {
                    Thread.sleep(1);
                    handler.post(uiFunction);
                    if (shouldStopCounter) {
                        break;
                    }
                    counter += 1f;
                } catch (Exception e) {
                    Log.e("Thread error", e.getMessage(), e);
                }
            }
        }
    }

    public static String convertToTime(float counter) {
        int ms = (int) counter % 1000;
        counter = counter / 1000;
        int min = (int) counter / 60;
        int sec = (int) counter - min * 60;
        String minPrefix;
        String secPrefix;
        String msPrefix;
        if (min < 10) {
            minPrefix = "0";
        } else {
            minPrefix = "";
        }
        if (sec < 10) {
            secPrefix = "0";
        } else {
            secPrefix = "";
        }

        if (ms < 10) {
            msPrefix = "00";
        } else if (ms < 100) {
            msPrefix = "0";
        } else {
            msPrefix = "";
        }
        return minPrefix + min + ":" + secPrefix + sec + "." + msPrefix + ms;
    }
}