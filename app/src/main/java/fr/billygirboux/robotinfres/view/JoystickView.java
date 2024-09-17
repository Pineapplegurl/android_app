package fr.billygirboux.robotinfres.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fr.billygirboux.robotinfres.R;

public class JoystickView extends View {

    private Paint paintStroke;
    private Paint paintFill;

    private float xJoy;
    private float yJoy;

    public JoystickView(Context context) {
        super(context);
        this.init();
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        this.paintStroke = new Paint();
        this.paintStroke.setStyle(Paint.Style.STROKE);
        this.paintStroke.setStrokeWidth(4);
        this.paintStroke.setColor(getResources().getColor(R.color.white));


        this.paintFill = new Paint();
        this.paintFill.setStyle(Paint.Style.FILL);
        this.paintFill.setStrokeWidth(4);
        this.paintFill.setColor(getResources().getColor(R.color.white));

        addGesture();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {

        float width = getWidth();
        float height = getHeight();


        canvas.save();
        canvas.translate(width/2, height/2);
        for (int i=0; i<4; i++) {
            canvas.drawLine(0, 120, 0, width/2, paintStroke);
            canvas.drawLine(0, width/2, -20, width/2 - 20, paintStroke);
            canvas.drawLine(0, width/2, 20, width/2 - 20, paintStroke);
            canvas.rotate(90);
        }
        canvas.restore();

        canvas.translate(xJoy + width/2, yJoy + width/2);
        canvas.drawCircle(0,0, 60, paintFill);
        canvas.drawCircle(0,0, 70, paintStroke);

    }

    private boolean inMove;

    private void addGesture() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                float width = getWidth();

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (x > width/2 - 60 && x < width/2 + 60
                        && y > width/2 - 60 && y < width/2 +60
                    ) {
                        inMove = true;
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    if (inMove) {
                        xJoy = x - width/2;
                        yJoy = y - width/2;
                        invalidate();
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    inMove = false;
                    xJoy = 0;
                    yJoy = 0;
                    invalidate();
                }

                return true;
            }
        });
    }
}
