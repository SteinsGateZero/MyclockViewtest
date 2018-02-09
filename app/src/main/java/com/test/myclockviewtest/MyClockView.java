package com.test.myclockviewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

/**
 * Created by SetinsGateZero on 2018/2/5.
 */

public class MyClockView extends View {
    private Paint paint;//画笔
    private int mainColor = Color.parseColor("#000000");//画笔颜色
    private float mWidth, mHeight;//视图宽高
    private float arcRa = 0;//圆半径
    private Double rr = 2 * Math.PI / 60;//2π即360度的圆形分成60份,一秒钟与一分钟
    private Double rr2 = 2 * Math.PI / 12;//2π圆形分成12份,圆形显示12个小时的刻度
    private PointF secondStartPoint, minuteStartPoint, hourStartPoint;//秒,分,时的坐标点
    private int startSecond, startMinute, startHour;//初始化时秒,分,时获取的系统时间
    private Rect textBound = new Rect();//字体被全部包裹的最小的矩形边框

    public MyClockView(Context context) {
        super(context);
        init();
    }

    public MyClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;//获得宽度
        mHeight = h;//获得高度

        //以最短的一边为所要绘制圆形的直径
        if (mWidth > mHeight) {
            arcRa = mHeight / 2;//以最短的一边算出半径
        } else {
            arcRa = mWidth / 2;//以最短的一边算出半径
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void init() {
        paint = new Paint();//初始化画笔
        paint.setColor(mainColor);//设置颜色
        //  paint.setAntiAlias(true);//抗锯齿(性能影响)
        paint.setStyle(Paint.Style.STROKE);//设置画笔
        paint.setTextSize(45);//设置字体大小
        secondStartPoint = new PointF(arcRa, 0);//初始化坐标点
        hourStartPoint = new PointF(arcRa, 0);
        minuteStartPoint = new PointF(arcRa, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //①获取系统时间
        getCurrentTime();

        //②当前时间时分秒分别所占的份数(角度),即为上面rr,rr2所得到的每份的角度乘以获得的时间
        Double secondAngle = rr * startSecond;
        Double minuteAngle = rr * startMinute;
        Double hourAngle = rr2 * startHour;

        //③利用三角函数计算分别计算出,时分秒三针所在的坐标点,坐标原点默认在手机屏幕左上角
        float sencondScale = 5 * arcRa / 6;//秒针长度
        float minuteScale = 3 * arcRa / 4;//分针长度
        float hourScale = arcRa / 2;//时针长度
        secondStartPoint.x = (float) (arcRa + sencondScale * Math.sin(secondAngle));
        secondStartPoint.y = (float) (arcRa - sencondScale * Math.cos(secondAngle));
        minuteStartPoint.x = (float) (arcRa + minuteScale * Math.sin(minuteAngle));
        minuteStartPoint.y = (float) (arcRa - minuteScale * Math.cos(minuteAngle));
        hourStartPoint.x = (float) (arcRa + hourScale * Math.sin(hourAngle));
        hourStartPoint.y = (float) (arcRa - hourScale * Math.cos(hourAngle));

        //④画圆,通过获取宽高算出最短一边作为直径，坐标原点默认在手机屏幕左上角
        canvas.drawCircle(arcRa, arcRa, arcRa, paint);

        //⑤围绕圆形绘制刻度,坐标原点默认在手机屏幕左上角
        int itime = 12;//长的刻度要显示的数字，这里从12点刻度开始顺时针绘制
        for (int i = 0; i < 60; i++) {///2π圆形分成60份,一秒钟与一分钟,所以要绘制60次,这里是从0到59
            float x1, y1, x2, y2;//刻度的两端的坐标即起始于结束的坐标
            float scale;//每个刻度离圆心的最近端坐标点到圆心的距离
            Double du = rr * i;//当前所占的角度
            Double sinx = Math.sin(du);//该角度的sin值
            Double cosy = Math.cos(du);//该角度的cos值
            x1 = (float) (arcRa + arcRa * sinx);//以默认坐标系通过三角函数算出刻度离圆心最远的端点的x轴坐标
            y1 = (float) (arcRa - arcRa * cosy);//以默认坐标系通过三角函数算出刻度离圆心最远的端点的y轴坐标
            if (i % 5 == 0) {//筛选刻度长度
                scale = 5 * arcRa / 6;//长刻度绘制,刻度离圆心的最近端坐标点到圆心的距离,这里取半径的五分之六的长度,可以通过情况来定

                //绘制长刻度上的数字1~12
                String number = itime + "";//当前数字变为String类型
                itime++;//数字加1
                if (itime > 12) {//如果大于数字12,重置为1
                    itime = 1;
                }
                float numScale = 4 * arcRa / 5;//数字离圆心的距离,这里取半径的五分之四的长度,可以通过情况来定
                float x3 = (float) (arcRa + numScale * sinx);//以默认坐标系通过三角函数算出x轴坐标
                float y3 = (float) (arcRa - numScale * cosy);//以默认坐标系通过三角函数算出x轴坐标
                paint.getTextBounds(number, 0, number.length(), textBound);//获取每个数字被全部包裹的最小的矩形边框数值

                //绘制数字,通过x3,y3根据文字最小包裹矩形边框数值进行绘制点调整
                canvas.drawText(number, x3 - textBound.width() / 2, y3 + textBound.height() / 2, paint);

            } else {
                scale = 9 * arcRa / 10;//短刻度绘制,这里取半径的十分之六九的长度,可以通过情况来定
            }
            x2 = (float) (arcRa + scale * sinx);//以默认坐标系通过三角函数算出该刻度离圆心最近的端点的x轴坐标
            y2 = (float) (arcRa - scale * cosy);//以默认坐标系通过三角函数算出该刻度离圆心最近的端点的y轴坐标
            canvas.drawLine(x1, y1, x2, y2, paint);//通过两端点绘制刻度
        }

        //⑥绘制时、分、秒针,坐标原点默认在手机屏幕左上角
        canvas.drawLine(arcRa, arcRa, secondStartPoint.x, secondStartPoint.y, paint);
        canvas.drawLine(arcRa, arcRa, minuteStartPoint.x, minuteStartPoint.y, paint);
        canvas.drawLine(arcRa, arcRa, hourStartPoint.x, hourStartPoint.y, paint);

        postInvalidateDelayed(1000);//每秒刷新一次
    }

    private void getCurrentTime() {
        long time = System.currentTimeMillis();//获取时间
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);
        startHour = mCalendar.get(Calendar.HOUR);//获取小时,12小时制
        startMinute = mCalendar.get(Calendar.MINUTE);//获取分钟
        startSecond = mCalendar.get(Calendar.SECOND);//获取秒
    }
}
