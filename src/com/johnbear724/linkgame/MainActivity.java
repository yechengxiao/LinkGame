package com.johnbear724.linkgame;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.johnbear724.linkgame.core.GameConfig;
import com.johnbear724.linkgame.core.GameService;
import com.johnbear724.linkgame.view.GameView;

public class MainActivity extends Activity {

    private GameService gameService; 
    private GameView gameView;
    private RelativeLayout gameLaytou;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        gameView = (GameView) findViewById(R.id.game_view);
        gameLaytou = (RelativeLayout) findViewById(R.id.game_layout); 
        gameService = new GameService(new GameConfig(10, 7, 20, 20, this));
        
        gameView.setGameService(gameService);
        
        final TextView startText = (TextView) findViewById(R.id.startText);
        startText.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                gameView.startGame();
                gameLaytou.removeView(startText);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }
}
