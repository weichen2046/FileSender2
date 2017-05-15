package com.weichen2046.filesender2.ui;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by chenwei on 2017/5/15.
 */

public class BaseActivity extends AppCompatActivity {
    // only access it in main thread
    private static int sAppShowCount = 0;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        sAppShowCount++;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sAppShowCount--;
    }

    /**
     * Note: should only be access in main thread.
     * @return
     */
    public static boolean isAppShowingUnlocked() {
        return sAppShowCount > 0;
    }
}
