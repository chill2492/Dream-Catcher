package edu.vt.cs.cs5254.dreamcatcher;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class DreamEntrySwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private DreamFragment.DreamEntryAdapter mAdapter;

    public DreamEntrySwipeToDeleteCallback(DreamFragment.DreamEntryAdapter adapter) {
        super(0, ItemTouchHelper.LEFT );
        mAdapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mAdapter.deleteItem(position);

        }
    }

