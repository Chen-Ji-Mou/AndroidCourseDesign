package com.chenjimou.androidcoursedesign.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MySpaceItemTouchCallback extends ItemTouchHelper.SimpleCallback
{
    onItemDeleteListener mListener;

    public MySpaceItemTouchCallback()
    {
        super(0, ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(
            @NonNull
                    RecyclerView recyclerView,
            @NonNull
                    RecyclerView.ViewHolder viewHolder,
            @NonNull
                    RecyclerView.ViewHolder target)
    {
        return false;
    }

    @Override
    public void onSwiped(
            @NonNull
                    RecyclerView.ViewHolder viewHolder, int direction)
    {
        if (mListener != null)
            mListener.onItemDelete(viewHolder.getLayoutPosition());
    }

    @Override
    public boolean isItemViewSwipeEnabled()
    {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled()
    {
        return false;
    }

    public interface onItemDeleteListener
    {
        void onItemDelete(int position);
    }

    public void setOnItemDeleteListener(onItemDeleteListener listener)
    {
        mListener = listener;
    }
}
