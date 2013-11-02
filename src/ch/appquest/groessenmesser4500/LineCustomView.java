package ch.appquest.groessenmesser4500;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;

@SuppressLint("DrawAllocation")
public class LineCustomView extends SurfaceView {

    public LineCustomView(Context context, AttributeSet attrs) {
            super(context, attrs);
    }

    protected void onDraw(Canvas canvas){
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStrokeWidth(3);
            canvas.drawLine(0, getHeight()/2, getWidth(), getHeight()/2, paint);
    }

}
