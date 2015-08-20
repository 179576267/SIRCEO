package com.xiankezu.sirceo.widghts;

import com.xiankezu.sirceo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * 自定义带图片的圆形progressbar
 *
 * @author zhenfei.wang
 */
public class CircleImageProgressBar extends View {

    /**
     * 中间图片的缩放比例
     */
    private float scale = 0.6f;

    /**
     * 进度的最大值和当前进度
     */
    private int maxProgress = 100;
    private int progress = 0;
    /**
     * 图片
     */
    private Bitmap mSrc;
    /**
     * 控件的宽度
     */
    private int mWidth;
    /**
     * 控件的高度
     */
    private int mHeight;
    /**
     * 圆形边线的宽度
     */
    private int strokeWidth;
    /**
     * progress的颜色和背景颜色
     */
    private int progressColor;
    private int backgroundColor = 0xF000000;

    public CircleImageProgressBar(Context context) {
        this(context, null);
    }

    public CircleImageProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 初始化一些自定义的参数
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CircleImageProgressBar(Context context, AttributeSet attrs,
                                  int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ImageProgressView, defStyleAttr, 0);

        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ImageProgressView_src:
                    mSrc = BitmapFactory.decodeResource(getResources(),
                            a.getResourceId(attr, 0));
                    break;
                case R.styleable.ImageProgressView_strokeWidth:
                    strokeWidth = a.getDimensionPixelSize(attr, (int) TypedValue
                            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f,
                                    getResources().getDisplayMetrics()));// 获得圆形边框的宽度
                    break;

                case R.styleable.ImageProgressView_progressColor:
                    progressColor = a.getInt(attr, 0);// 获得进度条的颜色
                    break;
                case R.styleable.ImageProgressView_backgroundColor:
                    backgroundColor = a.getInt(attr, 0);// 获得背景颜色
                    break;

            }
        }
        a.recycle();
    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @param source
     * @param min
     * @return
     */
    private Bitmap createCircleImage(Bitmap source, int min) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(min / 2, min / 2, (float) (scale * min / 2), paint);// 先画的决定形状
        /**
         * 使用SRC_IN，参考上面的说明
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        Matrix matrix = new Matrix();
        //设置Matrix以(px,py)为轴心进行缩放，sx、sy为X、Y方向上的缩放比例。
        matrix.setScale(scale, scale, min / 2, min / 2);
        canvas.drawBitmap(source, matrix, paint);// 后画的是填充
        return target;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public Bitmap getmSrc() {
        return mSrc;
    }


    /**
     * 绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {

        int min = Math.min(mWidth, mHeight);
        /**
         * 长度如果不一致，按小的值进行压缩
         */
        mSrc = Bitmap.createScaledBitmap(mSrc, min, min, false);

        if (strokeWidth != 0) {
            Paint paint = new Paint();
            paint.setColor(backgroundColor);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE); // 绘制空心圆
            paint.setStrokeWidth(strokeWidth);

            /**
             * 先画progress背景
             */
            canvas.drawCircle(min / 2 + strokeWidth, min / 2 + strokeWidth, min
                    / 2 + strokeWidth / 2, paint);

            paint.setColor(progressColor);
            float arc = (float) ((progress * 1.0 / maxProgress) * 360);
            canvas.drawArc(new RectF(strokeWidth / 2, strokeWidth / 2, (int) (min + strokeWidth * 1.5), (int) (min + strokeWidth * 1.5)),
                    -90, arc, false, paint);
        }
        canvas.drawBitmap(createCircleImage(mSrc, min), strokeWidth,
                strokeWidth, null);

    }

    /**
     * 计算控件的高度和宽度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mWidth = specSize;
        } else if (specMode == MeasureSpec.AT_MOST)// wrap_content
        {
            // 由图片决定的宽
            int desireByImg = getPaddingLeft() + getPaddingRight()
                    + mSrc.getWidth();
            mWidth = Math.min(desireByImg, specSize);
        }

        /***
         * 设置高度
         */

        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mHeight = specSize;
        } else if (specMode == MeasureSpec.AT_MOST)// wrap_content
        {
            int desire = getPaddingTop() + getPaddingBottom()
                    + mSrc.getHeight();
            mHeight = Math.min(desire, specSize);
        }
        setMeasuredDimension(mWidth + strokeWidth * 2, mHeight + strokeWidth
                * 2);

    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }


    public void setmSrc(Bitmap mSrc) {
        this.mSrc = mSrc;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.invalidate();
    }

    /**
     * 非ＵＩ线程调用
     */
    public void setProgressNotInUiThread(int progress) {
        this.progress = progress;
        this.postInvalidate();
    }
}
