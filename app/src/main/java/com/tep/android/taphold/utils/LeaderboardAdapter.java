package com.tep.android.taphold.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tep.android.taphold.R;
import com.tep.android.taphold.beans.Gamer;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private List<Gamer> mGamers;
    private int mRowLayout;

    public LeaderboardAdapter(List<Gamer> gamers, int rowLayout) {
        mGamers = gamers;
        mRowLayout = rowLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(mRowLayout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.usernameText.setText(mGamers.get(position).getUsername());
        String secondsOrSecond;

        if (mGamers.get(position).getPoints() == 1.0) {
            secondsOrSecond = " second";
        } else {
            secondsOrSecond = " seconds";
        }

        holder.pointsText.setText(Long.toString(mGamers.get(position).getPoints()) + secondsOrSecond);
    }

    @Override

    public int getItemCount() {
        return mGamers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView usernameText;
        public TextView pointsText;

        public ViewHolder(View itemView) {
            super(itemView);

            usernameText = (TextView) itemView.findViewById(R.id.username_text);
            pointsText = (TextView) itemView.findViewById(R.id.points_text);
        }
    }
}
