package com.johnbear724.linkgame;


import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.johnbear724.linkgame.core.GameConfig;
import com.johnbear724.linkgame.core.GameService;
import com.johnbear724.linkgame.dialog.PauseDialog;
import com.johnbear724.linkgame.sound.GameSound;
import com.johnbear724.linkgame.view.GameView;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class MainActivity extends Activity {

    private GameView gameView;
    private ProgressBar progressBar;
    private TextView timeText;
    private TextView scoreText;
    private TextView startText;
    private TextSwitcher textSwitcher;
    private RelativeLayout timeUpLayout;
    private RelativeLayout victoryLayout;
    private RelativeLayout gameLayout;
    private LayoutAnimationController layoutAni;
    private GameService gameService; 
    private Handler handler;
    private GameSound gameSound;
    private Timer timer = new Timer();
    private int time;
    private int score;
    private int comboTime;
    private int combo;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        if(time != 0) {
            startTimer(time);
        }
        gameSound.autoResume(gameView.isStart());
        super.onResume();
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        gameSound.autoPause();
        super.onPause();
    }
    
    public void init() {
        gameView = (GameView) findViewById(R.id.game_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        timeText = (TextView) findViewById(R.id.time_text);
        scoreText = (TextView) findViewById(R.id.score);
        textSwitcher = (TextSwitcher) findViewById(R.id.text_switcher);
        timeUpLayout = (RelativeLayout) findViewById(R.id.time_up_layout);
        victoryLayout = (RelativeLayout) findViewById(R.id.victory_layout);
        gameLayout = (RelativeLayout) findViewById(R.id.game_layout);
        
        textSwitcher.setFactory(new ViewFactory() {
            
            @Override
            public View makeView() {
                // TODO Auto-generated method stub
                TextView tv = new TextView(MainActivity.this);
                tv.setGravity(Gravity.CENTER);
                return tv;
            }
        });
        textSwitcher.setText("Ready!");
        gameSound = new GameSound(this);
        gameService = new GameService(new GameConfig(10, 7, 20, 20, this));
        timer = new Timer();
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch(msg.what) {
                case GameConfig.WIN_GAME :
                    if(timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    gameView.gameOver();
                    gameSound.play(GameSound.WIN, 1, 1, 0, 0, 1);
//                    new AlertDialog.Builder(MainActivity.this)
//                        .setTitle("胜利")
//                        .setMessage("哈哈哈哈啊哈哈！！！")
//                        .setPositiveButton("88", null)
//                        .setNegativeButton("哈罗", null)
//                        .create().show();
                    victoryLayout.setVisibility(View.VISIBLE);
                    break;
                case GameConfig.TIMER :
                    time--;
                    comboTime++; 
                    if(comboTime == 3) {
                        if(combo != 0) {
                            textSwitcher.setText("Ready!");
                        }
                        combo = 0;
                        comboTime = 0;
                    }
                    if(time == 0) {
                        setTimer(time);
                        timer.cancel();
                        timer = null;
                        gameView.gameOver();
                        showTimeUp();
                        break;
                    } else if(time <= 10) {
                        gameView.countAnimation();
                        setTimer(time);
                        timeText.setTextColor(Color.RED);
                        break;
                    } else {
                        timeText.setTextColor(Color.GRAY);
                        setTimer(time);
                    }
                    break;
                case GameConfig.SCOUR_1 :
                    score(1);
                    break;
                case GameConfig.SCOUR_2 :
                    score(2);
                    break;
                case GameConfig.SCOUR_3 :
                    score(3);
                    break;
                case GameConfig.START_TEXT_INVISIBLE :
                    hideLayout();
                }
                    
            };
        };
        gameView.setGame(gameService, gameSound, handler);
        layoutAni = AnimationUtils.loadLayoutAnimation(this, R.anim.time_up_layout_ani);
        timeUpLayout.setLayoutAnimation(layoutAni);
        timeUpLayout.setVisibility(View.INVISIBLE);
        victoryLayout.setLayoutAnimation(layoutAni);
        victoryLayout.setVisibility(View.INVISIBLE);
        startText = (TextView) findViewById(R.id.startText);
        startText.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //FIXME 有时调用之后gameView不绘制，触摸也没反应
                startGame(12);
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
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(MainActivity.this, PauseDialog.class);
            MainActivity.this.startActivityForResult(intent, 0);
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(resultCode) {
        case PauseDialog.NEW_GAME :
            startGame(100);
            break;
        case PauseDialog.EXIT :
            finish();
            break;
        }
    }
    
    
    public void startTimer(int t) {
        time = t + 1;
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(GameConfig.TIMER);
            }
        }, 0, 1000);
    }
    
    public void setTimer(int t) {
        time = t;
        timeText.setText(t + "");
        progressBar.setProgress(t);
    }
    
    public void showTimeUp() {
        gameSound.play(GameSound.TIME_UP, 1, 1, 0, 0, 1);
        timeUpLayout.setVisibility(View.VISIBLE);
//        new AlertDialog.Builder(MainActivity.this)
//        .setTitle("时间到！！")
//        .setMessage("哈哈哈哈啊哈哈！！！")
//        .setPositiveButton("88", null)
//        .setNegativeButton("哈罗", null)
//        .create().show();
    }
    
    public void score(int level) {
        int origin = score;
        score += (level * 100 + (combo - 1) * 100);
        combo++;
        comboTime = 0;
        if(combo == 1) {
            textSwitcher.setText("Combo1");
        } else if(combo == 2) {
            gameSound.play(GameSound.GOOD, 1, 1, 0, 0, 1);
            textSwitcher.setText("Combo2: Good!");
        } else if(combo == 3) {
            gameSound.play(GameSound.NICE, 1, 1, 0, 0, 1);
            textSwitcher.setText("Combo3: Nice!");
        } else if(combo == 4) {
            gameSound.play(GameSound.COOL, 1, 1, 0, 0, 1);
            textSwitcher.setText("Combo4: Cool!");
        }else if(combo >= 5) {
            gameSound.play(GameSound.CRAZY, 1, 1, 0, 0, 1);
            textSwitcher.setText("Combo" + combo + ": Crazy!");
        } 
        ValueAnimator ani = ValueAnimator.ofInt(origin, score);
        ani.addUpdateListener(new AnimatorUpdateListener() {
            
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                // TODO Auto-generated method stub
                scoreText.setText((Integer)arg0.getAnimatedValue() + "");
            }
        });
        ani.setDuration(300);
        ani.start();
    }
    
    public void reset() {
        setTimer(0);
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        score = 0;
        scoreText.setText("0");
        textSwitcher.setText("Ready!");
        combo = 0;
        comboTime = 0;
        gameView.gameOver();
    }
    
    public void startGame(int t) {
        reset();
        startTimer(t);
        gameView.startGame();
    }
    
    public void hideLayout() {
        startText.setVisibility(View.INVISIBLE);
        gameLayout.removeViewAt(10);
        timeUpLayout.setVisibility(View.INVISIBLE);
//        victoryLayout.setVisibility(View.INVISIBLE);
    }
    
    public void showVictoryLayout(int id) {
//        gameLayout.addV
    }
    
    public void timeUpPlayAgain(View v) {
        startGame(100);
    }
    
}
