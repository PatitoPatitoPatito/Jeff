package xyz.misterkozo.rcjeff;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class HelpActivity extends AppIntro
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showSkipButton(false);

        addSlide(AppIntroFragment.newInstance("My car moves but won't turn!", "You may have destroyed the engines, silly.", R.drawable.question, Color.RED));
        addSlide(AppIntroFragment.newInstance("I can't connect to the car!", "Double check that you're connected to 'raspinet'.", R.drawable.question, Color.MAGENTA));
        addSlide(AppIntroFragment.newInstance("I'm connected, car won't move", "Did you flip the switch at the bottom of the car?", R.drawable.question, Color.GRAY));
        addSlide(AppIntroFragment.newInstance("Connected, but picture is frozen", "Kill the connection and restart it. This can happen rarely.", R.drawable.question, Color.parseColor("#2ecc71")));

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}