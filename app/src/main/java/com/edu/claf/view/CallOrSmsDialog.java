package com.edu.claf.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

@SuppressWarnings("ALL")
public class CallOrSmsDialog {
    private static  AlertDialog.Builder builder;

    public static void getCallDialog(final Context context, final String phonenum){
        builder = new AlertDialog.Builder(context);
        builder.setTitle("提示!");
        builder.setMessage("确定要打电话给Ta:" + phonenum);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phonenum));
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public static void getSendSmsDialog(final Context context, final String phonenum){
        builder = new AlertDialog.Builder(context);
        builder.setTitle("提示!");
        builder.setMessage("确定要发短信给Ta:" + phonenum);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("smsto:"+phonenum));
                context.startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }
}
