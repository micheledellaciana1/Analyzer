package Analyzer.Fit;

import java.util.ArrayList;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.data.function.Function2D;

public class LinearFit extends Fit {

    static public LinearFit instance = new LinearFit();

    protected LinearFit() {
        super("Linear");
    }

    @Override
    protected FittedResults fitFunction(ArrayList<WeightedObservedPoint> points, String arg) {

        SimpleRegression RO = new SimpleRegression(true);
        for (int i = 0; i < points.size(); i++)
            RO.addData(points.get(i).getX(), points.get(i).getY());

        final RegressionResults rr = RO.regress();

        FittedResults result = new FittedResults();

        result.RegressionParameters = new double[7];
        result.RegressionParameters[0] = rr.getParameterEstimate(0);
        result.RegressionParameters[1] = rr.getParameterEstimate(1);
        result.RegressionParameters[2] = rr.getRSquared();
        result.RegressionParameters[3] = rr.getStdErrorOfEstimate(0);
        result.RegressionParameters[4] = rr.getStdErrorOfEstimate(1);
        result.RegressionParameters[5] = 1. / rr.getParameterEstimate(1);
        result.RegressionParameters[6] = 1. / rr.getParameterEstimate(1)
                * (rr.getStdErrorOfEstimate(1) / rr.getParameterEstimate(1));

        result.ParametersNames = new String[] { "Intercept", "slope", "Rsquare", "Error intercept", "Error slope",
                "1/slope", "Error 1/slope" };

        result.RegressionFunction = new Function2D() {
            @Override
            public double getValue(double x) {
                return rr.getParameterEstimate(0) + rr.getParameterEstimate(1) * x;
            }
        };

        return result;
    }
}
