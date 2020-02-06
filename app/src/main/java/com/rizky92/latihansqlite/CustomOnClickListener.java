package com.rizky92.latihansqlite;

import android.view.View;

public class CustomOnClickListener implements View.OnClickListener {

    private int pos;
    private OnItemClickCallback onItemClickCallback;

    public CustomOnClickListener(int pos, OnItemClickCallback onItemClickCallback) {
        this.pos = pos;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public void onClick(View view) {
        onItemClickCallback.onItemClicked(view, pos);
    }

    public interface OnItemClickCallback {
        void onItemClicked(View view, int pos);
    }
}
