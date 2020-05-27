package com.example.cleaningthecity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ResultActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private TextView scoreLabel;
    private TextView highScoreLabel;
    private TextView endGame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        db = new DatabaseHelper(this);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        highScoreLabel = (TextView) findViewById(R.id.highScoreLabel);
        endGame = (TextView) findViewById(R.id.endGame);

        // get variables info from bundle/intent
        String name = getIntent().getStringExtra("PlayerName");
        int score = getIntent().getIntExtra("SCORE", 0);
        int level = getIntent().getIntExtra("Level",1);
        scoreLabel.setText(String.format(Locale.getDefault(),"%d", score));
        checkResults(level);

        // update sqlite data with the new result
        db.insertDataRecords(score,name);

        SharedPreferences settings = getSharedPreferences("HIGH_SCORE", Context.MODE_PRIVATE);
        int highScore = settings.getInt("HIGH_SCORE", 0);

        if (score > highScore) {
            highScoreLabel.setText(String.format(Locale.getDefault(),"%s%d", getString(R.string.high_score), highScore));
            // Update High Score
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", score);
            editor.apply();
        } else {
            highScoreLabel.setText(String.format(Locale.getDefault(),"%s%d", getString(R.string.high_score), highScore));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // share score/result
        if (item.getItemId() == R.id.share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.titleShare) + scoreLabel.getText().toString());
            startActivity(Intent.createChooser(sharingIntent,getString(R.string.share)));
        }

        return true;
    }

    private void checkResults(int level) {
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.my_layout);
        TextView endText = (TextView) findViewById(R.id.endGameText);

        if(level==11)
        {
            endGame.setText(getString(R.string.win_game));
            endText.setText(getString(R.string.text_win));
            myLayout.setBackgroundResource(R.drawable.winbg);
        }
        else{
            endGame.setText(getString(R.string.game_over));
            endText.setText(getString(R.string.text_lose));
            myLayout.setBackgroundResource(R.drawable.losebg);
        }
    }

    public void backMainMenu(View view) {
        // back to start activity
        startActivity(new Intent(getApplicationContext(), StartActivity.class));
    }

    public void tryAgain(View view) {
        // new game
        String name = getIntent().getStringExtra("PlayerName");
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("PlayerName",name);
        intent.putExtras(extras);
        startActivity(intent);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Disable Return Button
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if(event.getKeyCode()==KeyEvent.KEYCODE_BACK)
                return true;
        }

        return super.dispatchKeyEvent(event);
    }
}
