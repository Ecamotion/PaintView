package com.ecamotion.paintviewtests;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Created by manuel.munoz on 4/9/15.
 */
public class PaintView extends RelativeLayout {

    public interface PaintListener{
        void onPaintEnd();
    }

    private int FRAME_RATE = 10;
    private int DURATION = 300;
    private int WIDTH;
    private int HEIGHT;
    private float radiusMax;
    private Paint paint;
    private float x;
    private float y;
    private int timer;
    private boolean animationRunning = false;
    private GestureDetector gestureDetector;
    private PaintListener listener;

    public PaintView(Context context) {
        super(context);
        init(context);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context){

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent event) {
                super.onLongPress(event);
                //animateRipple(event);
                sendClickEvent(true);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                x = e.getX();
                y = e.getY();
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                x = e.getX();
                y = e.getY();
                return true;
            }

        });

        //this.setDrawingCacheEnabled(true);
        this.setClickable(true);
        this.setWillNotDraw(false);
    }

    @Override
    public void onDraw(Canvas canvas){

        if(animationRunning){
            super.onDraw(canvas);
            if (DURATION <= timer * FRAME_RATE) {
                animationRunning = false;
                timer = 0;

                //if animation ended just paint the whole view
                //when the circle expands to much it disappears!
                canvas.drawRect(0,0, WIDTH, HEIGHT, paint);

                if(listener != null){
                    listener.onPaintEnd();
                }
                return;
            }
            else{
                postInvalidate();
            }

            float radius = (radiusMax * (((float) timer * FRAME_RATE) / DURATION));
            canvas.drawCircle(x, y, radius, paint);
            timer++;

        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
//            animateRipple(event);
            sendClickEvent(false);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        this.onTouchEvent(event);
        return super.onInterceptTouchEvent(event);
    }

    private void sendClickEvent(final Boolean isLongClick) {
        if (getParent() instanceof ListView) {
            final int position = ((ListView) getParent()).getPositionForView(this);
            final long id = ((ListView) getParent()).getItemIdAtPosition(position);
            if (isLongClick) {
                if (((ListView) getParent()).getOnItemLongClickListener() != null)
                    ((ListView) getParent()).getOnItemLongClickListener().onItemLongClick(((ListView) getParent()), this, position, id);
            } else {
                if (((ListView) getParent()).getOnItemClickListener() != null)
                    ((ListView) getParent()).getOnItemClickListener().onItemClick(((ListView) getParent()), this, position, id);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        WIDTH = w;
        HEIGHT = h;
    }

    public void animateRipple(){
        createAnimation(x, y);
    }
    public void animateRipple(MotionEvent event) {
        createAnimation(event.getX(), event.getY());
    }

    public void animateRipple(final float x, final float y) {
        createAnimation(x, y);
    }

    private void createAnimation(final float x, final float y) {
        if (!animationRunning) {

            float w = x > WIDTH /2 ? WIDTH - Math.abs(x - WIDTH) : WIDTH - x;
            float h = y > HEIGHT /2 ? HEIGHT - Math.abs(y - HEIGHT) : HEIGHT - y;
            radiusMax = Math.max(w, h);

            this.x = x;
            this.y = y;

            animationRunning = true;

            invalidate();
        }
    }

    public void setPaintColor(int color){
        paint.setColor(color);
    }

    public void setPaintColor(String color){
        paint.setColor(Color.parseColor(color));
    }

    public void setPaintListener(PaintListener listener){
        this.listener = listener;
    }

    public PaintListener getPaintListener(){
        return listener;
    }
}
