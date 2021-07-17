package Analyzer.Transformation.Filters;

import org.jfree.data.xy.XYSeries;

import Analyzer.Transformation.Transformation;

import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class BoxMean_2 extends Transformation {

    public BoxMean_2() {
        super("BoxMean 2");
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
            SimpleRegression RO = new SimpleRegression(true);
            double sumX = 0;
            double sumY = 0;
            int count = 0;
            for (int j = -halfBox; j <= halfBox; j++) {
                int t = i + j;
                if (t < series.getItemCount() && t >= 0) {
                    RO.addData(series.getX(t).doubleValue(), series.getY(t).doubleValue());
                    sumX += series.getX(t).doubleValue();
                    sumY += series.getY(t).doubleValue();
                    count++;
                }
            }

            if (count == 0) {
                transformed.add(series.getX(i), series.getY(i));
            } else if (count < 3) {
                transformed.add(sumX / count, sumY / count);
            } else {
                RegressionResults rr = RO.regress();
                double X = sumX / count;
                double Y = rr.getParameterEstimate(0) + rr.getParameterEstimate(1) * X;
                transformed.add(X, Y);
            }
        }

        return transformed;
    }
}
