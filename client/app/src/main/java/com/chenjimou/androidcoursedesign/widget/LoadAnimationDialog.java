package com.chenjimou.androidcoursedesign.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.chenjimou.androidcoursedesign.R;
import com.chenjimou.androidcoursedesign.databinding.DialogAnimationLoadBinding;

import androidx.annotation.NonNull;

public class LoadAnimationDialog extends Dialog
{
    private LoadAnimationDialog(@NonNull Context context)
    {
        super(context);
    }

    public static LoadAnimationDialog init(Context context, String title)
    {
        DialogAnimationLoadBinding binding = DialogAnimationLoadBinding.inflate(LayoutInflater.from(context));

        LoadAnimationDialog dialog = new LoadAnimationDialog(context);
        dialog.setContentView(binding.getRoot());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        binding.title.setText(title);

        return dialog;
    }
}
