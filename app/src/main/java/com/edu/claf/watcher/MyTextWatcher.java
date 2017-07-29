package com.edu.claf.watcher;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.edu.claf.BaseApplication;

public class MyTextWatcher implements TextWatcher {

    private CharSequence temp;
    private int editStart;
    private int editEnd;
    private EditText editText;
    private int charCount;
    Handler handler;

    public MyTextWatcher(EditText editText,int charCount,Handler handler){
        this.editText = editText;
        this.charCount = charCount;
        this.handler = handler;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        temp = charSequence;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        editStart = editText.getSelectionStart();
        editEnd = editText.getSelectionEnd();
        Message message = Message.obtain();
        message.obj = temp.length();
        message.what = 222;
        handler.sendMessage(message);
        if (temp.length() > charCount){
            Toast.makeText(BaseApplication.getContext(), "您输入的字数已经超过了限制！", Toast.LENGTH_SHORT).show();
            editable.delete(editStart-1,editEnd);
            int tempSelection = editStart;
            editText.setText(editable);
            editText.setSelection(tempSelection);
        }
    }
}
