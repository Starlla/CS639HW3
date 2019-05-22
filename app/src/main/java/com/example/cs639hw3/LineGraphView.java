package com.example.cs639hw3;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class LineGraphView extends View {

    int mPointColor;
    int mMaxColor;
    int mLineColor;
    int mPathColor;
    Paint mMaxPointPaint;
    Paint mPointPaint;
    Paint mAxisPaint;
    Paint mTextPaint;
    Paint mLinePaint;
    Paint mPathPaint;
    float mPointRadius;
    private boolean showLineChecked;
    private boolean highlightIntegralChecked;

    private List<Integer> mData = new ArrayList<>();
    private List<String> xAxisList = new ArrayList<>();
    private List<Integer> yAxisList = new ArrayList<>();


    public LineGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LineGraphView);
        mPointColor = array.getColor(R.styleable.LineGraphView_pointColor, Color.parseColor("#008577"));
        mMaxColor = array.getColor(R.styleable.LineGraphView_maxColor, Color.parseColor("#00574B"));
        mLineColor = array.getColor(R.styleable.LineGraphView_lineColor, Color.parseColor("#003857"));
        mPathColor = array.getColor(R.styleable.LineGraphView_pathColor, Color.parseColor("#66008577"));
        mPointRadius = array.getDimension(R.styleable.LineGraphView_pointRadius, dpToPx(5));
        array.recycle();
        initPaints();
    }


    private void initPaints(){
        mPointPaint = new Paint();
        mPointPaint.setColor(mPointColor);

        mMaxPointPaint = new Paint();
        mMaxPointPaint.setColor(mMaxColor);

        mLinePaint = new Paint();
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(dpToPx(2));

        mPathPaint = new Paint();
        mPathPaint.setColor(mPathColor);
        mPathPaint.setStyle(Paint.Style.FILL);
//        mPathPaint.setStrokeWidth(dpToPx(2));


        mAxisPaint = new Paint();
        mAxisPaint.setColor(Color.BLACK);
        mAxisPaint.setStyle(Paint.Style.STROKE);
        mAxisPaint.setStrokeWidth(dpToPx(2));

        mTextPaint = new Paint();
        mTextPaint.setTextSize(dpToPx(15));
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStrokeWidth(dpToPx(1));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int offset = dpToPx(5);
        int startY = getHeight() - dpToPx(30);
        int startX = dpToPx(30);

        canvas.drawLine(startX, startY, getWidth() - offset, startY, mAxisPaint);//x axis
        canvas.drawLine(startX, offset, startX, startY, mAxisPaint);//y axis
        int xAxisLength = getWidth() - offset - startX;
        int yAxisLength = startY - offset;
        int xAxisLengthOfOneData = xAxisLength/5;

        if(!xAxisList.isEmpty()) {
            int maxData = Collections.max(mData);
            int yAxisMax = maxData;
            if(yAxisMax%5 == 0)
                yAxisMax +=5;
            else {
                while (yAxisMax % 5 != 0) yAxisMax++;
            }

            int finalYAxisMax = yAxisMax;

            //Set the function for get the X and Y position for current point
            CurrentXAxisPosition currentPoint = new CurrentXAxisPosition() {
                @Override
                public float getCurrentXAxisPosition(int i) {
                    return startX + xAxisLengthOfOneData / 2 + xAxisLengthOfOneData * i - mPointRadius / 2f ;
                }
                @Override
                public float getCurrentYAxisPosition(int i) {
                    return startY - (yAxisLength * (mData.get(i) / (float) finalYAxisMax));
                }
            };

            if(highlightIntegralChecked && xAxisList.size()>1) {
                Path path = new Path();
                path.moveTo(currentPoint.getCurrentXAxisPosition(0), startY);

                for (int i = 0; i < xAxisList.size(); i++) {

                    path.lineTo(currentPoint.getCurrentXAxisPosition(i), currentPoint.getCurrentYAxisPosition(i));

                }
                path.lineTo(currentPoint.getCurrentXAxisPosition(xAxisList.size()-1), startY);
                path.lineTo(currentPoint.getCurrentXAxisPosition(0), startY);
                canvas.drawPath(path, mPathPaint);
            }
            //Draw lines
            if(showLineChecked && xAxisList.size()>1){
                for( int j = 0; j < xAxisList.size()-1; j ++ ){
                    canvas.drawLine(currentPoint.getCurrentXAxisPosition(j),currentPoint.getCurrentYAxisPosition(j),
                            currentPoint.getCurrentXAxisPosition(j+1),currentPoint.getCurrentYAxisPosition(j+1),mLinePaint);
                }
            }

            //Draw X axis marks  and points
            for (int i = 0; i < xAxisList.size(); i++) {
                Rect rect = new Rect();
                mTextPaint.getTextBounds(xAxisList.get(i), 0, xAxisList.get(i).length(), rect);
                canvas.drawText(xAxisList.get(i), startX + xAxisLengthOfOneData / 2 + xAxisLengthOfOneData * i - rect.width() / 2f,
                        startY + dpToPx(20), mTextPaint);
                canvas.drawCircle(currentPoint.getCurrentXAxisPosition(i),
                        currentPoint.getCurrentYAxisPosition(i), mPointRadius, mPointPaint);
            }
            // Draw max point
            if(xAxisList.size()>1) {
                int i = mData.indexOf(maxData);
                canvas.drawCircle(currentPoint.getCurrentXAxisPosition(i),
                        currentPoint.getCurrentYAxisPosition(i), mPointRadius, mMaxPointPaint);

            }
            // Draw Y axis marks
            canvas.drawText("0", offset, startY,mTextPaint);
            Rect rect = new Rect();
            mTextPaint.getTextBounds(""+yAxisMax, 0, (""+yAxisMax).length(), rect);
            canvas.drawText(""+yAxisMax, offset, offset+rect.height(),mTextPaint);
        }

    }

    interface CurrentXAxisPosition {
        float getCurrentXAxisPosition(int i);
        float getCurrentYAxisPosition(int i);
    }

    public void addData(@NotNull List<Integer> data, @NotNull List<String> xList) {
        this.mData = data;
        this.xAxisList = xList;
        invalidate();
    }

    public void showLines(boolean show){
        showLineChecked = show;
    }

    public void  highlightIntegral(boolean show){
        highlightIntegralChecked = show;
    }

    private int dpToPx(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getContext().getResources().getDisplayMetrics());
    }

}
