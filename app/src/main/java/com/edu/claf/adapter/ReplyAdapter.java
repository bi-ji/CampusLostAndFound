package com.edu.claf.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.claf.R;
import com.edu.claf.bean.Reply;

public class ReplyAdapter extends BaseAdapter {

    private int resourceId;
    private List<Reply> list;
    private LayoutInflater inflater;
    private TextView replyContent;
    private SpannableString ss;
    private Context context;

    public ReplyAdapter(Context context, List<Reply> list
            , int resourceId) {
        this.list = list;
        this.context = context;
        this.resourceId = resourceId;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Reply bean = list.get(position);
        View view = null;
        ViewHoler holder = null;
        if (convertView == null) {
            holder = new ViewHoler();
            view = View.inflate(context, resourceId, null);
            holder.tvReplyContent = (TextView) view.findViewById(R.id.replyContent);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHoler) view.getTag();
        }

        final String replyNickName = bean.getReplyUserNickName();
        final String commentNickName = bean.getCommentNickName();
        String replyContentStr = bean.getReplyContent();
        ss = new SpannableString(replyNickName + "回复" + commentNickName
                + "：" + replyContentStr);
        System.out.println(replyNickName + "回复" + commentNickName
                + "：" + replyContentStr + position);
        ss.setSpan(new ForegroundColorSpan(Color.BLUE), 0,
                replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.BLUE), replyNickName.length() + 2,
                replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new TextSpanClick(true), 0,
                replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new TextSpanClick(true), replyNickName.length() + 2,
                replyNickName.length() + commentNickName.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.tvReplyContent.setText(ss);
        holder.tvReplyContent.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    private final class TextSpanClick extends ClickableSpan {
        private boolean status;

        public TextSpanClick(boolean status) {
            this.status = status;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(android.view.View v) {
            String msgStr = "";
            if (status) {
                msgStr = "我是回复的人";
            } else {
                msgStr = "我是评论的人";
            }
            Toast.makeText(context, msgStr, Toast.LENGTH_SHORT).show();
        }
    }

    private class ViewHoler {
        TextView tvReplyContent;
    }

}
