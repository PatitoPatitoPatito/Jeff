package xyz.misterkozo.rcjeff;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideosAdapter extends ArrayAdapter<Video> {

    private final Activity context;
    private List<Video> videos;

    private ImageView im_thumbnail;
    private TextView tv_title, tv_time;
    private ImageButton bt_delete;

    public VideosAdapter(Activity context, List<Video> videos) {
        super(context, R.layout.item_video, videos);
        this.context = context;
        this.videos = videos;
        Log.d("RCJeff", videos.size()+"");
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.item_video, null, true);
        im_thumbnail = rowView.findViewById(R.id.im_thumbnail);
        tv_title = rowView.findViewById(R.id.tv_title);
        tv_time = rowView.findViewById(R.id.tv_time);
        bt_delete = rowView.findViewById(R.id.bt_delete);

        final Video video = videos.get(position);
        final int pos = position;
        if (video.thumb.contains("https:")) {
            Glide
                    .with(context)
                    .load(video.thumb)
                    .apply(new RequestOptions().placeholder(R.drawable.nosignal))
                    .into(im_thumbnail);
            if (!video.token.equals(FirebaseInstanceId.getInstance().getToken())) {
                bt_delete.setVisibility(View.GONE);
            }
            tv_title.setText(limitStr(video.name, 10));
        } else {
            im_thumbnail.setImageURI(Uri.parse(video.url));
            tv_title.setText("#" + String.valueOf(position + 1));
        }
        tv_time.setText(video.date);

        bt_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final YesNoDialog mAlert = new YesNoDialog(context);
                mAlert.setCancelable(false);
                mAlert.setTitle(context.getString(R.string.leaveTitle));
                mAlert.setMessage("Please confirm the deletion of video " + Integer.valueOf(pos+1));
                mAlert.setPositveButton("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAlert.dismiss();
                        if (video.token == null) {
                            File fdelete = new File(video.url);
                            if (fdelete.exists()) {
                                if (fdelete.delete()) {
                                    Toast.makeText(context, "File deleted!", Toast.LENGTH_SHORT).show();
                                    videos.remove(pos);
                                    VideosActivity.lst_offline.setAdapter(new VideosAdapter(context, videos));
                                } else {
                                    // not deleted :(
                                }
                            }
                        } else {
                            VideosActivity.dbRef.child(video.key).removeValue();
                            Toast.makeText(context, "File removed!", Toast.LENGTH_SHORT).show();
                            videos.remove(pos);
                            VideosActivity.lst_offline.setAdapter(new VideosAdapter(context, videos));
                        }
                    }
                });

                mAlert.setNegativeButton("No", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAlert.dismiss();
                        //Do want you want
                    }
                });

                mAlert.show();
            }
        });

        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent videos_video = new Intent(context, VideoActivity.class);
                videos_video.putExtra("URL", video.url);
                videos_video.putExtra("key", video.key);
                videos_video.putExtra("opKey", video.token);
                if (video.thumb.contains("https:"))
                    videos_video.putExtra("name", video.name);
                else
                    videos_video.putExtra("name", video.date);
                context.startActivity(videos_video);
            }
        });

        return rowView;
    }

    private String limitStr(String str, int limit) {
        if (str.length() <= limit)
            return str;
        str = str.substring(0,limit);
        str += "..";
        return str;
    }

}
