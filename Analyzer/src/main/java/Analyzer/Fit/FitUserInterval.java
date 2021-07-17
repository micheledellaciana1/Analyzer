package Analyzer.Fit;

import java.util.ArrayList;

import org.apache.commons.math3.fitting.WeightedObservedPoint;

public class FitUserInterval extends Fit {

    static public FitUserInterval instance = new FitUserInterval();

    protected FitUserInterval() {
        super("FitUserInterval");
    }

    @Override
    protected FittedResults fitFunction(ArrayList<WeightedObservedPoint> points, String arg) {
        try {
            String limits[] = arg.split(" ");
            ArrayList<WeightedObservedPoint> userPoints = new ArrayList<WeightedObservedPoint>();

            double xvalues[] = new double[points.size()];
            for (int j = 0; j < points.size(); j++)
                xvalues[j] = points.get(j).getX();

            for (int i = 0; i < limits.length - 1; i += 2) {
                double x1 = Double.valueOf(limits[i]);
                double x2 = Double.valueOf(limits[i + 1]);
                for (int j = 0; j < xvalues.length; j++) {
                    if (xvalues[j] >= x1 && xvalues[j] < x2)
                        userPoints.add(new WeightedObservedPoint(1, points.get(j).getX(), points.get(j).getY()));
                }
            }

            PolynomialFit polyFitter = new PolynomialFit();
            int polyOrder = userPoints.size() / 3;
            polyOrder = polyOrder < 1 ? 1 : polyOrder;
            polyOrder = polyOrder > 3 ? 3 : polyOrder;

            return polyFitter.fitFunction(userPoints, Integer.toString(polyOrder));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
