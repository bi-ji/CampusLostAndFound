package com.edu.claf.receiver;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.style.BulletSpan;
import android.util.Log;
import android.widget.Toast;

import com.edu.claf.R;
import com.edu.claf.activity.CommentsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

public class MyPushMessageReceiver extends BroadcastReceiver {

    private JSONObject pushJson;

    private int count = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            count++;
            String message = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            try {
                pushJson = new JSONObject(message);
                System.out.println(pushJson.get("alert"));
                showMessageInNotifytion(context, (String) pushJson.get("alert"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(context, "客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING), Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showMessageInNotifytion(Context context, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("您有新的评论");
        builder.setContentText(message);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Notification notification = builder.getNotification();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults = Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        Intent notificationIntent = new Intent(context, CommentsActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notificationManager.notify(1, notification);

    }
}
