package xyz.misterkozo.rcjeff;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideosActivity extends AppCompatActivity {

    private boolean allowed = false, online = false;

    public static FirebaseDatabase database;
    public static DatabaseReference dbRef;

    TextView tv_empty;
    public static ListView lst_offline;
    CompoundButton sw_online;
    List<Video> videos;
    VideosAdapter videosAdapter;
    Boolean doneYet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        ActivityCompat.requestPermissions(VideosActivity.this,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("videos");
        dbRef.keepSynced(false);

        tv_empty = findViewById(R.id.tv_empty);
        lst_offline = findViewById(R.id.lst_offline);
        sw_online = findViewById(R.id.sw_online);

        fetchVideosOffline();

        sw_online.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    fetchVideosOnline();
                    online = true;
                } else {
                    fetchVideosOffline();
                    online = false;
                }
                refreshVideos();
            }
        });


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (sw_online.isChecked()) {
                    videos = new ArrayList<>();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        if (!userSnapshot.getKey().equals("last")) {
                            videos.add(userSnapshot.getValue(Video.class));
                            videos.get(videos.size() - 1).key = userSnapshot.getKey();
                        }
                    }
                    refreshVideos();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                sw_online.setChecked(false);
                sw_online.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void fetchVideosOnline() {
        dbRef.child("last").setValue(System.currentTimeMillis());
    }

    public void fetchVideosOffline() {
        try {
            String path = Environment.getExternalStorageDirectory().toString() + "/RCJeff";
            File directory = new File(path);
            File[] files = directory.listFiles();

            videos = new ArrayList<>();
            for (File file : files) {
                String name = "video";
                String date = file.getName().split("\\.")[0];
                Video video = new Video(file.getAbsolutePath(), file.getAbsolutePath(), name, date);
                if (file.getName().split("\\.")[1].toLowerCase().equals("gif"))
                    videos.add(video);
            }
        } catch (Exception e) {
            videos = new ArrayList<>();
        }

        refreshVideos();
    }

    public void refreshVideos() {
        if (videos.isEmpty()) {
            tv_empty.setVisibility(View.VISIBLE);
            lst_offline.setVisibility(View.GONE);
        } else {
            tv_empty.setVisibility(View.GONE);
            lst_offline.setVisibility(View.VISIBLE);
        }

        if (!isOnline() && sw_online.isChecked()) {
            sw_online.setChecked(false);
            sw_online.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
        }

        videosAdapter = new VideosAdapter(this, videos);
        videosAdapter.notifyDataSetChanged();
        lst_offline.setAdapter(videosAdapter);
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        if (sw_online.isChecked()) {
            sw_online.setChecked(false);
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    allowed = true;
                    fetchVideosOffline();
                } else {
                    allowed = false;
                }
                return;
            }
        }
    }
}
