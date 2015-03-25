package com.tep.android.taphold.utils;

import java.util.Random;

public class QuoteManager {
    private static String[] mQuotes = {"Are you still there?", "Time has a wonderful way of showing us what really matters.", "Doesn’t it hurt yet?",
    "This is a thumb-fight, you know?", "Slowly but surely ….", "You can’t rush something you want to last forever.",
    "Almost there.", "Don’t give up! Not yet.", "Even miracles take a little time.", "Take a deep breath. :)", "Be patient. Eventually something interesting will happen.",
    "Love is patient.", "Your arm is getting stronger.", "Do you feel it?", "It’s over 9000!!!", "Ultra combo!", "Amazing job!", "Yeeeaaahhh!",
    "Warm up your fingers!", "Are you at work?", "You are so productive.", "You’re already more persistent than us!", "There can be only one.", "Keep on! You’re awesome.", "#gettep"};

    public static String getRandomQuote() {
        Random rnd = new Random();
        int randomNumber = rnd.nextInt(mQuotes.length);

        return mQuotes[randomNumber];
    }
}
