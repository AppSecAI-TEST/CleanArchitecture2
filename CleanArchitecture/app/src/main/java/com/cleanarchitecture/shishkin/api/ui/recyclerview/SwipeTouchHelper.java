package com.cleanarchitecture.shishkin.api.ui.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.lang.ref.WeakReference;

public class SwipeTouchHelper extends ItemTouchHelper.SimpleCallback {
    private AbstractRecyclerViewAdapter mMovieAdapter;
    private WeakReference<ISwipeListener> mListener;

    public SwipeTouchHelper(AbstractRecyclerViewAdapter movieAdapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);

        mMovieAdapter = movieAdapter;
    }

    public SwipeTouchHelper(AbstractRecyclerViewAdapter movieAdapter, ISwipeListener listener) {
        this(movieAdapter);

        if (listener != null) {
            mListener = new WeakReference<>(listener);
        }
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mMovieAdapter.remove(viewHolder.getAdapterPosition());

        if (mListener != null && mListener.get() != null) {
            mListener.get().onSwiped(viewHolder, direction);
        }
    }

}