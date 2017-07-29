package com.edu.claf.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.edu.claf.R;

import cn.bmob.v3.BmobUser;

public class SelectPicPopupWindow extends PopupWindow {


    private static final int CHANGE_PHOTO = 4;

    private static final int REQUESTCODE_TAKE = 1;
    private static final int REQUESTCODE_PICK = 2;
    private static final int REQUESTCODE_CUTTING = 3;
    Button btnTakePhoto;
    Button btnPickPhoto;
    Button btnCancel;
    LinearLayout popLayout;

    private View mMenuView;
    private BmobUser mUser;
    private String mPhoneNumber;

    public SelectPicPopupWindow() {

    }

    public SelectPicPopupWindow(Activity context, View.OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.activity_select_pic_popup_window, null);
        btnTakePhoto = (Button) mMenuView.findViewById(R.id.btn_take_photo);
        btnPickPhoto = (Button) mMenuView.findViewById(R.id.btn_pick_photo);
        btnCancel = (Button) mMenuView.findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        btnTakePhoto.setOnClickListener(itemsOnClick);
        btnPickPhoto.setOnClickListener(itemsOnClick);
        this.setContentView(mMenuView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.AnimBottom);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        this.setBackgroundDrawable(dw);
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }
}
