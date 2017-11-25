package xyz.leezoom.o.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.github.chrisbanes.photoview.PhotoView;


public class OImageView extends PhotoView {

    public final static int CIRCLE = 444;
    public final static int RECT = 555;
    private int mStyle = CIRCLE;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mWhiteWidth = 50;

    private Bitmap mBitmap;


    public OImageView(Context context) {
        super(context);
    }

    public OImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        //drawMask();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);

        int centerX = width / 2;
        int centerY = getTop() + height / 2;
        switch (mStyle) {
            case CIRCLE:
                Path path1 = new Path();
                path1.addRect(0, 0, width, height, Path.Direction.CW);
                path1.addCircle(centerX, centerY, width / 2 - mWhiteWidth, Path.Direction.CW);
                path1.setFillType(Path.FillType.EVEN_ODD);
                canvas.drawPath(path1, mPaint);
                break;
            case RECT:
                //canvas.drawRect(getX(), getY(), getX() + width, getY() + height, mPaint);
                Path path2 = new Path();
                path2.addRect(0, 0, width, height, Path.Direction.CW);
                path2.addRect(mWhiteWidth, mWhiteWidth, width - mWhiteWidth, height - mWhiteWidth, Path.Direction.CCW);
                path2.setFillType(Path.FillType.EVEN_ODD);
                canvas.drawPath(path2, mPaint);
                break;
            default:
                break;
        }
    }

    public void setStyle(int style) {
        mStyle = style;
        invalidate();
    }

    public void setWhiteWidth(int width) {
        if (width >= 50) {
            mWhiteWidth = width;
        }
        invalidate();
    }

}

                /*
                Path path = new Path();

                path.moveTo(0, centerY);
                path.quadTo(0, 0, centerX, 0);
                path.lineTo(0, 0);

                path.moveTo(0, centerY);
                path.quadTo(0, height, centerX, height);
                path.lineTo(0, height);

                path.moveTo(width , centerY);
                path.quadTo(width, height, centerX ,height);
                path.lineTo(width, height);

                path.moveTo(width, centerY);
                path.quadTo(width, 0, centerX, 0);
                path.lineTo(width, 0);
                //path.arcTo(0, getTop() + centerY, centerX, 0, 0, 90, false);
                canvas.drawPath(path, mPaint);
                //canvas.drawCircle(width / 2, getTop() + height / 2, width / 2 - 50, mPaint);
                */
