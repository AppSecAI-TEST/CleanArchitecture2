package com.cleanarchitecture.shishkin.common.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.common.utils.ViewUtils;

public class ColorPickerView extends View {

    private Bitmap bitmap;

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        bitmap = ((BitmapDrawable) ViewUtils.getDrawable(context, R.drawable.colorwheel)).getBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.scale(0.7f, 0.7f);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }
}
