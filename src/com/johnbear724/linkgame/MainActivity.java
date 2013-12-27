package com.johnbear724.linkgame;


import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

import com.johnbear724.linkgame.core.GameConfig;
import com.johnbear724.linkgame.core.GameService;
import com.johnbear724.linkgame.sound.GameSound;
import com.johnbear724.linkgame.view.GameView;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class MainActivity extends Activity {

    private GameView gameView;
    private ProgressBar progressBar;
    private TextView timeText;
    private TextView scoreText;
    private TextSwitcher textSwitcher;
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
                    gameSound.play(GameSound.WIN, 1, 1, 0, 0, 1);
                    new AlertDialog.Builder(MainActivity.this)
                        .setTitle("胜利")
                        .setMessage("哈哈哈哈啊哈哈！！！")
                        .setPositiveButton("88", null)
                        .setNegativeButton("哈罗", null)
                        .create().show();
                    reset();
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
                }
                    
            };
        };
        gameView.setGame(gameService, gameSound, handler);
        
        final TextView startText = (TextView) findViewById(R.id.startText);
        startText.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //FIXME 有时调用之后gameView不绘制，触摸也没反应
                startTimer(12);
                gameView.startGame();
                startText.setVisibility(View.INVISIBLE);;
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
            if(true) {
                new AlertDialog.Builder(MainActivity.this)
                .setTitle("游戏暂停！！")
                .setPositiveButton("继续游戏", null)
                .setNeutralButton("新游戏", new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        reset();
                        startTimer(100);
                        gameView.startGame();
                    }

                })
                .setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
//                        MainActivity.this.finish();
                        System.exit(0);
                    }
                })
                .create().show();
            } else {
                new AlertDialog.Builder(MainActivity.this)
                .setPositiveButton("继续游戏", null)
                .setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        MainActivity.this.finish();
                    }
                })
                .create().show();
            }
        }
        return super.onKeyDown(keyCode, event);
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
        new AlertDialog.Builder(MainActivity.this)
        .setTitle("时间到！！")
        .setMessage("哈哈哈哈啊哈哈！！！")
        .setPositiveButton("88", null)
        .setNegativeButton("哈罗", null)
        .create().show();
        reset();
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
    
}
