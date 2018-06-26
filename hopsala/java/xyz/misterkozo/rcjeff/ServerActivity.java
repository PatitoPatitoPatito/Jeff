package xyz.misterkozo.rcjeff;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ServerActivity extends AppCompatActivity {

    public final static String TAG = "ServerActivity";

    private Server server_im, server_ct, server_db;

    private ImageView im_camera, /*im_status, */im_found/*, im_wait*/;
    private TextView /*tv_hsv, */tv_wait;

    private ImageButton bt_forward, bt_right, bt_backward, bt_left;
    private ImageButton bt_settings, bt_record;
    private ToggleButton sw_autonomous;
    private boolean[] pressed;
    private int[] viewCoords;
    private double ratioX, ratioY;
    private int im_height, im_width;
    private boolean record, debug;
    private GifGenerator gifGenerator;
    private ConstraintLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.loadLibrary("opencv_java3");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tmp2);
        layout = this.findViewById(R.id.serverlayout);

        /*WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String name = wifiInfo.getSSID();
        if (!name.equals("raspinet")) {
            Toast.makeText(this, "Please connect to the right network!", Toast.LENGTH_SHORT).show();
            finish();
        }*/

        im_camera = findViewById(R.id.im_camera);
        //im_status = findViewById(R.id.im_status);
        im_found  = findViewById(R.id.im_found);
        //im_wait = findViewById(R.id.im_wait);
        tv_wait = findViewById(R.id.tv_wait);
        //tv_hsv    = findViewById(R.id.tv_hsv);

        bt_settings = findViewById(R.id.ib_settings);
        bt_record = findViewById(R.id.ib_record);
        bt_forward = findViewById(R.id.bt_forward);
        bt_right = findViewById(R.id.bt_right);
        bt_backward = findViewById(R.id.bt_backward);
        bt_left = findViewById(R.id.bt_left);
        sw_autonomous = findViewById(R.id.sw_autonomous);
        pressed = new boolean[5];
        record = false;
        gifGenerator = new GifGenerator();

        try {
            //couldve made a while loop here but i wanted the user to know
            //try to init the servers
            //something's wrong, it'll fall back to the previous screen
            server_im = new Server(6969);
            server_ct = new Server(42069);
            server_db = new Server(6666);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(getApplicationContext(), "Port 42069, 6969 or 6666 is already in use.\nYou may want to slow down a bit!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initManualControls(); //ontouch listeners for buttons
        getImageCoords(); //get coordinates of image frame, just as it sounds
        initImage(); //ontouch listener for image frame

        new Thread(new Runnable() {
            public void run() {
                startServers(); //start servers
                while (true) {
                    /*try {
                        TimeUnit.MILLISECONDS.sleep(2000);
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }*/
                    //wait until connections
                    while (!server_ct.isConnected());
                    while (!server_im.isConnected());
                    while (!server_db.isConnected());

                    refreshFromSettings(); //check if debug, check colors, update
                    switchOn(); //display on properties

                    while (server_im.isConnected() && server_ct.isConnected()) {
                        server_ct.setButtonsNow(pressed);
                        try { //refresh every 65 miliseconds
                            TimeUnit.MILLISECONDS.sleep(65);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                        refreshOutputs(); //refresh image, etc
                    }

                    try {
                        switchOff(); //display off properties
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        }).start(); //start threadddddddddddd

    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        killServers();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    public void SettingsButton(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 2);
    }

    public void RecordToggle(View v) {
        ToggleRecording();
    }

    private void ToggleRecording() {
        if (record) {
            byte[] gif = gifGenerator.generateGIF();
            FileOutputStream outStream = null;
            try {
                File durr = new File(Environment.getExternalStorageDirectory().toString() + "/RCJeff");
                durr.mkdir();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            try{
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
                Date date = new Date();
                String path = Environment.getExternalStorageDirectory().toString() + "/RCJeff/" + formatter.format(date) + ".gif";
                outStream = new FileOutputStream(path);
                outStream.write(gif);
                outStream.close();
                Toast.makeText(this, "Recorded to "+ path, Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                Toast.makeText(this, "Failed to save recording!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            }
            record = false;
            bt_record.setImageResource(R.drawable.record_off);
        } else {
            gifGenerator.start();
            record = true;
            bt_record.setImageResource(R.drawable.record_on);
            Toast.makeText(this, "Recording!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            refreshFromSettings();
        }
    }

    private void refreshFromSettings() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        debug = sp.getBoolean("debug", false);
        Set<String> colorsSet = sp.getStringSet("colors", null);
        String[] colorsArr;
        if (colorsSet == null) {
            colorsArr = new String[1];
            colorsArr[0] = "none";
        } else {
            colorsArr = colorsSet.toArray(new String[colorsSet.size()]);
        }
        String colors = "";
        for (int i = 0; i < colorsArr.length; i++) {
            colors += colorsArr[i];
            if (i < colorsArr.length-1) {
                colors += ",";
            }
        }

        server_db.setColors(colors);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (server_im.isConnected()) {
            softKill();
        }
    }

    private void softKill() {
        server_ct.softKill();
        server_im.softKill();
        server_db.softKill();
    }

    private void view_disconnected() {
        //im_wait.setVisibility(View.VISIBLE);
        im_camera.setVisibility(View.GONE);
        //im_status.setVisibility(View.GONE);
        im_found.setVisibility(View.GONE);
        bt_settings.setVisibility(View.GONE);
        bt_record.setVisibility(View.GONE);
        bt_forward.setVisibility(View.GONE);
        bt_right.setVisibility(View.GONE);
        bt_backward.setVisibility(View.GONE);
        bt_left.setVisibility(View.GONE);
        //tv_hsv.setVisibility(View.GONE);
    }

    public void view_connected() {
        //im_wait.setVisibility(View.GONE);
        im_camera.setVisibility(View.VISIBLE);
        //im_status.setVisibility(View.VISIBLE);
        im_found.setVisibility(View.INVISIBLE);
        bt_settings.setVisibility(View.VISIBLE);
        bt_record.setVisibility(View.VISIBLE);
        bt_forward.setVisibility(View.VISIBLE);
        bt_right.setVisibility(View.VISIBLE);
        bt_backward.setVisibility(View.VISIBLE);
        bt_left.setVisibility(View.VISIBLE);
        //tv_hsv.setVisibility(View.VISIBLE);
    }

    public void initManualControls() {
        bt_forward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    pressed[0] = true;
                    bt_backward.setVisibility(View.INVISIBLE);
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!pressed[4]) {
                        pressed[0] = false;
                        bt_backward.setVisibility(View.VISIBLE);
                    }
                    return false;
                }

                return false;
            }
        });

        bt_right.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    pressed[1] = true;
                    bt_left.setVisibility(View.INVISIBLE);
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!pressed[4]) {
                        pressed[1] = false;
                        bt_left.setVisibility(View.VISIBLE);
                    }
                    return false;
                }

                return false;
            }
        });

        bt_backward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    pressed[2] = true;
                    bt_forward.setVisibility(View.INVISIBLE);
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!pressed[4]) {
                        pressed[2] = false;
                        bt_forward.setVisibility(View.VISIBLE);
                    }
                    return false;
                }

                return false;
            }
        });

        bt_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN ) {
                    pressed[3] = true;
                    bt_right.setVisibility(View.INVISIBLE);
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!pressed[4]) {
                        pressed[3] = false;
                        bt_right.setVisibility(View.VISIBLE);
                    }
                    return false;
                }

                return false;
            }
        });

        sw_autonomous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pressed[4] = true;
                    pressed[0] = false;
                    pressed[1] = false;
                    pressed[2] = false;
                    pressed[3] = false;
                    bt_forward.setVisibility(View.INVISIBLE);
                    bt_right.setVisibility(View.INVISIBLE);
                    bt_backward.setVisibility(View.INVISIBLE);
                    bt_left.setVisibility(View.INVISIBLE);
                } else {
                    pressed[4] = false;
                    bt_forward.setVisibility(View.VISIBLE);
                    bt_right.setVisibility(View.VISIBLE);
                    bt_backward.setVisibility(View.VISIBLE);
                    bt_left.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    public void getImageCoords() {
        viewCoords = new int[2];
        im_camera.getLocationOnScreen(viewCoords);

        ViewTreeObserver vto = im_camera.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                im_camera.getViewTreeObserver().removeOnPreDrawListener(this);
                im_height = im_camera.getMeasuredHeight();
                im_width = im_camera.getMeasuredWidth();
                return true;
            }
        });
    }

    public void initImage() {
        im_camera.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!server_im.isConnected())
                    return false;
                if (debug) {
                    if (server_im.getMat() == null) {
                        //tv_hsv.setText("(0,0,0)");
                        return false;
                    }
                    int touchX = (int) event.getX();
                    int touchY = (int) event.getY();

                    ratioX = ((float) im_width) / (float) 320;
                    ratioY = ((float) im_height) / (float) 240;

                    int imageX = (int) ((touchX - viewCoords[0]) / ratioX); // viewCoords[0] is the X coordinate
                    int imageY = (int) ((touchY - viewCoords[1]) / ratioY); // viewCoords[1] is the y coordinate

                    Mat mat = server_im.getMat();
                    if (mat != null) {
                        Mat hsv = new Mat();
                        Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_RGB2HSV);
                        double[] hsv_values = hsv.get(imageY, imageX);
                        String hue = String.valueOf(hsv_values[0]);
                        String sat = String.valueOf(hsv_values[1]);
                        String val = String.valueOf(hsv_values[2]);
                        //tv_hsv.setText('(' + hue + ',' + sat + ',' + val + ')');
                        Log.d("RCJeff", '(' + hue + ',' + sat + ',' + val + ')');
                    }
                } else {
                    ToggleRecording();
                }
                return false;
            }
        });
    }

    public void startServers() {
        if (server_im != null)
            server_im.start();
        if (server_ct != null)
            server_ct.start();
        if (server_db != null)
            server_db.start();
    }

    public void killServers() {
        if (server_im != null)
            server_im.kill();
        if (server_ct != null)
            server_ct.kill();
        if (server_db != null)
            server_db.kill();
    }

    public void switchOn() {
        tv_wait.post(new Runnable() {
            public void run() {
                tv_wait.setVisibility(View.GONE);
                layout.setBackgroundColor(Color.WHITE);
            }
        });
        im_camera.post(new Runnable() {
            public void run() {
                im_camera.setVisibility(View.VISIBLE);
            }
        });
        /*im_status.post(new Runnable() {
            public void run() {
                im_status.setVisibility(View.VISIBLE);
                im_status.setImageResource(R.drawable.connected);
            }
        });*/
        bt_settings.post(new Runnable() {
            public void run() {
                bt_settings.setVisibility(View.VISIBLE);
            }
        });
        bt_record.post(new Runnable() {
            public void run() {
                bt_record.setVisibility(View.VISIBLE);
            }
        });
        bt_forward.post(new Runnable() {
            public void run() {
                bt_forward.setVisibility(View.VISIBLE);
            }
        });
        bt_right.post(new Runnable() {
            public void run() {
                bt_right.setVisibility(View.VISIBLE);
            }
        });
        bt_backward.post(new Runnable() {
            public void run() {
                bt_backward.setVisibility(View.VISIBLE);
            }
        });
        bt_left.post(new Runnable() {
            public void run() {
                bt_left.setVisibility(View.VISIBLE);
            }
        });
        sw_autonomous.post(new Runnable() {
            public void run() {
                sw_autonomous.setVisibility(View.VISIBLE);
                sw_autonomous.setChecked(false);
            }
        });
        /*tv_hsv.post(new Runnable() {
            public void run() {
                tv_hsv.setVisibility(View.VISIBLE);
            }
        });*/
    }

    public void switchOff() {
        tv_wait.post(new Runnable() {
            public void run() {
                tv_wait.setVisibility(View.VISIBLE);
                layout.setBackgroundColor(Color.parseColor("#ff4081"));
            }
        });
        im_camera.post(new Runnable() {
            public void run() {
                im_camera.setVisibility(View.GONE);
            }
        });
        /*im_status.post(new Runnable() {
            public void run() {
                im_status.setImageResource(R.drawable.disconnected);
                im_status.setVisibility(View.GONE);
            }
        });*/
        im_found.post(new Runnable() {
            public void run() {
                im_found.setVisibility(View.GONE);
            }
        });
        bt_settings.post(new Runnable() {
            public void run() {
                bt_settings.setVisibility(View.GONE);
            }
        });
        bt_record.post(new Runnable() {
            public void run() {
                bt_record.setVisibility(View.GONE);
            }
        });
        bt_forward.post(new Runnable() {
            public void run() {
                bt_forward.setVisibility(View.GONE);
            }
        });
        bt_right.post(new Runnable() {
            public void run() {
                bt_right.setVisibility(View.GONE);
            }
        });
        bt_backward.post(new Runnable() {
            public void run() {
                bt_backward.setVisibility(View.GONE);
            }
        });
        bt_left.post(new Runnable() {
            public void run() {
                bt_left.setVisibility(View.GONE);
            }
        });
        sw_autonomous.post(new Runnable() {
            public void run() {
                sw_autonomous.setVisibility(View.GONE);
            }
        });
        /*tv_hsv.post(new Runnable() {
            public void run() {
                tv_hsv.setVisibility(View.GONE);
            }
        });*/
        if (record) {
            ToggleRecording();
        }
    }

    public void refreshOutputs() {
        im_camera.post(new Runnable() {
            public void run() {
                if (record) {
                    gifGenerator.addFrame(server_im.getBmp());
                }
                if (server_im.picChanged) {
                    im_camera.setImageBitmap(server_im.getBmp());
                    server_im.picChanged = false;
                }

            }
        });
        im_found.post(new Runnable() {
            public void run() {
                if (server_im.found) {
                    im_found.setVisibility(View.VISIBLE);
                } else {
                    im_found.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
