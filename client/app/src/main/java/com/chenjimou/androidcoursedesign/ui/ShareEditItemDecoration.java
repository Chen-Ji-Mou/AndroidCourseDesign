package com.chenjimou.androidcoursedesign.ui;

import android.graphics.Rect;
import android.view.View;

import com.chenjimou.androidcoursedesign.base.BaseApplication;
import com.chenjimou.androidcoursedesign.utils.DisplayUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class ShareEditItemDecoration extends RecyclerView.ItemDecoration
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

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        int spanIndex = layoutParams.getSpanIndex();
        int position = parent.getChildAdapterPosition(view);
        int spacing = DisplayUtils.dip2px(BaseApplication.sApplication,8);

        outRect.bottom = spacing;
        outRect.right = spacing;
        if (position == 0 || position == 1 || position == 2)
        {
            outRect.top = spacing;
        }
        else
        {
            outRect.top = 0;
        }

        if (spanIndex % 3 == 0)
        {
            outRect.left = spacing;
        }
    }
}
