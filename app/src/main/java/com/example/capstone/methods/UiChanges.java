package com.example.capstone.methods;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class UiChanges {

    public static void buttonToBlack(EditText et, Button btn) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn.setBackgroundColor(Color.argb(255, 0, 0, 0));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
