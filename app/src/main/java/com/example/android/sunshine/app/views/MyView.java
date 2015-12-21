package com.example.android.sunshine.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by johno on 12/20/2015.
 */
public class MyView extends View {

    private Rect rectangle;
    private Paint paint;

    public MyView(Context context) {
        super(context);

        int x = 100;
        int y = 100;
        int sideLength = 300;

        // Create a rectangle that we'll draw later:
        rectangle = new Rect(x, y, sideLength, sideLength);

        // Create the Paint and set its color:
        paint = new Paint();
        paint.setColor(Color.YELLOW);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size = 0;

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (width > height){
            size = height;
        } else {
            size = height;
        }

        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.BLACK);
        canvas.drawRect(rectangle, paint);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
    }
}
