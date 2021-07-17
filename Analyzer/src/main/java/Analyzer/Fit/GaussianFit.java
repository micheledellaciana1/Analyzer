package Analyzer.Fit;

import java.util.ArrayList;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.jfree.data.function.Function2D;

public class GaussianFit extends Fit {

    static public GaussianFit instance = new GaussianFit();

    protected GaussianFit() {
        super("GaussianFit");
    }

    @Override
    protected FittedResults fitFunction(ArrayList<WeightedObservedPoint> points, String arg) {

        double parameters[] = GaussianCurveFitter.create().fit(points);
        FittedResults result = new FittedResults();
        result.RegressionParameters = parameters;
        result.ParametersNames = new String[] { "norm", "mean", "sigma" };
        final Gaussian fittedFunction = new Gaussian(parameters[0], parameters[1], parameters[2]);
        result.RegressionFunction = new Function2D() {
            @Override
            public double getValue(double x) {
                return fittedFunction.value(x);
            }
        };

        return result;
    }
}
