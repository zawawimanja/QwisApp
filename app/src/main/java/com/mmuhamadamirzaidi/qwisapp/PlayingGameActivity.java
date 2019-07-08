package com.mmuhamadamirzaidi.qwisapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mmuhamadamirzaidi.qwisapp.Common.Common;
import com.squareup.picasso.Picasso;

public class PlayingGameActivity extends AppCompatActivity implements View.OnClickListener{

    final static long INTERVAL = 1000; //1 second
    final static long TIMEOUT = 7000; //7 second
    int progressValue = 0;

    CountDownTimer mCountDown;

    int index = 0, score =0, thisQuestion = 0, totalQuestion = 0, correctAnswer;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference questions;

    ProgressBar progressBar;
    ImageView question_image;
    Button btnA, btnB, btnC, btnD;
    TextView txtScore, txtQuestionNum, question_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing_game);

        //Firebase
        database = FirebaseDatabase.getInstance();
        questions = database.getReference("Questions");

        //Views
        txtScore = (TextView)findViewById(R.id.txtScore);
        txtQuestionNum = (TextView)findViewById(R.id.txtTotalQuestion);
        question_text = (TextView)findViewById(R.id.question_text);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        btnA = (Button)findViewById(R.id.btnAnswerA);
        btnB = (Button)findViewById(R.id.btnAnswerB);
        btnC = (Button)findViewById(R.id.btnAnswerC);
        btnD = (Button)findViewById(R.id.btnAnswerD);

        btnA.setOnClickListener(this);
        btnB.setOnClickListener(this);
        btnC.setOnClickListener(this);
        btnD.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        mCountDown.cancel();
        if (index < totalQuestion) { //Still have questions in list
            //Choose correct answer
            score+=10;
            correctAnswer++;
            showQuestions(++index); //Next questions
        }
        else{
            //Choose wrong answer
            Intent intent = new Intent(this, DoneGameActivity.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE", score);
            dataSend.putInt("TOTAL", totalQuestion);
            dataSend.putInt("CORRECT", correctAnswer);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
        txtScore.setText(String.format("%d", score));
    }

    private void showQuestions(int index) {
        if (index < totalQuestion){
            thisQuestion++;
            txtQuestionNum.setText(String.format("%d / %d", thisQuestion, totalQuestion));
            progressBar.setProgress(0);
            progressValue=0;

            if (Common.ListQuestion.get(index).getIsImageQuestion().equals("true")){
                //If question is image
                Picasso.get().load(Common.ListQuestion.get(index).getQuestion()).into(question_image);

                question_image.setVisibility(View.VISIBLE);
                question_text.setVisibility(View.INVISIBLE);
            }
            else{
                question_text.setText(Common.ListQuestion.get(index).getQuestion());

                question_image.setVisibility(View.VISIBLE);
                question_text.setVisibility(View.INVISIBLE);
            }
            btnA.setText(Common.ListQuestion.get(index).getAnswerA());
            btnB.setText(Common.ListQuestion.get(index).getAnswerB());
            btnC.setText(Common.ListQuestion.get(index).getAnswerC());
            btnD.setText(Common.ListQuestion.get(index).getAnswerD());

            mCountDown.start(); //Start timer countdown
        }
        else{
            //If it is final question
            Intent intent = new Intent(this, DoneGameActivity.class);
            Bundle dataSend = new Bundle();
            dataSend.putInt("SCORE", score);
            dataSend.putInt("TOTAL", totalQuestion);
            dataSend.putInt("CORRECT", correctAnswer);
            intent.putExtras(dataSend);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        totalQuestion = Common.ListQuestion.size();

        mCountDown = new CountDownTimer(TIMEOUT, INTERVAL) {
            @Override
            public void onTick(long minisec) {
                progressBar.setProgress(progressValue);
                progressValue++;
            }

            @Override
            public void onFinish() {
                mCountDown.cancel();
                showQuestions(++index);
            }
        };
        showQuestions(++index);
    }
}