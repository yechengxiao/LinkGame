package com.johnbear724.linkgame.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import com.johnbear724.linkgame.R;

public class PauseDialog extends Activity {

    public static final int NEW_GAME = 0x1;
    public static final int EXIT = 0x2;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_pause_dialog);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pause_dialog, menu);
        return true;
    }

    public void resume(View v) {
        finish();
    }
    
    public void newGame(View v) {
        setResult(NEW_GAME);
        finish();
    }
    
    public void exit(View v) {
        setResult(EXIT);
        finish();
    }
    
}
