package com.carouseldemo.main;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;


public class BrushView extends View {
    private Paint brush = new Paint();
    private Path path = new Path();
    static boolean reset_flag=false;
    public BrushView(Context context) {
        super(context);
        brush.setAntiAlias(true);
        brush.setColor(Color.BLACK);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(5f);
       
        
        if(reset_flag==true){
        	//reset the path
                path.reset();
                //invalidate the view
                postInvalidate();
                reset_flag=false;
                
            }
  
    }
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();
        // Checks for the event that occurs
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            path.moveTo(pointX, pointY);
            return true;
        case MotionEvent.ACTION_MOVE:
            path.lineTo(pointX, pointY);
            break;
        default:
            return false;
        }       
         // Force a view to draw again
        postInvalidate();
        return false;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, brush);
    }
 
    
}