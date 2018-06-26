package xyz.misterkozo.rcjeff;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class NameDialog {
    Dialog dialog;
    String name = null;

    public void showDialog(final Activity activity){
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_name);

        final EditText et_name = dialog.findViewById(R.id.et_name);
        //this.nameListener = nameListener;

        Button noButton = dialog.findViewById(R.id.bt_cancel);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button yesButton = dialog.findViewById(R.id.bt_upload);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //VideoActivity.upload(et_name.getText().toString());
                name = et_name.getText().toString();
                //nameListener.secondary();
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}