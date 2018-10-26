package com.paozhuanyinyu.runtime.permission.dialog;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;


public class JustifyTextView extends TextView {

    private int mLineY;
    private int mViewWidth;

    public JustifyTextView(Context context) {
        super(context);
    }

    public JustifyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        // 返回绘制状态的资源ID数组表示视图的当前状态
        paint.drawableState = getDrawableState();
        // 对View上的内容进行测量后得到的View内容占据的宽度
        // 前提是你必须在父布局的onLayout()方法或者此View的onDraw()方法里调用measure(0,0);
        // 否则你得到的结果和getWidth()得到的结果一样。
        mViewWidth = getMeasuredWidth();
        // 获取文本
        String text = getText().toString();
        mLineY = 0;
        mLineY += getTextSize();
        // 获取用于显示当前文本的布局
        Layout layout = getLayout();

        if (layout == null) {
            return;
        }

        Paint.FontMetrics fm = paint.getFontMetrics();

        int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
        textHeight = (int) (textHeight * layout.getSpacingMultiplier() + layout.getSpacingAdd())-2;

        for (int i = 0; i < layout.getLineCount(); i++) {
            // 返回文本中的指定行开头的偏移
            int lineStart = layout.getLineStart(i);
            // 返回文本中的指定行最后一个字符的偏移
            int lineEnd = layout.getLineEnd(i);
            float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
            String line = text.substring(lineStart, lineEnd);

            if ("".equals(line)) {
                break;
            }

            if (i < layout.getLineCount() - 1) {
                if (needScale(line)) {
                    drawScaledText(canvas, lineStart, line, width);
                } else {
//                    canvas.drawText(line.replace("%", ""), 0, mLineY, paint);
                    canvas.drawText(line, 0, mLineY, paint);
                }
            } else {
                canvas.drawText(line, 0, mLineY, paint);
            }

            // 增加行高
            mLineY += textHeight;
        }
    }

    private void drawScaledText(Canvas canvas, int lineStart, String line,
                                float lineWidth) {
        float x = 0;
        if (isFirstLineOfParagraph(lineStart, line)) {
            String blanks = "  ";
            canvas.drawText(blanks, x, mLineY, getPaint());
            float bw = StaticLayout.getDesiredWidth(blanks, getPaint());
            x += bw;

            line = line.substring(3);
        }

        int gapCount = line.length() - 1;
        int i = 0;
        if (line.length() > 2 && line.charAt(0) == 12288
                && line.charAt(1) == 12288) {
            String substring = line.substring(0, 2);
            float cw = StaticLayout.getDesiredWidth(substring, getPaint());
            canvas.drawText(substring, x, mLineY, getPaint());
            x += cw;
            i += 2;
        }

        float d = (mViewWidth - lineWidth) / gapCount;
        for (; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            float cw = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, x, mLineY, getPaint());
            x += cw + d;
        }
    }

    private boolean isFirstLineOfParagraph(int lineStart, String line) {
        return line.length() > 3 && line.charAt(0) == ' ' && line.charAt(1) == ' ';
    }

    private boolean needScale(String line) {
        if (line.length() == 0) {
            return false;
        } else {
//            return line.charAt(line.length() - 1) != '%';
            return line.charAt(line.length() - 1) != '\n';
        }
    }

}