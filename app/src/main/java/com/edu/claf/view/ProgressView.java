package com.edu.claf.view;

import android.app.ProgressDialog;
import android.content.Context;


public class ProgressView extends android.app.ProgressDialog{

    static ProgressDialog progress;


    public ProgressView(Context context) {
        super(context);

    }

    public ProgressView(Context context, int theme) {
        super(context, theme);
    }


    public static void getProgressDialog(Context context,String message){
        progress = new ProgressDialog(context);
        progress.setMessage(message);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    public static void progressDialogDismiss(){
        if (progress != null) {
            progress.dismiss();
        }
    }


}
