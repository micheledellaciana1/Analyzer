package Analyzer.Transformation.Filters;

import org.jfree.data.xy.XYSeries;

import Analyzer.Transformation.Transformation;

public class GaussianMean extends Transformation {

    public GaussianMean() {
        super("Gaussian Mean");
    }

    @Override
    public XYSeries getTransformed(XYSeries series, String arg) {
        double std;

        try {
            std = Double.valueOf(arg) / 2;
        } catch (Exception e) {
            throw e;
        }

        XYSeries transformed = new XYSeries(series.getKey(), false);
        for (int i = 0; i < series.getItemCount(); i++) {

            double sumX = 0;
            double sumY = 0;
            double normalization = 0;

            for (int j = (int) (-std * 5); j <= std * 5; j++) {
                int t = i + j;
                if (t < series.getItemCount() && t >= 0) {
                    double gaussianValue = Math.exp(-Math.pow(j / std, 2) * 0.5);
                    sumX += series.getX(t).doubleValue() * gaussianValue;
                    sumY += series.getY(t).doubleValue() * gaussianValue;
                    normalization += gaussianValue;
                }
            }

            transformed.add(sumX / normalization, sumY / normalization);
        }

        return transformed;
    }
}
