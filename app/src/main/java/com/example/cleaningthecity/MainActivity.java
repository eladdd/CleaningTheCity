package com.example.cleaningthecity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;

import android.os.Handler;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TableRow levelBackGround;
    private TextView scoreLabel;
    private TextView startLabel;
    private TextView levelLabel;
    private ImageView garbageTruckImage;
    private ImageView objects;
    private ImageView bonusObject;
    private ImageView black;
    private ImageView healKit;
    private ImageView[] lifes;
    private ImageView levelUpImage;
    private ImageView roadImage;
    private ArrayList<Integer> imgArr; // used to load the game 10 points objects
    private AnimationDrawable levelUpAnimation;
    private AnimationDrawable garbageTruckAnimation;
    private AnimationDrawable roadAnimation;
    private MusicButton musicButton;

    // Size
    private int frameHeight;
    private int truckSize;
    private int screenWidth;
    private int screenHeight;

    // Position
    private int truckY;
    private int objectsX;
    private int objectsY;
    private int bonusObjX;
    private int bonusObjY;
    private int blackX;
    private int blackY;
    private int healKitX;
    private int healKitY;

    // Speed
    private int truckSpeed;
    private int objectsSpeed;
    private int bonusObjSpeed;
    private int blackSpeed;
    private int healKitSpeed;

    // Score
    private int score = 0;
    private final int pointsPerLevel=100;

    //level**
    private int level = 1;
    private final int maxLevel = 11;

    //life
    private int lifeCount;

    // Initialize Class
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;
    Random rand = new Random(); // initialize over here and used in changePos (to avoid recreate every time)

    // Status Check
    private boolean action_flg = false;
    private boolean start_flg = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sound = new SoundPlayer(this);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        startLabel = (TextView) findViewById(R.id.startLabel);
        levelLabel = (TextView) findViewById(R.id.levelLabel);
        levelUpImage = (ImageView) findViewById(R.id.levelUpImage);
        garbageTruckImage = (ImageView) findViewById(R.id.garbageTruckImage);
        roadImage = (ImageView) findViewById(R.id.roadImage);
        objects = (ImageView) findViewById(R.id.objects);
        bonusObject = (ImageView) findViewById(R.id.objectBonus);
        black = (ImageView) findViewById(R.id.black);
        healKit = (ImageView) findViewById(R.id.objectHeal);
        levelBackGround = (TableRow) findViewById(R.id.tableBackGround);
        musicButton = (MusicButton) findViewById(R.id.music_btn);

        //load life objects (triple)
        lifes = new ImageView[3];
        lifes[0] = (ImageView) findViewById(R.id.heart);
        lifes[1] = (ImageView) findViewById(R.id.heart2);
        lifes[2] = (ImageView) findViewById(R.id.heart3);
        lifes[0].setTag(findViewById(R.id.heart));
        lifes[1].setTag(findViewById(R.id.heart));
        lifes[2].setTag(findViewById(R.id.heart));

        // load objects to array, initialize imgArr
        getResourcesImages();

        // Get screen size to better match for display game
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        // Now
        // Nexus4 Width: 768 Height:1280
        // Speed Truck:21 Objects(10 points):13 bonusObject(30 points):21 Black:17 healKit: 25

        truckSpeed = Math.round(screenHeight / 60);  // 1280 / 60 = 21.333... => 21
        objectsSpeed = Math.round(screenWidth / 60); // 768 / 60 = 12.8 => 13
        bonusObjSpeed = Math.round(screenWidth / 36);   // 768 / 36 = 21.333... => 21
        blackSpeed = Math.round(screenWidth / 45); // 768 / 45 = 17.06... => 17
        healKitSpeed = Math.round(screenWidth / 36); // 768/30 =25.6..=>25

        // Move to out of screen for user wont see it on the start screen
        objects.setX(-80);
        objects.setY(-80);
        bonusObject.setX(-80);
        bonusObject.setY(-80);
        black.setX(-80);
        black.setY(-80);
        healKit.setX(-80);
        healKit.setY(-80);

        //score&level label
        scoreLabel.setText(String.format("%s0", getString(R.string.game_score)));
        levelLabel.setText(String.format("%s1", getString(R.string.level)));

        //life count
        lifeCount = 3;

        //background road animation
        roadImage.setBackgroundResource(R.drawable.road_animation);
        roadAnimation = (AnimationDrawable) roadImage.getBackground();
        roadImage.setVisibility(View.VISIBLE);

        //truck animation
        garbageTruckImage.setBackgroundResource(R.drawable.garbage_truck_animation);
        garbageTruckAnimation = (AnimationDrawable) garbageTruckImage.getBackground();
        garbageTruckImage.setVisibility(View.VISIBLE);
    }

    private void getResourcesImages() {
        // in this function we load all the resources images of 10 points objects in game (dynamic loader)
        imgArr = new ArrayList<>();
        Field[] fields = R.drawable.class.getFields();
        for(Field field : fields){
            if (field.getName().startsWith("object"))
            {
                imgArr.add(getResources().getIdentifier(field.getName(), "drawable", getPackageName()));
            }
        }
    }


    public void changePos() {
        /*
            This function control the positions of the objects in the game,
            the start position(before press play) will be out of user view (width screen +x)
            when the game start(After press play) there will be random math function to place the object position
         */
        hitCheck();

        // normal objects
        objectsX -= objectsSpeed;

        if (objectsX < 0) {
            //Random rand = new Random();
            int numberOfObjects = imgArr.size();

            objects.setImageResource(imgArr.get(rand.nextInt(numberOfObjects))); // get random object of 10 points from array(rand not +1 cuz array its 0-4)
            objectsX = screenWidth + 20;
            objectsY = (int) Math.floor(Math.random() * (frameHeight - objects.getHeight()));
        }
        objects.setX(objectsX);
        objects.setY(objectsY);


        // Black
        blackX -= blackSpeed;
        if (blackX < 0) {
            blackX = screenWidth + 10;
            blackY = (int) Math.floor(Math.random() * (frameHeight - black.getHeight()));
        }
        black.setX(blackX);
        black.setY(blackY);


        // bonus recycle object
        bonusObjX -= bonusObjSpeed;
        if (bonusObjX < 0) {
            bonusObjX = screenWidth + 10000; //raise the number make it rare
            bonusObjY = (int) Math.floor(Math.random() * (frameHeight - bonusObject.getHeight()));
        }
        bonusObject.setX(bonusObjX);
        bonusObject.setY(bonusObjY);

        //Heal kit object
        healKitX -= healKitSpeed;
        if(lifeCount < 3 && score>70*level && healKitX<0)
        {
            // if user has 2 or less lifes , the score is higher according to the level so heal kit will appear
            healKitX = screenWidth + 20000; //raise the number make it rare
            healKitY = (int) Math.floor(Math.random() * (frameHeight - healKit.getHeight()));
        }
        healKit.setX(healKitX);
        healKit.setY(healKitY);

        // Move Truck
        if (action_flg) {
            // Touching
            truckY -= truckSpeed;

        } else {
            // Releasing
            truckY += truckSpeed;
        }

        // Check Truck position, and control the Truck position according to screen&frame limit
        if (truckY < 0) truckY = 0;
        if (truckY > frameHeight - truckSize) truckY = frameHeight - truckSize;//framgeheight-truckheight
        garbageTruckImage.setY(truckY);

        //update score
        scoreLabel.setText(String.format("%s%d", getString(R.string.game_score), score));

        //change game settings when user get next level
        if(score >= pointsPerLevel*level){
            level++;
            blackSpeed+=1;
            objectsSpeed +=1;
            bonusObjSpeed +=1;
            String setLevel = getString(R.string.level)+ level;
            levelLabel.setText(setLevel);

            switch (level){
                case ((maxLevel - 1) / 2):
                    //level 5
                    levelBackGround.setBackgroundResource(R.drawable.midlevel);
                    break;
                case (maxLevel - 1):
                    //level 10
                    levelBackGround.setBackgroundResource(R.drawable.maxlevel);
                    break;
                case (maxLevel):
                    //finish Game
                    endGame();
                    break;
                default:
                    break;
            }


            //level up animation (check if its heb/english phone to show the right animation)
            levelUpImage.setBackgroundResource(checkLang() ? R.drawable.level_up_animation : R.drawable.level_up_animation2);
            levelUpAnimation = (AnimationDrawable) levelUpImage.getBackground();
            levelUpImage.setVisibility(View.VISIBLE);
            levelUpAnimation.start();
            //stop after 2000ms
            timerDelayRemoveView(2000,levelUpImage);

        }
    }

    public boolean checkLang() {
        //return true if its english else hebrew
        //new locale en -if the code lang will change
        return Locale.getDefault().getLanguage().equals(new Locale("en").getLanguage());

    }

    public void timerDelayRemoveView(long time, final ImageView v) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //levelUpAnimation.stop();
                v.setVisibility(View.GONE);
            }
        }, time);

    }
    public void hitCheck() {

        // If the center of the ball is in the truck, it counts as a hit.

        // objects 10 points
        int objectsCenterX = objectsX + objects.getWidth() / 2;
        int objectsCenterY = objectsY + objects.getHeight() / 2;

        // 0 <= objectsCenterX <= truckWidth
        // truckY <= objectsCenterY <= truckY + truckHeight
        if (0 <= objectsCenterX && objectsCenterX <= truckSize &&
                truckY <= objectsCenterY && objectsCenterY <= truckY + truckSize) {

            score += 10;
            objectsX = -10;
            sound.playHitSound();
        }

        // bonus recycle object
        int bonusObjCenterX = bonusObjX + bonusObject.getWidth() / 2;
        int bonusObjCenterY = bonusObjY + bonusObject.getHeight() / 2;

        if (0 <= bonusObjCenterX && bonusObjCenterX <= truckSize &&
                truckY <= bonusObjCenterY && bonusObjCenterY <= truckY + truckSize) {

            score += 30;
            bonusObjX = -10;
            sound.playHitSound();
        }

        // heal kit object
        int healKitCenterX = healKitX + healKit.getWidth() / 2;
        int healKitCenterY = healKitY + healKit.getHeight() / 2;

        if (0 <= healKitCenterX && healKitCenterX <= truckSize &&
                truckY <= healKitCenterY && healKitCenterY <= truckY + truckSize) {
            for(ImageView check:lifes)
            {
                if(check.getTag().equals(R.drawable.heart_g))
                {
                    check.setImageResource(R.drawable.heart);
                    check.setTag(R.drawable.heart);//to avoid get into this condition again
                    lifeCount++;
                    break;
                }
            }

            healKitX = -10;
            sound.playHitSound();

        }

        // Black
        int blackCenterX = blackX + black.getWidth() / 2;
        int blackCenterY = blackY + black.getHeight() / 2;

        if (0 <= blackCenterX && blackCenterX <= truckSize &&
                truckY <= blackCenterY && blackCenterY <= truckY + truckSize) {
            lifes[lifeCount -1].setImageResource(R.drawable.heart_g);
            lifes[lifeCount -1].setTag(R.drawable.heart_g);
            lifeCount--;
            sound.playOverSound();

            blackX = -10;

            if(lifeCount == 0)
            {
                endGame();
            }
        }
    }

    public void endGame(){
        // Stop Timer
        timer.cancel();
        timer = null;

        // Show Result
        String name = getIntent().getStringExtra("PlayerName");
        musicButton.destroySound();

        // user finish the game (get lvl 10) so max score = 10*PPL
        score = level == maxLevel?(maxLevel - 1) * pointsPerLevel : score;

        // Bundle variables
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("SCORE", score);
        extras.putString("PlayerName",name);
        extras.putInt("Level",level);
        intent.putExtras(extras);
        startActivity(intent);
    }


    public boolean onTouchEvent(MotionEvent me) {
        if (!start_flg) {
            start_flg = true;
            roadAnimation.start();
            garbageTruckAnimation.start();

            // get frame height and truck height here,
            // Because the UI hasn't been set on the screen in OnCreate()
            FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
            frameHeight = frame.getHeight();
            truckY = (int)garbageTruckImage.getY();

            // square(height and width are the same.)
            truckSize = garbageTruckImage.getHeight();
            //truckHeight = truck.getHeight
            //truckWidth = truck.getWidth

            startLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);  // call changePos every 20 millisec


        } else {
            if (me.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;

            } else if (me.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }

        return true;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Disable Buttons , right now just return button but if needed more can do switch case
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if(event.getKeyCode()==KeyEvent.KEYCODE_BACK)
                return true;
        }

        return super.dispatchKeyEvent(event);
    }

}
