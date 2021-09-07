package com.chenjimou.androidcoursedesign.ui;

import android.graphics.Rect;
import android.view.View;

import com.chenjimou.androidcoursedesign.base.BaseApplication;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MySpaceItemDecoration extends RecyclerView.ItemDecoration
{
    @Override
    public void getItemOffsets(
            @NonNull
                    Rect outRect,
            @NonNull
                    View view,
            @NonNull
                    RecyclerView parent,
            @NonNull
                    RecyclerView.State state)
    {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildLayoutPosition(view);
        int spacing = DisplayUtils.dip2px(BaseApplication.sApplication,8);

        outRect.left = spacing;
        outRect.right = spacing;
        outRect.top = spacing;
        if (position == parent.getAdapter().getItemCount() - 1)
        {
            outRect.bottom = spacing;
        }
    }
}
