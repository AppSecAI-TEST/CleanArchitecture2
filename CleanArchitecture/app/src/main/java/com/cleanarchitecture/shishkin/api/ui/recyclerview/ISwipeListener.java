package com.cleanarchitecture.shishkin.api.ui.recyclerview;

import android.support.v7.widget.RecyclerView;

public interface ISwipeListener {

    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);

}
