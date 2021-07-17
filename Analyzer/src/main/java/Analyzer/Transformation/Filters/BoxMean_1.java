package Analyzer.Transformation.Filters;

import org.jfree.data.xy.XYSeries;

import Analyzer.Transformation.Transformation;

public class BoxMean_1 extends Transformation {

    public BoxMean_1() {
        super("BoxMean 1");
    }

    @Override
    public XYSeries getTransformed(XYSeries series, String arg) {
        int halfBox;

        try {
            halfBox = Integer.valueOf(arg) / 2;
        } catch (Exception e) {
            throw e;
        }

        XYSeries transformed = new XYSeries(series.getKey(), false);
        for (int i = 0; i < series.getItemCount(); i++) {
            double sumX = 0;
            double sumY = 0;
            int count = 0;

            for (int j = -halfBox; j <= halfBox; j++) {
                int t = i + j;
                if (t < series.getItemCount() && t >= 0) {
                    sumX += series.getX(t).doubleValue();
                    sumY += series.getY(t).doubleValue();
                    count++;
                }
            }

            if (count > 0)
                transformed.add(sumX / count, sumY / count);
            else
                transformed.add(series.getX(i), series.getY(i));
        }

        return transformed;
    }

}
