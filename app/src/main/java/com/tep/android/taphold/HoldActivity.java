package com.tep.android.taphold;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.tep.android.taphold.DAO.GamerDAO;
import com.tep.android.taphold.beans.Gamer;
import com.tep.android.taphold.utils.GoogleAnalyticsBuilder;
import com.tep.android.taphold.utils.QuoteManager;

import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class HoldActivity extends ActionBarActivity implements TextToSpeech.OnInitListener {
    private static final String PREFS_NAME = "com.tep.android.taphold";
    private SharedPreferences mSettings;

    private TextToSpeech mTextToSpeech;
    private ProgressDialog mProgressDialog;
    private TextView mQuoteText;
    private ImageView mCircle;
    private Date mStartTime;
    private Date mStopTime;
    private long mPoints;
    private boolean isPushing = false;

    private Handler quoteHandler = new Handler();
    private Runnable quoteRunnable = new Runnable() {
        @Override
        public void run() {
            changeQuoteTextAndSpeak();
            quoteHandler.postDelayed(this, 2500);
        }
    };

    private void changeQuoteTextAndSpeak() {
        fadeOutText();
        mQuoteText.setText(QuoteManager.getRandomQuote());
        fadeInText();

        if (mTextToSpeech != null) {
            if (!mTextToSpeech.isSpeaking()) {

                Random rnd = new Random();
                int randomNumber = rnd.nextInt(10);

                if (randomNumber % 7 == 0) {
                    mTextToSpeech.speak("less", TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    mTextToSpeech.speak("more", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
    }

    private Handler animationHandler = new Handler();
    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            playAnimationIfNotPushing();
            animationHandler.postDelayed(animationRunnable, 1500);
        }
    };

    private void playAnimationIfNotPushing() {
        if (!isPushing) {
            YoYo.with(Techniques.Pulse)
                    .duration(600)
                    .playOn(mCircle);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hold);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mSettings = getSharedPreferences(PREFS_NAME, 0);

        initializeLayout();

        animationHandler.postDelayed(animationRunnable, 1000);
    }

    private void initializeLayout() {
        mTextToSpeech = new TextToSpeech(this, this);
        mCircle = (ImageView) findViewById(R.id.circle_image);
        mQuoteText = (TextView) findViewById(R.id.quotes_text);
        LinearLayout hold = (LinearLayout) findViewById(R.id.hold_layout);
        hold.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        animationHandler.removeCallbacks(animationRunnable);
                        fadeOutCircle();
                        isPushing = true;
                        startCounting();
                        return true;
                    }

                    case MotionEvent.ACTION_UP: {
                        stopSpeach();
                        stopCounting();
                        resetGame();
                        return true;
                    }

                    default:
                        return false;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleAnalyticsBuilder.getInstance().sendActivity("HoldActivity", this);
    }

    private void fadeOutText() {
        YoYo.with(Techniques.FadeOut)
                .duration(350)
                .playOn(mQuoteText);
    }

    private void fadeInText() {
        YoYo.with(Techniques.FadeIn)
                .duration(350)
                .playOn(mQuoteText);
    }

    private void fadeOutCircle() {
        YoYo.with(Techniques.FadeOut)
                .duration(1000)
                .playOn(mCircle);
    }

    private void startCounting() {
        mStartTime = new Date();
        quoteHandler.postDelayed(quoteRunnable, 1000);
    }

    private void stopSpeach() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }

    private void stopCounting() {
        mStopTime = new Date();
        quoteHandler.removeCallbacks(quoteRunnable);
        saveOrDismissPoints();
    }

    private void resetGame() {
        isPushing = false;
        fadeInCircle();
        fadeOutText();
        mTextToSpeech = new TextToSpeech(this, this);
        animationHandler.postDelayed(animationRunnable, 1000);
    }

    private void fadeInCircle() {
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(mCircle);
    }

    private void saveOrDismissPoints() {
        mPoints = (mStopTime.getTime() - mStartTime.getTime()) / 1000;
        buildAlertDialog();
    }

    private void buildAlertDialog() {
        final EditText input = new EditText(this);
        String secondsOrSecond;
        if (mPoints == 1) {
            secondsOrSecond = " second";
        } else {
            secondsOrSecond = " seconds";
        }
        String lastUsername = mSettings.getString("username", null);

        if (lastUsername != null) {
            input.setText(lastUsername);
        } else {
            input.setHint("your name");
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(HoldActivity.this);

        dialog.setView(input);
        dialog.setTitle(Long.toString(mPoints) + secondsOrSecond);
        dialog.setMessage("Congratz! Do you want to want to fight with others?");
        dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String username = input.getText().toString();
                saveUsernameLocal(username);
                insertPoints(username);
            }
        });
        dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void saveUsernameLocal(String username) {
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("username", username);
        editor.apply();
    }

    private void insertPoints(String username) {
        Gamer gamer = new Gamer(username, mPoints);

        mProgressDialog = ProgressDialog.show(this, "Saving your score :)",
                "Hold on...", true);
        new InsertPointsTask().execute(gamer);
    }

    private void showGlobalLeaderboard() {
        Intent globalLeaderboard = new Intent(this, LeaderboardActivity.class);
        globalLeaderboard.putExtra("points", Long.toString(mPoints));

        startActivity(globalLeaderboard);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPushing) {
            stopSpeach();
            stopCounting();
            resetGame();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            mTextToSpeech.setLanguage(Locale.US);
        } else {
            mTextToSpeech = null;
        }
    }

    private class InsertPointsTask extends AsyncTask<Gamer, Void, Void> {

        @Override
        protected Void doInBackground(Gamer... params) {
            Gamer gamer = params[0];

            GamerDAO gamerDAO = new GamerDAO(HoldActivity.this);

            try {
                gamerDAO.insert(gamer);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mProgressDialog.dismiss();
            showGlobalLeaderboard();
        }
    }
}