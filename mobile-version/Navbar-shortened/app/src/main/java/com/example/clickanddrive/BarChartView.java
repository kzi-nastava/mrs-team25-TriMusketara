package com.example.clickanddrive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.example.clickanddrive.dtosample.responses.DailyStats;

import java.util.List;

public class BarChartView extends View {

    private List<DailyStats> data;

    private static final int BAR_COLOR = 0xFFF5CB5C;
    private static final int AXIS_COLOR = 0x40E8EDDF;
    private static final int LABEL_COLOR = 0xAAE8EDDF;
    private static final int VALUE_COLOR = 0xFFE8EDDF;

    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);


    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint.setColor(BAR_COLOR);
        barPaint.setStyle(Paint.Style.FILL);

        axisPaint.setColor(AXIS_COLOR);
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeWidth(2f);

        labelPaint.setColor(LABEL_COLOR);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        valuePaint.setColor(VALUE_COLOR);
        valuePaint.setTypeface(Typeface.DEFAULT_BOLD);
        valuePaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setData(List<DailyStats> data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float density = getResources().getDisplayMetrics().scaledDensity;
        labelPaint.setTextSize(10 * density);
        valuePaint.setTextSize(9 * density);

        if (data == null || data.isEmpty()) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(LABEL_COLOR);
            p.setTextSize(13 * density);
            p.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("No data for selected period", getWidth() / 2f, getHeight() / 2f, p);

            return;
        }

        int w = getWidth();
        int h = getHeight();
        float dp = getResources().getDisplayMetrics().density;

        float padL = 8 * dp;
        float padR = 8 * dp;
        float padTop = 20 * dp;
        float padBot = 28 * dp;

        float chartW = w - padL - padR;
        float chartH = h - padTop - padBot;

        canvas.drawLine(padL, padTop, padL, padTop + chartH, axisPaint);
        canvas.drawLine(padL, padTop + chartH, padL + chartW, padTop + chartH, axisPaint);

        int count = data.size();

        int maxRides = 1;
        for (DailyStats d : data) {
            if (d.getNumberOfRides() > maxRides) maxRides = d.getNumberOfRides();
        }

        float totalGap = chartW * 0.3f;
        float barW     = (chartW - totalGap) / count;
        float gap      = totalGap / (count + 1);

        for (int i = 0; i < count; i++) {
            DailyStats day = data.get(i);
            int rides = day.getNumberOfRides();

            float barH = (rides / (float) maxRides) * chartH;
            if (barH < 2 * dp && rides > 0) barH = 2 * dp;

            float left   = padL + gap + i * (barW + gap);
            float right  = left + barW;
            float top    = padTop + chartH - barH;
            float bottom = padTop + chartH;

            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rect, 3 * dp, 3 * dp, barPaint);

            if (rides > 0) {
                canvas.drawText(String.valueOf(rides), left + barW / 2f, top - 3 * dp, valuePaint);
            }

            int step = Math.max(1, count / 7);
            if (i % step == 0 || i == count - 1) {
                String label = formatLabel(day.getDate());
                canvas.drawText(label, left + barW / 2f, padTop + chartH + 16 * dp, labelPaint);
            }
        }

    }

    private String formatLabel(String isoDate) {
        if (isoDate == null || isoDate.length() < 10) return "";
        try {
            String[] parts = isoDate.split("-");
            return Integer.parseInt(parts[2]) + "." + Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return isoDate.length() >= 7 ? isoDate.substring(5) : isoDate;
        }
    }
}
