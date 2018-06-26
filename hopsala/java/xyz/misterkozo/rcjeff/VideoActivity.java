package xyz.misterkozo.rcjeff;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VideoActivity extends AppCompatActivity {

    public final static String TAG = "VideoActivity";

    ImageView im_recorded;
    ProgressBar pb_upload;
    TextView tv_uploadstatus;
    ImageButton bt_upload;
    EditText et_comment;
    Button bt_post;
    ListView lst_comments;
    TextView tv_befirst;
    public static FirebaseDatabase database;
    public static DatabaseReference dbRef;
    StorageReference stRef;

    String url, name, key, opKey;
    Uri uri, thumburi;
    List<String> comments;

    Bitmap bitmap;
    final NameDialog nameDialog = new NameDialog();

    private int origHeight = 0;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        view = findViewById(R.id.videolayout);
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    origHeight = view.getHeight();
                }
            });
        }

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("videos");
        stRef = FirebaseStorage.getInstance().getReference();

        im_recorded = findViewById(R.id.im_recorded);
        pb_upload = findViewById(R.id.pb_upload);
        tv_uploadstatus = findViewById(R.id.tv_uploadstatus);
        bt_upload = findViewById(R.id.bt_upload);
        et_comment = findViewById(R.id.et_comment);
        bt_post = findViewById(R.id.bt_post);
        lst_comments = findViewById(R.id.lst_comments);
        tv_befirst = findViewById(R.id.tv_befirst);

        url = getIntent().getStringExtra("URL");
        name = getIntent().getStringExtra("name");
        key = getIntent().getStringExtra("key");
        opKey = getIntent().getStringExtra("opKey");

        Glide //loads recorded video into frame
                .with(this)
                .load(url)
                .apply(new RequestOptions().placeholder(R.drawable.nosignal))
                .into(im_recorded);

        tv_uploadstatus.setText(name);

        if (isOnline() && !isUpload()) { //check if online and not uploaded
            bt_upload.setVisibility(View.VISIBLE); //show upload button
        }

        if (isUpload()) { //if it's uploaded
            et_comment.setVisibility(View.VISIBLE); //gimme the ability to comment
            bt_post.setVisibility(View.VISIBLE);
            tv_befirst.setVisibility(View.VISIBLE);

            fetchComments(); //fetch the comments and display them
        }

        if (isUpload()) {
            findViewById(R.id.videolayout).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (bottom < origHeight) { //aesthetics, hide unnecessary stuff while using keyboard
                        lst_comments.setVisibility(View.GONE);
                        tv_befirst.setVisibility(View.GONE);
                    } else if (bottom > oldBottom) {
                        if (comments != null && comments.size() > 0) {
                            lst_comments.setVisibility(View.VISIBLE);
                        } else {
                            tv_befirst.setVisibility(View.VISIBLE);
                        }

                        if (comments == null) {
                            fetchComments();
                        }
                    }
                }
            });
        }
    }

    public void video_upload(View v) {
        nameDialog.showDialog(this);
        tv_uploadstatus.setVisibility(View.GONE);
        bt_upload.setVisibility(View.GONE);
        nameDialog.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                tv_uploadstatus.setVisibility(View.VISIBLE);
                bt_upload.setVisibility(View.VISIBLE);
                if (nameDialog.name != null) {
                    upload(nameDialog.name);
                }
            }

        });
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void upload(final String name) {
        String url = getIntent().getStringExtra("URL");
        final Uri file = Uri.parse("file://"+url);
        String[] longlongman = url.split("/");
        final String bigfilename = longlongman[longlongman.length-1];
        final String filename = bigfilename.split("\\.")[0];
        StorageReference fileRef = stRef.child(filename+".gif");
        try
        {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , Uri.parse("file://"+url));
        }
        catch (Exception e)
        {
            Log.e("RCJeff", e.toString());
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = stRef.child("file://"+filename+".jpg").putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                thumburi = taskSnapshot.getDownloadUrl();
            }
        });

        fileRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        uri = taskSnapshot.getDownloadUrl();
                        tv_uploadstatus.setVisibility(View.VISIBLE);
                        pb_upload.setVisibility(View.GONE);
                        tv_uploadstatus.setText("Uploaded successfully!");
                        Video video = new Video();
                        video.url = uri.toString();
                        video.thumb = thumburi.toString();
                        if (name.equals(""))
                            video.name = filename;
                        else
                            video.name = name;
                        video.date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());
                        video.token = FirebaseInstanceId.getInstance().getToken();
                        dbRef.push().setValue(video);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + "% done");
                        //tv_uploadstatus.setText("Status: " + String.valueOf(currentprogress)+"% uploaded");
                        tv_uploadstatus.setVisibility(View.GONE);
                        pb_upload.setVisibility(View.VISIBLE);
                        pb_upload.setProgress((int)progress);
                        bt_upload.setEnabled(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pb_upload.setVisibility(View.GONE);
                        tv_uploadstatus.setText("Failed to upload!");
                        bt_upload.setEnabled(true);
                    }
                });
    }

    private boolean isUpload() {
        return url.contains("https:");
    }

    private void fetchComments() {
        dbRef.child("last").setValue(System.currentTimeMillis());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.getKey().equals(key)) {
                        Iterator<DataSnapshot> commentsIterator =
                                userSnapshot.child("comments").getChildren().iterator();
                        while (commentsIterator.hasNext()) {
                            comments.add(commentsIterator.next().getValue().toString());
                            //commentsIteratorcommentsIterator.next();
                        }
                        showComments();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, error.toString());
            }
        });
    }

    private void showComments() {
        if (comments != null && comments.size() > 0) {
            tv_befirst.setVisibility(View.GONE);
            lst_comments.setVisibility(View.VISIBLE);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, comments);
            lst_comments.setAdapter(adapter);
        } else {
            tv_befirst.setVisibility(View.VISIBLE);
            lst_comments.setVisibility(View.GONE);
        }
    }

    public void video_postcomm(View v) {
        String comment = et_comment.getText().toString();
        if (comment.trim().length() == 0) {
            Toast.makeText(this, "Can't post an empty comment, you weirdo!", Toast.LENGTH_SHORT).show();
            return;
        }

        dbRef.child(key).child("comments").push().setValue(comment);
        pushTo(opKey, "comment","Someone's just commented on \"" + name+"\"!");
        et_comment.setText("");
        Toast.makeText(this, "Posted!", Toast.LENGTH_SHORT).show();
    }

    private final static String API_KEY =
            "CLASSIFIEDDD";
    private void pushTo(final String to, final String type, final String message) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    JSONObject jGcmData = new JSONObject();
                    JSONObject jData = new JSONObject();
                    jData.put("type", type);
                    jData.put("message", message);
                    jGcmData.put("to", to);
                    jGcmData.put("data", jData);

                    // Create connection to send GCM Message request.
                    URL url = new URL("https://android.googleapis.com/gcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "key=" + API_KEY);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    // Send GCM message content.
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(jGcmData.toString().getBytes());

                    InputStream inputStream = conn.getInputStream();
                    Log.d(TAG, inputStream.toString());
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        };

        thread.start();
    }
}
