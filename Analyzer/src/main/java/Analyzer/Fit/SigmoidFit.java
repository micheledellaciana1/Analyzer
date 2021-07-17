package Analyzer.Fit;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.analysis.function.Sigmoid;
import org.apache.commons.math3.fitting.SimpleCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.jfree.data.function.Function2D;

public class SigmoidFit extends Fit {
    static public SigmoidFit instance = new SigmoidFit();

    protected SigmoidFit() {
        super("SigmoidFit");
    }

    @Override
    protected FittedResults fitFunction(ArrayList<WeightedObservedPoint> points, String arg) {

        final Sigmoid.Parametric sigmoid = new Sigmoid.Parametric();

        double initialPars[] = new double[] { 0, 0 };

        SimpleCurveFitter curve = SimpleCurveFitter.create(new ParametricUnivariateFunction() {
            @Override
            public double value(double x, double... parameters) {
                return sigmoid.value(x, parameters);
            }

            @Override
            public double[] gradient(double x, double... parameters) {
                return sigmoid.gradient(x, parameters);
            }

        }, ArrayUtils.clone(initialPars));

        final FittedResults result = new FittedResults();
        result.RegressionParameters = curve.withMaxIterations(100).fit(points);
        result.ParametersNames = new String[] { "Lower asymptote", "higher asymptote" };

        result.RegressionFunction = new Function2D() {
            @Override
            public double getValue(double x) {
                return sigmoid.value(x, result.RegressionParameters);
            }
        };

        return result;
    }
}
