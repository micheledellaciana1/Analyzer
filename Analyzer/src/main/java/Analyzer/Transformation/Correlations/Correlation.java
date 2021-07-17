package Analyzer.Transformation.Correlations;

import java.util.List;

import org.jfree.data.xy.XYSeries;

public abstract class Correlation {
    protected String _name;

    public Correlation(String name) {
        _name = name;
    }

    public abstract XYSeries getCorrelated(XYSeries target, List<XYSeries> source, String arg);

    public String get_name() {
        return _name;
    }

    protected double getYBestExtimation(XYSeries series, double x) {
        int nearest = 0;
        double shortestDistance = Double.MAX_VALUE;

        for (int i = 0; i < series.getItemCount(); i++) {
            double distance = Math.abs(series.getX(i).doubleValue() - x);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearest = i;
            }
        }

        return series.getY(nearest).doubleValue();
    }
}
