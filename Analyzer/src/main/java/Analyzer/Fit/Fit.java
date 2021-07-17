package Analyzer.Fit;

import java.util.ArrayList;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.jfree.data.function.Function2D;
import org.jfree.data.xy.XYSeries;

public abstract class Fit {
    protected String _name;

    public Fit(String name) {
        _name = name;
    }

    public String get_name() {
        return _name;
    }

    protected abstract FittedResults fitFunction(ArrayList<WeightedObservedPoint> points, String arg);

    public FittedResults fit(XYSeries serie, String arg, double xMin, double xMax, double yMin, double yMax) {
        ArrayList<WeightedObservedPoint> points = getFitPoint(serie, xMin, xMax, yMin, yMax);
        if (points.size() == 0)
            return null;
        FittedResults result = fitFunction(points, arg);
        if (result == null)
            return null;

        result.createRegressionCurve(serie.getKey(), xMin, xMax, 1000);
        return result;
    }

    private static XYSeries sampleFunction2D(Function2D f, double Xmin, double Xmax, int sample, Comparable<?> key) {
        XYSeries serie = new XYSeries(key, false);
        double dx = (Xmax - Xmin) / sample;

        for (double x = Xmin; x < Xmax; x += dx) {
            serie.add(x, f.getValue(x));
        }

        return serie;
    }

    private ArrayList<WeightedObservedPoint> getFitPoint(XYSeries serie, double xMin, double xMax, double yMin,
            double yMax) {
        ArrayList<WeightedObservedPoint> points = new ArrayList<WeightedObservedPoint>();
        for (int i = 0; i < serie.getItemCount(); i++)
            if (serie.getX(i).doubleValue() >= xMin && serie.getX(i).doubleValue() <= xMax)
                if (serie.getY(i).doubleValue() >= yMin && serie.getY(i).doubleValue() <= yMax)
                    points.add(new WeightedObservedPoint(1, serie.getX(i).doubleValue(), serie.getY(i).doubleValue()));

        return points;
    }

    public class FittedResults {
        public XYSeries RegressionCurve;
        public Function2D RegressionFunction;
        public double RegressionParameters[];
        public String ParametersNames[];

        void createRegressionCurve(Comparable<?> key, double minX, double maxX, int sample) {
            double XSize = maxX - minX;
            RegressionCurve = sampleFunction2D(RegressionFunction, minX - XSize * 0.1, maxX + XSize * 0.1, sample,
                    "fit_" + key);
        }
    }
}
