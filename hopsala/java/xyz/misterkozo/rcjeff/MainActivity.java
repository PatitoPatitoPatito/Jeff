package xyz.misterkozo.rcjeff;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView tv_clever;

    String[] sentences = {
            "RESERVED FOR LENGTH",
            "I ALWAYS HAVE A GOOD OL' TIME",
            "Is this loss?",
            "This is not Minecraft",
            "CLEVER TEXT",
            "This is the text for today!",
            "Swag it out",
            "2b||!2b",
            "Allahu akbar",
            "I can write anything I want here",
            "Are you alone?",
            "Open bob vagner, please",
            "There are many sentences\nbut this one is yours",
            "MainActivity extends AppCompatActivity!",
            "Are you up?",
            "Vladimir sees all",
            "What are you wearing?",
            "(((ZOD))) has taken over",
            "100% autonomous!!!!11",
            "40°42′42"+'″'+"N 74°00′45"+'″'+"W"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sentences[0] = "There are " + String.valueOf(sentences.length) + " possible sentences";
        tv_clever = findViewById(R.id.tv_clever);
        tv_clever.setText(sentences[new Random().nextInt(sentences.length)]);
    }

    public void main_server(View v) {
        Intent main_play = new Intent(this, ServerActivity.class);
        startActivity(main_play);
    }

    public void main_videos(View v) {
        Intent main_videos = new Intent(this, VideosActivity.class);
        startActivity(main_videos);
    }

    public void main_about(View v) {
        AboutDialog aboutDialog = new AboutDialog();
        aboutDialog.showDialog(this);
    }

    public void main_settings(View v) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void main_help(View v) {
        Intent main_help = new Intent(this, HelpActivity.class);
        startActivity(main_help);
    }

    public void main_exit(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        final YesNoDialog mAlert = new YesNoDialog(this);
        mAlert.setCancelable(false);
        mAlert.setTitle(getString(R.string.leaveTitle));
        mAlert.setMessage(getString(R.string.leaveText));
        mAlert.setPositveButton("Yes", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlert.dismiss();
                finish();
            }
        });

        mAlert.setNegativeButton("No", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlert.dismiss();
            }
        });

        mAlert.show();
    }
}
