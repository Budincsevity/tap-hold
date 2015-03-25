package com.tep.android.taphold;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tep.android.taphold.DAO.GamerDAO;
import com.tep.android.taphold.beans.Gamer;
import com.tep.android.taphold.utils.DividerItemDecoration;
import com.tep.android.taphold.utils.GoogleAnalyticsBuilder;
import com.tep.android.taphold.utils.LeaderboardAdapter;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class LeaderboardActivity extends ActionBarActivity {

    private RecyclerView mGamersList;
    private ProgressBar mProgress;
    private String mPoints;
    private TextView mPointsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        initializeToolbar();
        getExtra();
        initializeLayout();

        String secondsOrSecond;
        if (mPoints.equals("1")) {
            secondsOrSecond = " second";
        } else {
            secondsOrSecond = " seconds";
        }
        mPointsText.setText("You held it for " + mPoints + secondsOrSecond);

        new GetTop10GamersTask().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleAnalyticsBuilder.getInstance().sendActivity("LeaderboardActivity", this);
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getExtra() {
        Intent intent = getIntent();
        mPoints = intent.getStringExtra("points");
    }

    private void initializeLayout() {
        mProgress = (ProgressBar) findViewById(R.id.progress_ring);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(LeaderboardActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mGamersList = (RecyclerView) LeaderboardActivity.this.findViewById(R.id.gamers);
        mGamersList.setLayoutManager(layoutManager);
        mGamersList.setItemAnimator(new DefaultItemAnimator());
        mGamersList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mPointsText = (TextView) findViewById(R.id.points_text);

        Button shareButton = (Button) findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePoints();
            }
        });
    }

    private void sharePoints() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mPoints);
        shareIntent.setType("text/plain");

        startActivity(shareIntent);
    }

    private class GetTop10GamersTask extends AsyncTask<Void, Void, List<Gamer>> {

        @Override
        protected List<Gamer> doInBackground(Void... params) {

            GamerDAO gamerDAO = new GamerDAO(LeaderboardActivity.this);
            List<Gamer> gamers = null;

            try {
                gamers = gamerDAO.getTop10Gamers();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return gamers;
        }

        @Override
        protected void onPostExecute(List<Gamer> gamers) {
            super.onPostExecute(gamers);

            if (gamers != null) {
                mProgress.setVisibility(View.INVISIBLE);
                LeaderboardAdapter leaderboardAdapter = new LeaderboardAdapter(gamers, R.layout.gamers_list_item);
                mGamersList.setAdapter(leaderboardAdapter);
            }
        }
    }
}
