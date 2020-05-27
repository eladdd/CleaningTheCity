package com.example.cleaningthecity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class StartActivity extends AppCompatActivity {

    private Button startButton;
    private EditText nameEditText;
    private Button recordsButton;
    private TextView closeInstruction;
    private Dialog instructionsDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startButton = (Button)(findViewById(R.id.btn_start));
        recordsButton = (Button)(findViewById(R.id.btn_records));
        nameEditText = (EditText)(findViewById(R.id.editText_name));
        instructionsDialog = new Dialog(this);
    }

    public void checkInput(View view) {
        if (nameEditText.getText().toString().trim().length()==0) {
            //trim=remove spaces to avoid blank name
            nameEditText.setError(getString(R.string.check_enter_name));
        } else {
            startGame();
        }
    }

    public void startGame() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("PlayerName", nameEditText.getText().toString());
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void showRecords(View view){
        Intent intent = new Intent(StartActivity.this, RecordsActivity.class);
        startActivity(intent);
    }

    public void showInstructions(View view) {
        instructionsDialog.setContentView(R.layout.game_instructions);
        closeInstruction = (TextView) (instructionsDialog.findViewById(R.id.txt_close));
        closeInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instructionsDialog.dismiss();
            }
        });
        instructionsDialog.show();
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
