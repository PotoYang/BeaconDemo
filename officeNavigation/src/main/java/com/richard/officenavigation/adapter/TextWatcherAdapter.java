package com.richard.officenavigation.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TextWatcherAdapter implements TextWatcher {

    private final EditText view;
    private final TextWatcherListener listener;
    public TextWatcherAdapter(EditText editText, TextWatcherListener listener) {
        this.view = editText;
        this.listener = listener;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        listener.onTextChanged(view, s.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // pass
    }

    @Override
    public void afterTextChanged(Editable s) {
        // pass
    }

    public interface TextWatcherListener {

        void onTextChanged(EditText view, String text);

    }

}