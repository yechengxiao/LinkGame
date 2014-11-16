package com.johnbear724.linkgame;


import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher.ViewFactory;
import com.johnbear724.linkgame.core.GameService;
import com.johnbear724.linkgame.sound.GameSound;
import com.johnbear724.linkgame.view.GameView;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    public static final int Victory = 0x123;
    public static final int TIMER = 0x1234;
    public static final int SCOUR_1 = 0x111;
    public static final int SCOUR_2 = 0x112;
    public static final int SCOUR_3 = 0x113;
    public static final int START_TEXT_INVISIBLE = 0x3;

    private GameView gameView;
    private ProgressBar progressBar;
    private TextView timeText;
    private TextView scoreText;
    private TextView startText;
    private TextView victoryTime;
    private TextView victoryScore;
    private ImageView refreshImage;
    private ImageView searchImage;
    private TextSwitcher refreshSwitcher;
    private TextSwitcher searchSwitcher;
    private TextSwitcher textSwitcher;
    private RelativeLayout timeUpLayout;
    private RelativeLayout victoryLayout;
    private LayoutAnimationController layoutAni;
    private AlertDialog pauseDialog;
    private GameService gameService;
    private GameSound gameSound;
    private Timer timer = new Timer();
    private int time;
    private int score;
    private int comboTime;
    private int combo;
    private int refreshNum;
    private int searchNum;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch(msg.what) {
                case Victory :
                    showVictory();
                    break;
                case TIMER :
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
                case SCOUR_1 :
                    score(1);
                    break;
                case SCOUR_2 :
                    score(2);
                    break;
                case SCOUR_3 :
                    score(3);
                    break;
                case START_TEXT_INVISIBLE :
                    hideLayout();
            }

        };
    };;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    
    @Override
    protected void onResume() {
        if(time != 0) {
            startTimer(time);
        }
        gameSound.autoResume(gameView.isStart());
        super.onResume();
    }
    
    @Override
    protected void onPause() {
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
        victoryScore = (TextView) findViewById(R.id.victory_score);
        victoryTime = (TextView) findViewById(R.id.victory_time);
        textSwitcher = (TextSwitcher) findViewById(R.id.text_switcher);
        refreshSwitcher = (TextSwitcher) findViewById(R.id.refresh_switcher);
        searchSwitcher = (TextSwitcher) findViewById(R.id.search_switcher);
        timeUpLayout = (RelativeLayout) findViewById(R.id.time_up_layout);
        victoryLayout = (RelativeLayout) findViewById(R.id.victory_layout);
        refreshImage = (ImageView) findViewById(R.id.refresh);
        searchImage = (ImageView) findViewById(R.id.search);
        
        OnClickListener refreshListener = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(refreshNum == 2 || refreshNum == 1) {
                    refreshNum--;
                    refreshSwitcher.setText(refreshNum + "");
                    gameView.refresh();
                }
            }
        };
        OnClickListener searchListener = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if(searchNum == 2 || searchNum == 1) {
                    searchNum--;
                    searchSwitcher.setText(searchNum + "");
                    gameView.search();
                }
            }
        };
        ViewFactory viewFactory = new ViewFactory() {
            
            @Override
            public View makeView() {
                TextView tv = new TextView(MainActivity.this);
                tv.setGravity(Gravity.CENTER);
                return tv;
            }
        };
        refreshSwitcher.setFactory(viewFactory);
        searchSwitcher.setFactory(viewFactory);
        refreshImage.setOnClickListener(refreshListener);
        refreshSwitcher.setOnClickListener(refreshListener);
        searchImage.setOnClickListener(searchListener);
        searchSwitcher.setOnClickListener(searchListener);
        
        textSwitcher.setFactory(viewFactory);
        textSwitcher.setText("Ready!");
        gameSound = new GameSound(this);
        timer = new Timer();
        pauseDialog = new AlertDialog.Builder(MainActivity.this)
        .setTitle("游戏暂停！！")
//        .setMessage("游戏暂停！！")
        .setPositiveButton("退出游戏", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                MainActivity.this.finish();
                System.exit(0);
            }
        })
        .setNeutralButton("新游戏", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startGame(100);
            }
        } )
        .setNegativeButton("继续游戏", null)
        .create();
        pauseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            
            @Override
            public void onDismiss(DialogInterface dialog) {
                onResume();
            }
        });
        gameView.setGame(gameSound, handler);
        layoutAni = AnimationUtils.loadLayoutAnimation(this, R.anim.time_up_layout_ani);
        timeUpLayout.setVisibility(View.INVISIBLE);
        victoryLayout.setVisibility(View.INVISIBLE);
        startText = (TextView) findViewById(R.id.startText);
        startText.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
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
            onPause();
            pauseDialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }
    
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // TODO Auto-generated method stub
//        switch(resultCode) {
//        case PauseDialog.NEW_GAME :
//            startGame(100);
//            break;
//        case PauseDialog.EXIT :
//            finish();
//            break;
//        }
//    }

    /**
     * 启动计时器
     * @param time 需要倒计时的时间
     */
    public void startTimer(int time) {
        this.time = time + 1;
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            
            @Override
            public void run() {
                handler.sendEmptyMessage(TIMER);
            }
        }, 0, 1000);
    }

    /**
     * 设置计时器的时间显示
     * @param t 需要显示的事件
     */
    public void setTimer(int t) {
        time = t;
        timeText.setText(t + "");
        progressBar.setProgress(t);
    }

    /**
     * 显示时间结束提示
     */
    public void showTimeUp() {
        gameSound.play(GameSound.TIME_UP, 1, 1, 0, 0, 1);
        timeUpLayout.setVisibility(View.VISIBLE);
        timeUpLayout.setLayoutAnimation(layoutAni);
    }
    
    public void showVictory() {
        if(timer != null) {
            timer.cancel();
            timer = null;
        }
        gameView.gameOver();
        gameSound.play(GameSound.WIN, 1, 1, 0, 0, 1);
        victoryLayout.setVisibility(View.VISIBLE);
        victoryLayout.setLayoutAnimation(layoutAni);
        victoryTime.setText("" + (100 - time));
        victoryScore.setText("" + score);
    }

    /**
     * 显示获得分数的提示
     * @param level 连击等级
     */
    public void score(int level) {
        int origin = score;
        combo++;
        comboTime = 0;
        score += (level * 100 + (combo - 1) * 100);
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
                scoreText.setText((Integer)arg0.getAnimatedValue() + "");
            }
        });
        ani.setDuration(800);
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
        refreshNum = 2;
        searchNum = 2;
        refreshSwitcher.setText(refreshNum + "");
        searchSwitcher.setText(searchNum + "");
        gameView.gameOver();
    }
    
    public void startGame(int t) {
        reset();
        startTimer(t);
        gameView.startGame();
    }
    
    public void hideLayout() {
        startText.setVisibility(View.INVISIBLE);
        timeUpLayout.setVisibility(View.INVISIBLE);
        victoryLayout.setVisibility(View.INVISIBLE);
    }
    
    public void timeUpPlayAgain(View v) {
        startGame(100);
    }
    
}
